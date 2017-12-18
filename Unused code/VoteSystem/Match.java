package edu.chalmers.matchServer.matchLogic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.matchServer.MatchServer;
import edu.chalmers.matchServer.selecter.ShipSelecter;
import edu.chalmers.nimby.model.Entity;
import edu.chalmers.nimby.model.Equipment;
import edu.chalmers.nimby.model.IdentifiableEntity;
import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet3PlayerCommand;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet6AvailableShipsToVoteOn;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet7AvailableShipsToSitIn;
import edu.chalmers.nimby.network.match.PlayerConnection;
import edu.chalmers.nimby.network.match.PlayerInfo;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Pipe;

/**
 * Contains a match.
 * @author Adam Grönberg
 *
 */
public class Match implements Runnable {
	
	private int uniqueIDCounter = 1;
	private World world;
	private int matchID;
	private static final int TARGET_FPS = 60;
//	private static final long SLEEP_TIME = 1;
	
	private static final float STEP_TIME = 1 / TARGET_FPS;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;

	private MatchServer matchServer;
	
	private List<Entity> entities;
	private List<IdentifiableEntity> identifiableEntities;
	
	private HashSet<PlayerConnection> playersInGame;
	
	private Team teamOne;
	private Team teamTwo;

	private Pipe<Packet3PlayerCommand> commandPipe;
	
	private PlayersReadyMonitor playersReady;
	
	private ShipSelecter teamOneSelecter;
	private ShipSelecter teamTwoSelecter;
	
	/**
	 * Constructor.
	 * @param matchID the ID of the match
	 * @param playersToConnect 
	 * @param matchServer 
	 */
	public Match(final int matchID, final HashSet<PlayerInfo> playersToConnect, final MatchServer matchServer) {
		this.matchID = matchID;
		this.matchServer = matchServer;
		playersReady = new PlayersReadyMonitor(playersToConnect);
		
		entities = new LinkedList<Entity>();
		identifiableEntities = new LinkedList<IdentifiableEntity>();
		playersInGame = new HashSet<PlayerConnection>();
		commandPipe = new Pipe<Packet3PlayerCommand>();
		world = new World(Vector2.Zero, true);
		
		Log.info("[Match " + matchID + "] Players to connect: ");
		for (PlayerInfo player : playersToConnect) {
			Log.info("[Match " + matchID + "] " + player.getUserName() + " " + player.getToken());
		}
	}
	
