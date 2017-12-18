package edu.chalmers.nimby.network.lobby;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.controller.LobbyScreen;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet1LoginAccepted;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet2ErrorMessage;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet3Message;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet6InitializeGameClient;
import edu.chalmers.nimby.util.PipeIn;

/**
 * Monitors the network and routers the data.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public final class LobbyServerListener extends Listener {

	private static final long SLEEP_TIME = 100; //Sleeps 100ms to let the MatchScreen start.

	private PipeIn<String> chatPipe;

	private Nimby game;
	
	private static LobbyServerListener instance = null;

	/**
	 * Constructor.
	 */
	private LobbyServerListener() { }
	
	/**
	 * Returns the instance of this class if it has been initialized.
	 * @return the instance, null if the LobbyServerHandle hasn't been initialized
	 */
	public static synchronized LobbyServerListener getInstance() {
		if (instance == null) {
			instance = new LobbyServerListener();
		} 
		return instance;
	}
	
	/**
	 * Initiates the MatchServerListener.
	 * @param nimby 
	 * @param pipes A class that holds all pipes
	 */
	public void init(final Nimby nimby, final PipeIn<String> chatPipe) {
		this.game = nimby;
		this.chatPipe = chatPipe;
  	}

	/**
	 * Called when a server connected with user.
	 * @param server the server connected
	 */
	@Override
	public void connected(final Connection server) {
		LobbyServerHandle.getInstance().setConnectionType();
	}

	/**
	 * Called when user has disconnected.
	 * @param server the server that disconnected
	 */
	@Override
	public void disconnected(final Connection server) { }

	/**
	 * Called when receiving a package from lobby server.
	 * @param server The server received from
	 * @param data	The data received
	 */
	@Override
	public void received(final Connection server, final Object data) {

		if (data instanceof Packet1LoginAccepted) {
			boolean anwser = ((Packet1LoginAccepted) data).accepted;
			if (anwser) {
				Log.info("[CLIENT] Successfully logged in on server " + server.getRemoteAddressTCP());
			} else {
				Log.info("[CLIENT] Login attempt to server" + server.getRemoteAddressTCP() + " failed");
				server.close();
			}
		} else if (data instanceof Packet3Message) {
			Packet3Message message = ((Packet3Message) data);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(message.messageType).append("] ").append("[").append(message.sender).append("]: ").append(message.message);
			chatPipe.put(stringBuilder.toString());
		} else if (data instanceof Packet2ErrorMessage) {
			String message = ((Packet2ErrorMessage) data).message;
			chatPipe.put(message);
		} else if (data instanceof Packet6InitializeGameClient) {
			Packet6InitializeGameClient matchServer = ((Packet6InitializeGameClient) data);
			LobbyScreen lobbyScreen = (LobbyScreen) game.getScreen();
			lobbyScreen.changeToMatchScreen();
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			game.initMatchServerConnection(matchServer.ipAddress, matchServer.matchID);
		} 	
	}


}
