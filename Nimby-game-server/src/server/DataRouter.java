package server;

import java.util.HashSet;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.lobby.NetworkConnection;


/**
 * This class is responsible of sending packets to the correct connections.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public class DataRouter {

	private LobbyPacketFactory lobbyServer;

	/**
	 * Constructor. Sets the lobbyServer.
	 * @param lobbyServer The local server
	 */
	public DataRouter(final LobbyPacketFactory lobbyServer) {
		this.lobbyServer = lobbyServer;
	}

	/**
	 * Broadcast message to all connections except self.
	 * @param message Message to send
	 * @param userID The user who sends ID
	 */
	public final void sendBroadcastMessage(final AbstractPacket message, final int userID) {
		lobbyServer.sendToAllExceptTCP(userID, message);
	}

	/**
	 * Sends a message to a specific connection.
	 * @param message message to send
	 * @param conn the connection
	 */
	public final void sendPrivateMessage(final AbstractPacket message, final NetworkConnection conn) {
		lobbyServer.sendToTCP(conn.getID(), message);
	}

	/**
	 * Sends a message to everyone on the list.
	 * @param message the message to send
	 * @param recivers the receivers
	 */
	public final void sendToList(final AbstractPacket message, final HashSet<NetworkConnection> recivers) {		
		for (NetworkConnection netConn: recivers) {
			sendPrivateMessage(message, netConn);
		}
	}
}
