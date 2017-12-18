package edu.chalmers.nimby.network.match;

/**
 * Used to send information about a player between matchServer and lobbyServer.
 * @author Adam Gr�nberg
 *
 */

public class PlayerInfo implements Player {
	private String userName = "";
	private String token = "";
	
	@Override
	public final String getToken() {
		return token;
	}
	
	/**
	 * @param token sets the token
	 */
	public final void setToken(final String token) {
		this.token = token;
	}
	
	@Override
	public final String getUserName() {
		return userName;
	}

	/**
	 * @param userName sent the user name
	 */
	public final void setUserName(final String userName) {
		this.userName = userName;
	}

}
