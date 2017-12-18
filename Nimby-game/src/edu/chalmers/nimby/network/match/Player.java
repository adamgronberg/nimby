package edu.chalmers.nimby.network.match;

/**
 * Interface for setting and getting player userName and token.
 * @author Adam Gr�nberg
 *
 */
public interface Player {
	/**
	 * @return player token
	 */
	String getToken();
	
	/**
	 * @return player userName
	 */
	String getUserName();
	
}
