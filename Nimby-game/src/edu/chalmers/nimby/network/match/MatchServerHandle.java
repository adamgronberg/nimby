package edu.chalmers.nimby.network.match;

import com.esotericsoftware.kryonet.Client;

import edu.chalmers.nimby.model.gameLogic.ProjectileCreator.ProjectileCreationInfo;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.network.lobby.ServerHandle;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet0ConnectToMatchRequest;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet3GameClientUpdate;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet5ClientShipBuildingReady;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet6GameClientProjectileSpawnRequest;


/**
 * Used to communicate with the match server.
 * @author Adam Gr�nberg
 *
 */
public final class MatchServerHandle extends ServerHandle {
	
	private static MatchServerHandle instance = null;
	private static int currentMatchID = 0;
	 
	/**
	 * Singleton. Use getInstance method.
	 */
	private MatchServerHandle() { }
 
	/**
	 * Gets instance.
	 * @return the instance
	 */
	public static synchronized MatchServerHandle getInstance() {
		if (instance == null) {
			instance = new MatchServerHandle();
		}
		return instance;
	}

	/**
	 * Sends a command request to match server.
	 * @param matchUniquePartID the ID of the part that activated.
	 * @param keyPress the type of key press
	 */
	public synchronized void sendCommandRequest(final int matchUniquePartID, final MatchNetwork.KeyPress keyPress) {
		Packet3GameClientUpdate command = new Packet3GameClientUpdate();
		command.matchUniquePartID = matchUniquePartID;
		command.keyPress = keyPress;
		sendUDPPacket(command);
	}
	
	/**
	 * Sends a command request to match server.
	 */
	public void sendConnectionRequest() {
		Packet0ConnectToMatchRequest request = new Packet0ConnectToMatchRequest();
		request.matchID = MatchServerHandle.currentMatchID;
		request.userName = LobbyServerHandle.getInstance().getUserName();
		request.ship = LobbyServerHandle.getInstance().getMatchShip();
		client.sendTCP(request);
	}

	@Override
	public void init() {
		client = new Client(MatchNetwork.WRITE_BUFFER_SIZE, MatchNetwork.OBJECT_BUFFER_SIZE);
		client.addListener(MatchServerListener.getInstance());
		MatchNetwork.register(client);
	}
	
	/**
	 * Gets the current matchID.
	 * @return	The current match ID
	 */
	public static int getCurrentMatchID() {
		return currentMatchID;
	}

	/**
	 * Set the current match ID counter.
	 * @param currentMatchID The current match ID
	 */
	public static void setCurrentMatchID(final int currentMatchID) {
		MatchServerHandle.currentMatchID = currentMatchID;
	}

	/**
	 * Sends a packet to the match server.
	 * @param packet packet to  send
	 */
	public void sendTCPPacket(final AbstractPacket packet) {
		client.sendTCP(packet);
	}
	
	/**
	 * Sends a packet to the match server.
	 * @param packet packet to  send
	 */
	public void sendUDPPacket(final AbstractPacket packet) {
		client.sendUDP(packet);
	}
	
	/**
	 * Sends a building status ok status to match server.
	 */
	public void sendShipBuildingReadyStatus() {
		sendTCPPacket(new Packet5ClientShipBuildingReady());
	}
	
	/**
	 * Sends a request to the server to spawn a projectile.
	 * @param projectileToSpawn
	 */
	public void sendProjectileSpawnRequest(final ProjectileCreationInfo projectileToSpawn) {
		Packet6GameClientProjectileSpawnRequest spawnRequest = new Packet6GameClientProjectileSpawnRequest();
		spawnRequest.position = projectileToSpawn.projectileInfo.position;
		spawnRequest.velocity = projectileToSpawn.projectileInfo.velocity;
		spawnRequest.uniqueProjectileID = projectileToSpawn.projectileID;
		sendUDPPacket(spawnRequest);
	}
	
	/**
	 * Disconnects the client from the game.
	 */
	public void disconnectFromMatchServer() {
		client.close();
	}
}
