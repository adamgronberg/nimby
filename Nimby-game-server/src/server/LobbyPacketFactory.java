package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet6InitializeGameClient;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet8InitializeMatchServer;
import edu.chalmers.nimby.network.lobby.NetworkConnection;
import edu.chalmers.nimby.network.lobby.UserData;
import edu.chalmers.nimby.network.match.PlayerInfo;

/**
 * The main server.
 * @author Adam Gr�nberg and Gustav Dahl
 *
 */
public class LobbyPacketFactory extends Server {

	private PlayerLists playerLists = new PlayerLists();
	private HashMap<Integer, MatchToStart> matchesToStart = new HashMap<Integer, MatchToStart>();
	
	private int matchIDCounter = 1;	

	private DataRouter dataRouter;

	/**
	 * Constructor.
	 */
	public LobbyPacketFactory() {
		super();
		dataRouter = new DataRouter(this);
	}

	@Override
	protected final Connection newConnection() {
		return new NetworkConnection();
	}

	/**
	 * Controls that the login request is valid.
	 * @param userName name of user
	 * @return true if valid login
	 */
	public final boolean valideLoginRequest(final String userName) {
		for (NetworkConnection netConn : playerLists.getOnlineUsers()) {
			UserData user = (UserData) netConn.getData();
			if (user.getUserName().equals(userName)) {
				Log.info("[SERVER] Username: " + userName + " is taken, login rejected.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the UserConnection if the is online.
	 * @param userName	The name of the user to get
	 * @return The user if online, null if offline
	 */
	public final NetworkConnection getUser(final String userName) {
		for (NetworkConnection netConn : playerLists.getOnlineUsers()) {
			UserData user = (UserData) netConn.getData();
			if (user.getUserName().equals(userName)) {
				return netConn;
			}
		}
		return null;
	}

	/**
	 * Starts a match on a match server.
	 */
	public final void startMatch() {
		HashSet<NetworkConnection> oldQueue = new HashSet<NetworkConnection>();
		
		if (playerLists.getMatchQueue().isEmpty()) {
			Log.error("No UserConnections online!");	
			return;
		}
		
		int matchID = matchIDCounter;
		NetworkConnection matchConn = null;
		for (NetworkConnection netConn: playerLists.getMatchServers()) {	//TODO just takes the first server atm, should take the "best" one
			matchConn = netConn;
			break;
		}
		
		if (matchConn == null) {
			Log.error("No match servers connected!");	
			return;
		}
		
		//Information to Initialize match server.
		Packet8InitializeMatchServer matchInfoToMatchServer = new Packet8InitializeMatchServer();
		matchInfoToMatchServer.matchID = matchIDCounter;
//		for (NetworkConnection netConn: playerLists.getMatchQueue()) {
//			UserData user = (UserData) netConn.getData();
//			PlayerInfo player = new PlayerInfo();
//			player.setUserName(user.getUserName());
//			player.setToken(user.getToken());
//			matchInfoToMatchServer.players.add(player);
//			oldQueue.add(netConn);
//			playerLists.getMatchQueue().remove(netConn);
//		}
		
		Iterator<NetworkConnection> itr = playerLists.getMatchQueue().iterator();
		while (itr.hasNext()) {
			NetworkConnection netConn = itr.next();
			UserData user = (UserData) netConn.getData();
			PlayerInfo player = new PlayerInfo();
			player.setUserName(user.getUserName());
			player.setToken(user.getToken());
			matchInfoToMatchServer.players.add(player);
			oldQueue.add(netConn);
		}
		playerLists.getMatchQueue().clear();
				
		MatchToStart match = new MatchToStart();
		match.matchConn = matchConn;
		 
		match.players = oldQueue;
		matchesToStart.put(matchID, match);
		
		dataRouter.sendPrivateMessage(matchInfoToMatchServer, matchConn);
		matchIDCounter++;
	}
	
	/**
	 * Sends the players to the matchID.
	 * @param matchID the ID of the match that is ready to receive it's connections.
	 */
	public final void sendPlayersToMatch(final int matchID) {
		
		if (!matchesToStart.containsKey(matchID)) {
			Log.error("[SERVER] matchesToStart didn't contain matchID: " + matchID);
		}
		
		MatchToStart match = matchesToStart.get(matchID);
		Packet6InitializeGameClient matchInfoToClient = new Packet6InitializeGameClient();
		matchInfoToClient.ipAddress = match.matchConn.getRemoteAddressTCP().getAddress().toString().replace("/", "");
		matchInfoToClient.matchID = matchID;
		Log.info("[SERVER] Sending match info to clients");

		dataRouter.sendToList(matchInfoToClient , match.players);
	}

	/**
	 * Gets the dataRouter used.
	 * @return dataRouter
	 */
	public final DataRouter getDataRouter() {
		return dataRouter;
	}

	/**
	 * @return the different lists containit player positions.
	 */
	public PlayerLists getPlayerLists() {
		return playerLists;
	}
}
