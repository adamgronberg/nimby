package edu.chalmers.nimby.network.lobby;

import com.esotericsoftware.kryonet.Client;

import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.ConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet0LoginRequest;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet5RegisterConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet7JoinMatchQueueRequest;

/**
 * The handle of the connection between the client and server.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public final class LobbyServerHandle extends ServerHandle {

	private String userName = "";
	private String sessionToken = "";
	private PlayerShip ship;
	
	private static LobbyServerHandle instance = null;
	
	/**
	 * Returns the instance of this class if it has been initialized.
	 * @return the instance, null if the LobbyServerHandle hasn't been initialized
	 */
	public static synchronized LobbyServerHandle getInstance() {
		if (instance == null) {
			instance = new LobbyServerHandle();
		} 
		return instance;
	}

	/**
	 * Constructor. 
	 */
	private LobbyServerHandle() { }

	/**
	 * Sends a packet to the connected server.
	 * @param packet packet to  send
	 */
	public void sendTCPPacket(final AbstractPacket packet) {
		client.sendTCP(packet);
	}
	
	/**
	 * Sends a login request to lobby server.
	 * @param userName The user name the client tries to login with
	 */
	public void sendLoginRequest(final String userName) {
		this.userName = userName;
		Packet0LoginRequest request = new Packet0LoginRequest();
		request.userName = userName;
		request.login = true;
		client.sendTCP(request);
	}
	
	/**
	 * Sends a logout request to lobby server.
	 */
	public void sendLogoutRequest() {
		this.userName = "";
		Packet0LoginRequest request = new Packet0LoginRequest();
		request.userName = userName;
		request.login = false;
		client.sendTCP(request);
	}
	
	/**
	 * Sets the type of connection to the server to client.
	 */
	public void setConnectionType() {
		Packet5RegisterConnectionType registerRequest = new Packet5RegisterConnectionType();
		registerRequest.connectionType = ConnectionType.CLIENT;
		client.sendTCP(registerRequest);
	}

	@Override
	public void init() {
		client = new Client();
		client.addListener(LobbyServerListener.getInstance());
		LobbyNetwork.register(client);
	}

	/**
	 * Gets current used userName. Used to optimize client.
	 * @return current userName
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the ships used for the next match.
	 * @param ship The ship player will use in the match.
	 */
	public void setMatchShip(final PlayerShip ship) {
		this.ship = ship;
	}
	
	/**
	 * Gets the ships used for the next match.
	 * @return the ships to be used for the match.
	 */
	public PlayerShip getMatchShip() {
		return ship;
	}

	/**
	 * Gets the session token.
	 * @return sessionToken
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Sets session token.
	 * @param sessionToken token sent.
	 */
	public void setSessionToken(final String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	/**
	 * Sends a request to either join or leave matchmaking queue.
	 * @param join
	 */
	public void sendMatchQueueRequest(final boolean join) {
		Packet7JoinMatchQueueRequest request = new Packet7JoinMatchQueueRequest();
		request.join = join;
		sendTCPPacket(request);
	}

}
