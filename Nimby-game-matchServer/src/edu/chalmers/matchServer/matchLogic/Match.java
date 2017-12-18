package edu.chalmers.matchServer.matchLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.matchServer.MatchServer;
import edu.chalmers.matchServer.PlayerCommand;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.factory.PartBuilder;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet2InitialisedShips;
import edu.chalmers.nimby.network.match.Player;
import edu.chalmers.nimby.network.match.PlayerConnection;
import edu.chalmers.nimby.network.match.PlayerInfo;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Pipe;

/**
 * Contains a match.
 * @author Adam Gr�nberg
 *
 */
public final class Match implements Runnable {
	
	private static final int SEPERATEION = 50;
	private static final int TEAM_START_Y = 75;
	private static final int TEAM_ONE_STARTX = 50;
	private static final int TEAM_TWO_STARTX = 150;
	
	private static final int SLEEP_TIME = 16666667;		//Time in nanoseconds between world steps.
	private static final long SEND_SNAPSHOT_TIME = 333;	//Time in milliseconds between snapshots.
	
	private int teamOneXPos = TEAM_ONE_STARTX;
	private int teamTwoXPos = TEAM_TWO_STARTX;
	
	private int teamOneYPos = TEAM_START_Y;
	private int teamTwoYPos = TEAM_START_Y;
	
	private int uniquePlayerIDCounter = 1;
	private int uniquePartIDCounter = 1;
	
	private final MatchServer matchServer;
	private final MatchLogic matchLogic;
	private final PlayersInGame playersInGame;
	

	private final int matchID;
	
	private final Pipe<PlayerCommand> commandPipe;
	private PlayersReadyMonitor playersReady;
	
	/**
	 * Constructor.
	 * @param matchID the ID of the match
	 * @param playersToConnect 
	 * @param matchServer 
	 */
	public Match(final int matchID, final HashSet<PlayerInfo> playersToConnect, final MatchServer matchServer) {
		this.matchID = matchID;
		this.matchServer = matchServer;
		
		playersInGame = new PlayersInGame();
		commandPipe = new Pipe<PlayerCommand>();
		matchLogic = new MatchLogic(this);
		
		HashSet<Player> playersNotReady = new HashSet<>();
		for (PlayerInfo player: playersToConnect) {
			PlayerInfo notReadyPlayer = new PlayerInfo();
			notReadyPlayer.setUserName(player.getUserName());
			notReadyPlayer.setToken(player.getToken());
			playersNotReady.add(notReadyPlayer);
		}
		
		playersReady = new PlayersReadyMonitor(playersNotReady);
		
		Log.info("[Match " + matchID + "] Players to connect: ");
		for (PlayerInfo player : playersToConnect) {
			Log.info("[Match " + matchID + "] " + player.getUserName() + " " + player.getToken());
		}
	}
	
