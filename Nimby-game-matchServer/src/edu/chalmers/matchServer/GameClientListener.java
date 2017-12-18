package edu.chalmers.matchServer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.matchServer.matchLogic.Match;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet0ConnectToMatchRequest;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet1ConnectToMatchAnswer;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet3GameClientUpdate;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet5ClientShipBuildingReady;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet6GameClientProjectileSpawnRequest;
import edu.chalmers.nimby.network.match.PlayerConnection;
import edu.chalmers.nimby.network.match.UniqueIdentifier;

/**
 * Used to listen on game clients.
 * @author Adam Grönberg
 *
 */
public class GameClientListener extends Listener {

	private MatchServer matchServer;

	/**
	 * Constructor.
	 * @param matchServer the server
	 */
	public GameClientListener(final MatchServer matchServer) {
		this.matchServer = matchServer;
	}

	/**
	 * Called when a server connected with user.
	 * @param conn the server connected
	 */
	@Override
	public void connected(final Connection conn) { }

	/**
	 * Called when user has disconnected.
	 * @param conn the server that disconnected
	 */
	@Override
	public void disconnected(final Connection conn) { 
		if (conn instanceof PlayerConnection) {
			PlayerConnection player = (PlayerConnection) conn;
			if (player.getMatchID() != -1) {
				Match match = matchServer.getMatch(player.getMatchID());
				match.removePlayerFromGame(player);
			}
		}
	}

	/**
	 * Called when receiving a package from server.
	 * @param conn The server received from
	 * @param data	The data received
	 */
	@Override
	public final void received(final Connection conn, final Object data) {
		PlayerConnection playerConn = (PlayerConnection) conn;
		if (data instanceof Packet3GameClientUpdate) {
			Packet3GameClientUpdate command = (Packet3GameClientUpdate) data;
			handleCommands(playerConn, command);
		} else if (data instanceof Packet6GameClientProjectileSpawnRequest) {
			Packet6GameClientProjectileSpawnRequest projectileSpawnRequest = (Packet6GameClientProjectileSpawnRequest) data;
			handleProjectileSpawnRequest(playerConn, projectileSpawnRequest);
		} else if (data instanceof Packet0ConnectToMatchRequest) {
			Packet0ConnectToMatchRequest request = (Packet0ConnectToMatchRequest) data;
			handleConnectToMatchReqest(playerConn, request);
		} else if (data instanceof Packet5ClientShipBuildingReady) {
			Match match = matchServer.getMatch(playerConn.getMatchID());
			match.playerShipBuildReady(playerConn.getUserName());
		}
	}

	private void handleProjectileSpawnRequest(final PlayerConnection playerConn,
			final Packet6GameClientProjectileSpawnRequest projectileSpawnRequest) {
		if (projectileSpawnRequest.uniqueProjectileID == null) {
			Log.error("uniqueProjectileID was never set for the projectile recived from client. Can not create it");
			return;
		}
		Match match = matchServer.getMatch(playerConn.getMatchID());
		match.addProjectileToSpawnQueue(projectileSpawnRequest.position, projectileSpawnRequest.velocity, projectileSpawnRequest.uniqueProjectileID);
	}

	/**
	 * Handles player commands.
	 * @param playerConn The player that tries to connect	
	 * @param command The command given
	 */
	private void handleCommands(final PlayerConnection playerConn, final Packet3GameClientUpdate command) {
		Match match = matchServer.getMatch(playerConn.getMatchID());
		UniqueIdentifier activatedEntity = new UniqueIdentifier(playerConn.getPlayerID(), command.matchUniquePartID);
		PlayerCommand playerCommand = new PlayerCommand(activatedEntity, command.keyPress, command.spawnedEntityID);
		match.giveCommand(playerCommand);
	}

	/**
	 * Handles a player that tries to connect to a match.
	 * @param playerConn The player that tries to connect
	 * @param request The request
	 */
	private void handleConnectToMatchReqest(final PlayerConnection playerConn, final Packet0ConnectToMatchRequest request) {
		Match match = matchServer.getMatch(request.matchID);
		Packet1ConnectToMatchAnswer anwser = new Packet1ConnectToMatchAnswer();	
		if (match != null) {
			if (match.playerValidForMatch(request.userName)) {
				int uniquePlayerID = match.getUniquePlayerIDCounter();
				anwser.uniqueID = uniquePlayerID;
				//player.setToken(token);			TODO 
				playerConn.setMatchID(request.matchID);
				playerConn.setPlayerID(uniquePlayerID);
				playerConn.setUserName(request.userName);
				playerConn.setShip(request.ship);
				match.addPlayerToGame(playerConn);
			}
		} 
		matchServer.getPacketSender().sendToTCP(playerConn.getID(), anwser);
	}
}
