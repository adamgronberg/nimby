package edu.chalmers.nimby.network.lobby;

/**
 * Class containing UserCOnnection specific information.
 * @author Adam Gr�nberg
 *
 */
public class UserData implements ConnectionData {

	private String userName = "";
	private String token = "-1";
	
	/**
	 * Gets userName.
	 * @return userName
	 */
	public final String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the UserName.
	 * @param userName the name to give user
	 */
	public final void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * Gets token.
	 * @return token
	 */
	public final String getToken() {
		return token;
	}

	/**
	 * Sets token.
	 * @param sessionToken token
	 */
	public final void setToken(final String sessionToken) {
		this.token = sessionToken;
	}
	
}
