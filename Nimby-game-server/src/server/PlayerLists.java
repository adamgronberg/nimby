package server;

import java.util.HashSet;

import edu.chalmers.nimby.network.lobby.NetworkConnection;

/**
 * Contains the different lists users are in.
 * @author Adam Grönberg
 *
 */
public class PlayerLists {

	private HashSet<NetworkConnection> loggedIn = new HashSet<NetworkConnection>();
	private HashSet<NetworkConnection> matchQueue = new HashSet<NetworkConnection>();
	private HashSet<NetworkConnection> matchServers = new HashSet<NetworkConnection>();
	private HashSet<NetworkConnection> generalChat = new HashSet<NetworkConnection>();
	
	/**
	 * Gets the available match servers connections.
	 * @return list of match servers
	 */
	public final HashSet<NetworkConnection> getMatchServers() {
		return matchServers;
	}

	/**
	 * Adds a match server to available match servers.
	 * @param matchServer the server to add
	 */
	public final void addMatchServer(final NetworkConnection matchServer) {
		matchServers.add(matchServer);
	}

	/**
	 * Removes a match server to available match servers.
	 * @param matchServer the server to remove
	 */
	public final void removeMatchServer(final NetworkConnection matchServer) {
		matchServers.remove(matchServer);
	}

	/**
	 * Returns a HashSet of UserConnections in match queue.
	 * @return current queue
	 */
	public final HashSet<NetworkConnection> getMatchQueue() {
		return matchQueue;
	}

	/**
	 * Adds a UserConnection to match queue.
	 * @param netConn UserCOnnection to add
	 */
	public final void addUserToMatchQueue(final NetworkConnection netConn) {
		matchQueue.add(netConn);
	}

	/**
	 * Removes a UserConnection to match queue.
	 * @param netConn UserCOnnection to remove
	 */
	public final void removeUserFromMatchQueue(final NetworkConnection netConn) {
		matchQueue.remove(netConn);
	}

	/**
	 * Returns a HashSet containing all users who are logged in.
	 * @return users online
	 */
	public final HashSet<NetworkConnection> getOnlineUsers() {
		return loggedIn;
	}

	/**
	 * Add UserConnection as logged in.
	 * @param netConn UserConnection set logged in
	 */
	public final void addUserToLoggedIn(final NetworkConnection netConn) {
		loggedIn.add(netConn);
		generalChat.add(netConn);
	}

	/**
	 * Removes UserConnection as logged in.
	 * @param conn UserConnection set logged off
	 */
	public final void removeUserFromLoggedIn(final NetworkConnection conn) {
		loggedIn.remove(conn);
		generalChat.remove(conn);
	}
	/**
	 * Removes the UserConnection from the generalChat.
	 * @param conn UserConnection to remove from the chat
	 */
	public final void leaveGeneralChat(final NetworkConnection conn) {
		generalChat.remove(conn);
	}

	/**
	 * Adds the UserConnection to the generalChat.
	 * @param conn UserConnection to add to the chat
	 */
	public final void joinGeneralChat(final NetworkConnection conn) {
		generalChat.add(conn);
	}

	/**
	 * Returns a HashSet containing all users participating in the general chat.
	 * @return a HashSet
	 */
	public final HashSet<NetworkConnection> getGeneralChatUsers() {
		return generalChat;
	}
	
}
