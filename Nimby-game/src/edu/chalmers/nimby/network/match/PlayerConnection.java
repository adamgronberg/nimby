package edu.chalmers.nimby.network.match;

import com.esotericsoftware.kryonet.Connection;

import edu.chalmers.nimby.model.PlayerShip;

/**
 * Used between the game client and match server. Stores information of the player.
 * @author Adam Gr�nberg
 *
 */
public class PlayerConnection extends Connection implements Player {
	private String userName = "";
	private String token = "";
	private int playerID = -1;
	private int matchID = -1;
	private int team = -1;
	private PlayerShip ship;
	
	/**
	 * Gets the player token.
	 * @return token
	 */
	public final String getToken() {
		return token;
	}
	
	public PlayerConnection() {
		super();
	}
	
	public PlayerConnection(final PlayerConnection playerConn) {
		this.userName = playerConn.getUserName();
		this.token = playerConn.getToken();
		this.playerID = playerConn.getPlayerID();
		this.matchID = playerConn.getMatchID();
		this.team = playerConn.getTeam();
		this.ship = new PlayerShip(playerConn.getShip());
	}
	
	/**
	 * @param token player token for current connection.
	 */
	public final void setToken(final String token) {
		this.token = token;
	}
	
	/**
	 * @return matchID that the payer is connected to
	 */
	public final int getMatchID() {
		return matchID;
	}
	
	/**
	 * @param matchID match ID that the payer is connected to
	 */
	public final void setMatchID(final int matchID) {
		this.matchID = matchID;
	}
	
	/**
	 * @return playerID the ID unique to the player of that match.
	 */
	public final int getPlayerID() {
		return playerID;
	}
	
	/**
	 * @param playerID the ID unique to the player of that match.
	 */
	public final void setPlayerID(final int playerID) {
		this.playerID = playerID;
	}
	
	/**
	 * Gets the user name of the player.
	 * @return players name
	 */
	public final String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the userName.
	 * @param userName name to set
	 */
	public final void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the ships.
	 * @return ships the player brought to the match.
	 */
	public final PlayerShip getShip() {
		return ship;
	}
	
	/**
	 * Sets the ships.
	 * @param ship to be set for the match.
	 */
	public final void setShip(final PlayerShip ship) {
		this.ship = ship;
	}

	/**
	 * @return the team number of player.
	 */
	public final int getTeam() {
		return team;
	}

	/**
	 * Sets the team number of the player.
	 * @param team the number of the team.
	 */
	public final void setTeam(final int team) {
		this.team = team;
	}
}
