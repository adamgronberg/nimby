package edu.chalmers.matchServer.matchLogic;

import java.util.HashSet;

import edu.chalmers.nimby.network.match.PlayerConnection;

/**
 * Contains a team.
 * @author Adam Grönberg
 *
 */
public class Team {

	private HashSet<PlayerConnection> players;

	/**
	 * Constructor.
	 */
	public Team() {
		players = new HashSet<PlayerConnection>();
	}
	
	/**
	 * @return the players on the team
	 */
	public final synchronized HashSet<PlayerConnection> getPlayers() {
		return players;
	}
	
	/**
	 * Adds a player to the team.
	 * @param player the player to add
	 */
	public final synchronized void addPlayer(final PlayerConnection player) {
		players.add(player);
	}
	
}
