package edu.chalmers.nimby.network.lobby;

import com.esotericsoftware.kryonet.Connection;

/**
 * A connection between server and client.
 * @author Adam Grönberg
 *
 */
public class NetworkConnection extends Connection {
	private ConnectionData data;
	
	/**
	 * The data contained in the connection (Either UserData or ServerData).
	 * @return The data type of connection.
	 */
	public final ConnectionData getData() {
		return data;
	}

	/**
	 * Sets the data type of the connection (Either UserData or ServerData).
	 * @param data the connection type
	 */
	public final void setData(final ConnectionData data) {
		this.data = data;
	} 

}
