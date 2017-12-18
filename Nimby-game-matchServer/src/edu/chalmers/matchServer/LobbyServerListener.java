package edu.chalmers.matchServer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet8InitializeMatchServer;
/**
 * Used to get packets from the lobby server.
 * @author Adam Grönberg
 *
 */
public class LobbyServerListener extends Listener {
	
	private MatchServer matchServer;
	
	/**
	 * Constructor. Sets the connection handle.
	 * @param matchServer 
	 */
	public LobbyServerListener(final MatchServer matchServer) {
		this.matchServer = matchServer;
	}
	/**
	 * Called when a server connected with user.
	 * @param conn the server connected
	 */
	@Override
	public final void connected(final Connection conn) {
		matchServer.registerConnectionType();
	}

	/**
	 * Called when user has disconnected.
	 * @param conn the server that disconnected
	 */
	@Override
	public void disconnected(final Connection conn) { }

	/**
	 * Called when receiving a package from lobby server.
	 * @param conn The server received from
	 * @param data	The data received
	 */
	@Override
	public final void received(final Connection conn, final Object data) {
		if (data instanceof Packet8InitializeMatchServer) {
			Packet8InitializeMatchServer info = (Packet8InitializeMatchServer) data;
			matchServer.createMatch(info.matchID, info.players, conn);
			matchServer.sendMatchReadyStatusToLobby(info.matchID, conn);
		}
	}
}