	@Override
	public void run() {
		try {
			Log.info("[Match " + matchID + "] Waiting for players to connect...");
			playersReady.waitForPlayersReady();

			createTeams();

			List<byte[]> initedShips = initializeShipsAndSendThemToClients();

			waitForShipsToBeCreatedOnClients();

			matchLogic.createShips(initedShips);
			
			matchLoop();
		} catch (InterruptedException | UnsupportedEncodingException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes ships and creates a blob also sends them to clients.
	 * @return byte[] blob with all ships.
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * @throws InterruptedException 
	 */
	private List<byte[]> initializeShipsAndSendThemToClients() throws UnsupportedEncodingException, SQLException, InterruptedException {
		List<byte[]> shipBlobs = createShipsIndexes();
		HashSet<PlayerConnection> players = playersInGame.getSnapshotOfPlayersIngame();
		int totalNumber = shipBlobs.size();
		for (byte[] blob : shipBlobs) {
			Packet2InitialisedShips initialisedShips = new Packet2InitialisedShips();
			initialisedShips.totalPackages = totalNumber;
			initialisedShips.ship = blob;
			matchServer.getPacketSender().sendToListTCP(initialisedShips, players);
		}
		return shipBlobs;
	}

	/**
	 * Waits for all ships to be created.
	 * @throws InterruptedException 
	 */
	private void waitForShipsToBeCreatedOnClients() throws InterruptedException {
		HashSet<Player> playersNotReady = new HashSet<>();
		for (PlayerConnection player: playersInGame.getSnapshotOfPlayersIngame()) {
			PlayerInfo notReadyPlayer = new PlayerInfo();
			notReadyPlayer.setUserName(player.getUserName());
			notReadyPlayer.setToken(player.getToken());
			playersNotReady.add(notReadyPlayer);
		}
		
		playersReady = new PlayersReadyMonitor(playersNotReady);		//Wait for players to build their ships
		Log.info("[Match " + matchID + "] Waiting for clients to receive ships...");
		playersReady.waitForPlayersReady();
	}

	/**
	 * The match loop. Handles update and sleepTimes.
	 * @throws InterruptedException 
	 */
	private void matchLoop() throws InterruptedException {
		Log.info("[Match " + matchID + "] Match started!");
		long lastLoopTime = System.currentTimeMillis();
		long lastSendTime = System.currentTimeMillis();
		while (true) {
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			if (commandPipe.hasNext()) {
				PlayerCommand playerCommand = commandPipe.pull();
				UniqueIdentifier activatedEntitiy = new UniqueIdentifier(playerCommand.activatedEntityID.userID, 
																			playerCommand.activatedEntityID.entityID);
				UniqueIdentifier entityToSpawn = playerCommand.spawnedEntityID;
				matchLogic.activateIdentifyEntities(activatedEntitiy, playerCommand.keyPress, entityToSpawn); 
			}
			
			matchLogic.update(delta);
			
			if (SEND_SNAPSHOT_TIME < System.currentTimeMillis() - lastSendTime) {
				lastSendTime = System.currentTimeMillis();
				
				matchServer.getPacketSender().sendSnapshot(matchLogic.getIdentifiableEntities(), playersInGame.getSnapshotOfPlayersIngame());
			}
			TimeUnit.NANOSECONDS.sleep(SLEEP_TIME);
		}
	}

	/**
	 * Creates a teams ships and puts the correct index on them.
	 * @return Player ships that are indexed and ready to build.
	 * @throws UnsupportedEncodingException	thrown if something is wrong in the incoming blob.
	 * @throws SQLException thrown if something is wrong in the incoming blob.
	 * @throws InterruptedException thrown when something went wrong with playersInGame.
	 */
	private List<byte[]> createShipsIndexes() throws UnsupportedEncodingException, SQLException, InterruptedException {
		World world = new World(Vector2.Zero, true);
		List<byte[]> playerShips = new LinkedList<>();
		for (PlayerConnection player : playersInGame.getSnapshotOfPlayersIngame()) {
			Part[] ship = PartBuilder.createSpaceShip(player.getShip().getShipBlob(), world);
			for (Part part : ship) {
				UniqueIdentifier uniqueIdentifier = new UniqueIdentifier();
				uniqueIdentifier.entityID = uniquePartIDCounter++;
				uniqueIdentifier.userID = player.getPlayerID();
				part.setUniquePartMatchID(uniqueIdentifier);
				if (part instanceof Equipment) {
					Equipment equipment = (Equipment) part;
					if (player.getTeam() == 1) {
						equipment.body.setTransform(teamOneXPos, teamOneYPos, equipment.body.getAngle());
					} else {
						equipment.body.setTransform(teamTwoXPos, teamTwoYPos, equipment.body.getAngle());
					}
				}
			}
			if (player.getTeam() == 1) {
				ship[0].body.setTransform(teamOneXPos, teamOneYPos, 0);
				teamOneYPos += SEPERATEION;
			} else {
				ship[0].body.setTransform(teamTwoXPos, teamTwoYPos, 0);
				teamTwoYPos += SEPERATEION;
			}
			playerShips.add(PartBuilder.createBlob(ship[0]));
		}
		return playerShips;
	}
	
	/**
	 * Controls that the player should be in the game he requests to join.
	 * @param userName The player name
	 * @return	true if he's a-okay
	 */
	public boolean playerValidForMatch(final String userName) {
		return playersReady.playerContains(userName);
	}

	/**
	 * Returns a unique ID for this match.
	 * @return the unique ID
	 */
	public int getUniquePlayerIDCounter() {
		return uniquePlayerIDCounter++;
	}

	/**
	 * The input the world needs to control and handle.
	 * @param playerCommand command from player
	 */
	public void giveCommand(final PlayerCommand playerCommand) {
		commandPipe.put(playerCommand);
	}
	
	public void addProjectileToSpawnQueue(final Vector2 position, final Vector2 velocity, final UniqueIdentifier uniqueProjectileID) {
		matchLogic.putProjectileOnBuildQueue(position, velocity, uniqueProjectileID);
	}
	
	/**
	 * Splits the players into two teams.
	 * @throws InterruptedException 
	 */
	private void createTeams() throws InterruptedException {
		Object[] players = playersInGame.getSnapshotOfPlayersIngame().toArray();
		
		for (int i = 0; i < players.length / 2; i++) {
			PlayerConnection player = (PlayerConnection) players[i];
			player.setTeam(1);
			Log.info("[Match " + matchID + "] " + player.getUserName() + " has joined teamOne");
		}
		
		for (int i = players.length / 2; i < players.length; i++) {
			PlayerConnection player = (PlayerConnection) players[i];
			player.setTeam(2);
			Log.info("[Match " + matchID + "] " + player.getUserName() + " has joined teamTwo");
		}
	}
	
	/**
	 * Sets the player building process ready.
	 * @param userName name of the player
	 */
	public void playerShipBuildReady(final String userName) {
		playersReady.setPlayerReady(userName);
		Log.info("[Match " + matchID + "] Player: " + userName + " has received his ship. Players left " + playersReady.getNumberOfPlayersLeft());
		playersReady.checkPlayersReady();
	}
	
	/**
	 * Adds the player to playersInGame. Removes the player from playerNotReady.
	 * @param player The player
	 */
	public void addPlayerToGame(final PlayerConnection player) {
		playersReady.setPlayerReady(player.getUserName());
		playersInGame.addPlayer(player);
		Log.info("[Match " + matchID + "] Player: " + player.getUserName() + " has connected. Players left " + playersReady.getNumberOfPlayersLeft());
		playersReady.checkPlayersReady();
	}
	
	/**
	 * Removes a player from the game.
	 * @param player the player to remove.
	 */
	public void removePlayerFromGame(final PlayerConnection player) {
		playersInGame.removePlayer(player);
		Log.info("[Match " + matchID + "] Player: " + player.getUserName() + " has disconnected from match.");
	}
	
	public void removeProjectilesFromClients(final List<UniqueIdentifier> idsToRemove) throws InterruptedException {
		matchServer.getPacketSender().sendProjectilesToRemove(idsToRemove, playersInGame.getSnapshotOfPlayersIngame());
	}
}