	@Override
	public final void run() {
		
		try {
			Log.info("[Match " + matchID + "] Waiting for players to connect...");
			playersReady.waitForPlayersReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Splits into two teams
		teamOne = new Team();
		teamTwo = new Team();
		createTeams();
		
		teamOneSelecter = new ShipSelecter();
		HashSet<PlayerShip> teamOneShips = ShipSelecter.createShipPool(teamOne.getPlayers());
		Log.info("[Match " + matchID + "] Team one ships to choose from :");
		for (PlayerShip ship: teamOneShips) {
			Log.info("[Match " + matchID + "] " + ship.getName());
		}
		
		teamTwoSelecter = new ShipSelecter();
		HashSet<PlayerShip> teamTwoShips = ShipSelecter.createShipPool(teamTwo.getPlayers());
		Log.info("[Match " + matchID + "] Team two ships to choose from :");
		for (PlayerShip name: teamTwoShips) {
			Log.info("[Match " + matchID + "] " + name);
		}
		
		Packet6AvailableShipsToVoteOn shipsToVoteOn = new Packet6AvailableShipsToVoteOn();
		shipsToVoteOn.ships = teamOneShips;
		matchServer.sendToList(shipsToVoteOn, teamOne.getPlayers());
		shipsToVoteOn.ships = teamTwoShips;
		matchServer.sendToList(shipsToVoteOn, teamTwo.getPlayers());
		
		playersReady = new PlayersReadyMonitor(playersInGame);
		
		try {
			Log.info("[Match " + matchID + "] Waiting for players to vote...");
			playersReady.waitForPlayersReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Packet7AvailableShipsToSitIn shipsToSitIn = new Packet7AvailableShipsToSitIn();
		shipsToSitIn.ships = teamOneSelecter.selectShips(teamOne.getPlayers().size());
		matchServer.sendToList(shipsToSitIn, teamOne.getPlayers());
		shipsToSitIn.ships = teamTwoSelecter.selectShips(teamTwo.getPlayers().size());
		matchServer.sendToList(shipsToSitIn, teamTwo.getPlayers());
		
		playersReady = new PlayersReadyMonitor(playersInGame);
		
		try {
			Log.info("[Match " + matchID + "] Waiting for players to sit...");
			playersReady.waitForPlayersReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//TODO: Create the ships
		
		Log.info("[Match " + matchID + "] Match started!");
		
		//tempCreate4Ships();

		float lastLoopTime = System.nanoTime();
		while (true) {
			float delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			update(delta);
//			try {
//				Thread.sleep(SLEEP_TIME);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}

	/**
	 * Adds the player to playersInGame. Removes the player from playerNotReady.
	 * @param player The player
	 */
	public final void addPlayerToGame(final PlayerConnection player) {
		playersReady.setPlayerReady(player.getUserName());
		playersInGame.add(player);
		Log.info("[Match " + matchID + "] Player: " + player.getUserName() + " has connected. Players left " + playersReady.getNumberOfPlayersLeft());
		playersReady.checkPlayersReady();
	}
	
	/**
	 * Adds the players votes to the pool. Removes the player from playerNotReady.
	 * @param player The player
	 * @param votes the ships the player voted on
	 */
	public final void addPlayerVotes(final PlayerConnection player, final HashSet<PlayerShip> votes) {
		playersReady.setPlayerReady(player.getUserName());
		switch (player.getTeam()) {
		case 1:
			teamOneSelecter.addShipVotes(votes);
			break;
		case 2:
			teamTwoSelecter.addShipVotes(votes);
			break;
		
		default:
			Log.error("[Match " + matchID + "] WARNING: Player: " + player.getUserName() + " isn't in a team!");
			break;
		}
		Log.info("[Match " + matchID + "] Player: " + player.getUserName() + " has voted. Players left " + playersReady.getNumberOfPlayersLeft());
		playersReady.checkPlayersReady();
	}
	
	/**
	 * Controls that the player should be in the game he requests to join.
	 * @param userName The player name
	 * @return	true if he's a-okay
	 */
	public final boolean playerValidForMatch(final String userName) {
		return playersReady.playerContains(userName);
	}

	/**
	 * Returns a unique ID for this match.
	 * @return the unique ID
	 */
	public final int getUniqueIDCounter() {
		return uniqueIDCounter++;
	}

	/**
	 * Adds the entities to the update cycle.
	 * @param entities the entities to add to the update cycle
	 */
	public final void addEntity(final Entity ... entities) {
		for (Entity entity: entities) {
			this.entities.add(entity);
			if (entity instanceof IdentifiableEntity) {
				IdentifiableEntity idEnt = (IdentifiableEntity) entity;
				identifiableEntities.add(idEnt);
			}
		}
	}

	/**
	 * Updates all entities.
	 * @param delta time between calls
	 */
	public final void update(final float delta) {

		if (world != null) {
			world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}

		Iterator<Entity> itr = entities.iterator();
		while (itr.hasNext()) {
			Entity e = itr.next();
			// ALLA VET ATT DET ÄR NULL HÄR, INGEN FREDAGSFIKA!!!
			e.update(delta, null);
		}
		sendSnapshot();
	}
	
	/**
	 * Sends the match current status to all game clients.
	 */
	private void sendSnapshot() {
		
		
	}

	/**
	 * Returns the entity with the unique ID.
	 * @param id The ID to find
	 */
	public final void activateIdentifyEntities(final UniqueIdentifier id) {
		for (IdentifiableEntity entity : identifiableEntities) {
			if (entity.getUniqueID().equals(id)) {
				if (entity instanceof Equipment) {
					Equipment equipment = (Equipment) entity;
					equipment.activateEquipment();
				}
			}
		}
	}

	/**
	 * The input the world needs to control and handle.
	 * @param command command from player
	 */
	public final void giveCommand(final Packet3PlayerCommand command) {
		commandPipe.put(command);
	}
	
	/**
	 * Splits the players into two teams.
	 */
	public final void createTeams() {
		Object[] players = playersInGame.toArray();
		System.out.println("" + players.length);
		
		for (int i = 0; i < players.length / 2; i++) {
			PlayerConnection player = (PlayerConnection) players[i];
			player.setTeam(1);
			teamOne.addPlayer(player);
			Log.info("[Match " + matchID + "] " + player.getUserName() + " has joined teamOne");
		}
		
		for (int i = players.length / 2; i < players.length; i++) {
			PlayerConnection player = (PlayerConnection) players[i];
			player.setTeam(2);
			teamTwo.addPlayer(player);
			Log.info("[Match " + matchID + "] " + player.getUserName() + " has joined teamTwo");
		}
	}
}
