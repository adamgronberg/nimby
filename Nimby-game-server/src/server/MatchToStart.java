package server;

import java.util.HashSet;

import edu.chalmers.nimby.network.lobby.NetworkConnection;

/**
 * Contains a hashSet of NetworkConnection representing the players that are supposed to join a game as well as a 
 * NetworkConnection containing the information about the server they should join.
 * @author Adam Grönberg
 *
 */
public class MatchToStart {
	public HashSet<NetworkConnection> players;
	public NetworkConnection matchConn;
}
