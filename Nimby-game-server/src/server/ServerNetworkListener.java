package server;


import java.util.HashSet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.network.lobby.ConnectionData;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet0LoginRequest;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet1LoginAccepted;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet2ErrorMessage;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet3Message;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet4Action;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet5RegisterConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet7JoinMatchQueueRequest;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet9MatchServerReady;
import edu.chalmers.nimby.network.lobby.NetworkConnection;
import edu.chalmers.nimby.network.lobby.ServerData;
import edu.chalmers.nimby.network.lobby.UserData;

/**
 * Listens and handles incoming data.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public class ServerNetworkListener extends Listener {

	public static final int SERVER_TCP_PORT = 54650;
	public static final int SERVER_UDP_PORT = 54655;

	private LobbyPacketFactory lobbyServer;
	private DataRouter dataRouter;

	/**
	 * Constructor. Sets server to listen on and creates the DataRouter used.
	 * @param lobbyServer The server to listen to
	 */
	public ServerNetworkListener(final LobbyPacketFactory lobbyServer) {
		this.lobbyServer = lobbyServer;
		dataRouter = lobbyServer.getDataRouter();
	}

	/**
	 * Called when someone connected to server.
	 * @param conn The user connected
	 */
	@Override
	public void connected(final Connection conn) { }

	/**
	 * Called when a connection has disconnected. Removes the connection as logged in
	 * and removes if from any queues.
	 * @param conn The connection that disconnected
	 */
	@Override
	public final void disconnected(final Connection conn) {
		if (conn instanceof NetworkConnection) {
			NetworkConnection netCon = (NetworkConnection) conn;
			ConnectionData connType = (ConnectionData) netCon.getData();
			if (connType instanceof UserData) {
				lobbyServer.getPlayerLists().removeUserFromMatchQueue(netCon);
				lobbyServer.getPlayerLists().removeUserFromLoggedIn(netCon);
			} else if (connType instanceof ServerData) { 
				lobbyServer.getPlayerLists().removeMatchServer(netCon);
			}
		}
	}

	/**
	 * Called when ServerNetworkListener has detected incoming data.
	 * @param conn The connection that sent the packet
	 * @param data Contains the packet the user sent
	 */
	@Override
	public final void received(final Connection conn, final Object data) {	
		NetworkConnection netCon = (NetworkConnection) conn;
		ConnectionData userType = netCon.getData();
		if (userType instanceof UserData) {
			handleUsers(netCon, data);
		} else if (userType instanceof ServerData) {
			handleMatchServers(netCon, data);
		} else if (data instanceof Packet5RegisterConnectionType) {
			Packet5RegisterConnectionType type = ((Packet5RegisterConnectionType) data);
			registerConnectionType(netCon, type);
		}
	}

	/**
	 * Registers the connection type.
	 * @param netCon	The connection to register
	 * @param type	The message containing the type
	 */
	private void registerConnectionType(final NetworkConnection netCon,
			final Packet5RegisterConnectionType type) {
		switch(type.connectionType) {
		case CLIENT:
			netCon.setData(new UserData());
			Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() +  " connected as a User");
			break;
		case MATCH_SERVER:
			netCon.setData(new ServerData());
			Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() +  " connected as a server");
			lobbyServer.getPlayerLists().addMatchServer(netCon);
			Log.info("[SERVER] MatchServer: " + netCon.getRemoteAddressTCP() +  " is now avalible.");
			break;
		default:
			break;
		}
	}

	/**
	 * Handles match server connections.
	 * @param netCon	is always a ServerConnection
	 * @param data		the data received from server
	 */
	private void handleMatchServers(final NetworkConnection netCon, final Object data) {
		if (data instanceof Packet9MatchServerReady) {
			Packet9MatchServerReady matchReady = (Packet9MatchServerReady) data;
			Log.info("[SERVER] Match: " + netCon.getRemoteAddressTCP() + " with ID " + matchReady.matchID 
					+  " is ready to start, sending players to the match");
			lobbyServer.sendPlayersToMatch(matchReady.matchID);
		}
	}

	/**
	 * Handles user connections.
	 * @param netCon	is always a UserConnection.
	 * @param data	The data received from user
	 */
	private void handleUsers(final NetworkConnection netCon, final Object data) {

		if (data instanceof Packet0LoginRequest) {
			Packet0LoginRequest request = ((Packet0LoginRequest) data);
			if (request.login) {
				tryLoginUser(netCon, request);
			} else {
				lobbyServer.getPlayerLists().removeUserFromLoggedIn(netCon);
				lobbyServer.getPlayerLists().removeUserFromMatchQueue(netCon);
			}
		} else if (data instanceof Packet3Message) {
			Packet3Message message = ((Packet3Message) data);
			handleMessage(netCon, message);
		} else if (data instanceof Packet4Action) {
			Packet4Action action = ((Packet4Action) data);
			checkActionType(netCon, action);
		} else if (data instanceof Packet7JoinMatchQueueRequest) {
			Packet7JoinMatchQueueRequest request = (Packet7JoinMatchQueueRequest) data;
			if (request.join) {
				lobbyServer.getPlayerLists().addUserToMatchQueue(netCon);
			} else {
				lobbyServer.getPlayerLists().removeUserFromMatchQueue(netCon);
			}

		} 
	}

	/**
	 * Handles user messages.
	 * @param netCon	is always a UserConnection.
	 * @param message	The message received from user
	 */
	private void handleMessage(final NetworkConnection netCon, final Packet3Message message) {
		UserData user = (UserData) netCon.getData();
		Log.info("[SERVER] User: " + user.getUserName() +  " has sent a message.");
		message.sender = user.getUserName();
		switch(message.messageType) {
		case WHISPER:
			NetworkConnection netConn = lobbyServer.getUser(message.receiver);
			if (netConn != null) {
				dataRouter.sendPrivateMessage(message, netConn);
			} else {
				Packet2ErrorMessage errorMessage = new Packet2ErrorMessage();
				errorMessage.message = "The user " + message.receiver + " is not online or does not exist";
				dataRouter.sendPrivateMessage(errorMessage, netCon);
			}
			break;
		case GENERAL:
			if (lobbyServer.getPlayerLists().getGeneralChatUsers().contains(netCon)) {
				dataRouter.sendToList(message, lobbyServer.getPlayerLists().getGeneralChatUsers());
			}
			break;
			//TODO Compare with the list coming the packet
			//TODO Get Members from users federation if he is part of any

			//and save them in a temporary list
		case FEDERATION:
			HashSet<NetworkConnection> onlineUsers = lobbyServer.getPlayerLists().getOnlineUsers();
			HashSet<NetworkConnection> onlineFederationMembers = new HashSet<NetworkConnection>();


			HashSet<String> federationMembers = message.set;

			for (String fedMember: federationMembers) {
				for (NetworkConnection conn: onlineUsers) {
					UserData data = (UserData) conn.getData();
					if (fedMember.equals(data.getUserName())) {
						onlineFederationMembers.add(conn);
						break;
					}
				}
			}
			dataRouter.sendToList(message, onlineFederationMembers);

			break;
		case BROADCAST:
			//TODO Check if the sender is admin, else do nothing
			dataRouter.sendToList(message, lobbyServer.getPlayerLists().getOnlineUsers());
			break;
		case SHIP:	
			//TODO send to matchserver
			//		dataRouter.sendToMatchServer(message);
			break;
		case TEAM:
			//	dataRouter.sendToMatchServer(message);
			//TODO send to matchserver
			//team chat
			break;
		case MATCH:
			//	dataRouter.sendToMatchServer(message);
			//TODO send to matchserver
			//Match chat
			break;
		default:
			break;
		}
	}

	/**
	 * Determines the type of action the user sent and executes it to do the right task.
	 * @param netCon 
	 * @param action the packet containing the action
	 */
	private void checkActionType(final NetworkConnection netCon, final Packet4Action action) {
		Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() + " has tried to make an action");

		switch(action.actionType) {
		case JOINGENERAL:
			lobbyServer.getPlayerLists().joinGeneralChat(netCon);
			break;
		case LEAVEGENERAL:
			lobbyServer.getPlayerLists().leaveGeneralChat(netCon);
			break;
		case IGNOREUSER:
			break;		
		case UNIGNOREUSER:
			break;	
		case LISTIGNORE:
			break;	
		default:
			break;
		}
	}

	/**
	 * Tries to login the user.
	 * @param netCon	is always a UserConnection
	 * @param request	the request data
	 */
	private void tryLoginUser(final NetworkConnection netCon, final Packet0LoginRequest request) {
		Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() +  " tries to login...");
		UserData user = (UserData) netCon.getData();

		Packet1LoginAccepted logInAnswer = new Packet1LoginAccepted();
		if (lobbyServer.valideLoginRequest(request.userName)) {
			user.setUserName(request.userName);
			logInAnswer.accepted = true;
			Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() + " has successfully logged in with user name: " +  request.userName + ".");
			lobbyServer.getPlayerLists().addUserToLoggedIn(netCon);
		} else {
			Log.info("[SERVER] User: " + netCon.getRemoteAddressTCP() + " has failed to login.");
		}
		netCon.sendTCP(logInAnswer);
	}
}
