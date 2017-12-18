package edu.chalmers.nimby.util;

/**
 * URL's used to access the account server.
 * @author Mikael Stolpe
 *
 */
public interface AccountServerConstants {
	String IP_ADDR = "192.168.1.101:8080/nimbyAccServer/";
	String BASE_URL = "http://" + IP_ADDR;
	String ACCOUNT = BASE_URL + "account/";
	String SHIP = BASE_URL + "ship/";
	String FEDERATION = BASE_URL + "federation/";
	String PART = BASE_URL + "part/";
}
