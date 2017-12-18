package edu.chalmers.nimby.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import edu.chalmers.model.Account;
import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.network.lobby.PlayerData;
import edu.chalmers.service.AbstractResource;

/**
 * A class which will handle login and create account demands from the different screens.
 * @author Gustav Dahl
 *
 */
public final class CreateLoginAccountHandle implements Runnable {

	private static final String LOGIN = "login";
	private static final String CREATE_ACCOUNT = "create";
	private static final String CREATE_SHIP = "createShip";

	private boolean resultReady;

	private static LobbyServerHandle connectionHandle;
	private String cmd;
	private String userName, password, email, token, shipToCreate;
	private static CreateLoginAccountHandle instance;
	private static boolean loggedIn = false;
	private static boolean createShipSuccesful = false;

	/**
	 * @return the createShipSuccesful
	 */
	public boolean isCreateShipSuccesful() {
		return createShipSuccesful;
	}

	/**
	 * @param createShipSuccesful the createShipSuccesful to set
	 */
	public static void setCreateShipSuccesful(final boolean createShipSuccesful) {
		CreateLoginAccountHandle.createShipSuccesful = createShipSuccesful;
	}

	/**
	 * @param loggedIn the loggedIn to set
	 */
	public static void setLoggedIn(final boolean loggedIn) {
		CreateLoginAccountHandle.loggedIn = loggedIn;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 *  Iniates the parameters used when running the thread.
	 * @param userName the username of the user
	 * @param password the password of the user
	 * @param email the email of the user
	 * @param cmd the command that the user whishes to use
	 * @param token the sessiontoken for the user
	 * @param shipToCreate the JSON representing a ship
	 */
	public void setParameters(final String userName, final String password, final String email, final String cmd,
			final String token, final String shipToCreate) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.cmd = cmd;
		this.token = token;
		this.shipToCreate = shipToCreate;
	}

	/**
	 * Resets all the parameters used when running the thread.
	 */
	public void clearParameters() {
		userName = null;
		password = null;
		email = null; 
		cmd = null;
		token = null;
		shipToCreate = null;
		createShipSuccesful  = false;

	}

	/**
	 * This is a utility class, this wont be called.
	 */
	private CreateLoginAccountHandle() { };

	/**
	 * returns a instance of this class.
	 * @return the instance
	 */
	public static synchronized CreateLoginAccountHandle getInstance() {
		if (instance == null) {
			instance = new CreateLoginAccountHandle();
		}
		return instance;
	}

	/**
	 * Tries to create an account on the accountserver.
	 * @param userName The prefered username of the user
	 * @param email the prefered email of the user
	 * @param password the password of the suer
	 */
	public static void createAccountOnAccServer(final String userName, final String email, final String password) {
		ClientRequest request = null;
		ClientResponse<String> response = null;
		Account send = new Account();
		send.setEmail(email);
		send.setProfileName(userName);
		send.setPassword(password);
		try {
			try {
				request = new ClientRequest(AccountServerConstants.ACCOUNT + "create/");
				request.accept("application/json");

				Json parser = new Json();
				parser.setOutputType(JsonWriter.OutputType.json);
				String input = parser.toJson(send);
				System.out.println(input);
				request.body("application/json", input);

				response = request.post(String.class);
				System.out.println("Status: " + response.getStatus());

			} catch (NoClassDefFoundError e) {
				System.out.println("Could not request connection to the server.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("The user could not create an account");
			System.out.println(userName + email + password);
		}
	}

	/**
	 * Tries to login an user to the account server.
	 * @param userName The userName of the user
	 * @param password The password of the user
	 * @return a boolean if the login was succesful
	 */
	public static void loginAccount(final String userName, final String password) {
		String sessionToken;
		ClientRequest request = null;
		Account login = new Account();
		login.setProfileName(userName);
		login.setPassword(password);
		try {
			try {
				request = new ClientRequest(AccountServerConstants.ACCOUNT + "login/");
			} catch (NoClassDefFoundError e) {
				System.out.println("The server are not avaible at this point. Please try again later.");
			}
			System.out.println(AccountServerConstants.ACCOUNT + "login/");

			Json parser = new Json();
			parser.setOutputType(JsonWriter.OutputType.json);
			String input = parser.toJson(login);
			System.out.println(input);
			request.body("application/json", input);

			ClientResponse<String> response = request.post(String.class);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String accInput = br.readLine();
			if (response.getStatus() == AbstractResource.LOGIN_OK) {
				sessionToken = accInput; 
				setLoggedIn(true);
				//loggedIn = true;
				connectionHandle = LobbyServerHandle.getInstance();
				connectionHandle.sendLoginRequest(userName);
				connectionHandle.setSessionToken(sessionToken);
			} else {
				String output = "Failed : HTTP error code : " + response.getStatus();
				System.out.println(output + " Cause:" + accInput);
				setLoggedIn(false);
				//loggedIn = false;
				throw new IllegalArgumentException("Wrong login values");
			}
			//Log in the user on the lobbyserver and save the credentials for this session
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("The user could not logi in on the account server");
		}
	}

	/**
	 * Tries to save a ship to the account server.
	 * @param token the users token
	 * @param shipToCreate ship as a Json sting
	 */
	public void createShip(final String token, final String shipToCreate) {
		ClientRequest request = null;
		//	setCreateShipSuccesful(false);
		if (shipToCreate == null) {
			throw new IllegalArgumentException("Can't create a ship which is null");
		}
		System.out.println(token);
		try {
			try {
				request = new ClientRequest(AccountServerConstants.SHIP + "create/" + token);
			} catch (NoClassDefFoundError e) {
				System.out.println("The server are not avaible at this point. Please try again later.");
			}
			request.body("application/json", shipToCreate);
			System.out.println(shipToCreate);
			System.out.println(AccountServerConstants.SHIP + "create/" + token);
			ClientResponse<String> response = request.post(String.class);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String reply = br.readLine();
			if (response.getStatus() == AbstractResource.CREATED) {
				// responsehandle here
				setCreateShipSuccesful(true);
				System.out.println("ship created fine");
				//				setCreateShipSuccesful(true);

			} else {
				String output = "Failed : HTTP error code : " + response.getStatus();
				System.out.println(output + " Cause:" + reply);
				//				loggedIn = false;
				setCreateShipSuccesful(false);
				setLoggedIn(false);
				throw new IllegalArgumentException("Server says you bad");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			setCreateShipSuccesful(false);
			System.out.println("Exception when creating ship");
		}

		setResultReady();

	}

	/**
	 * Fetches all the ships the user has created.
	 * @param token token to get ships for 
	 */
	public static void fetchCreatedShips(final String token) {
		ClientRequest request = null;
		System.out.println(token);
		try {
			try {
				request = new ClientRequest(AccountServerConstants.SHIP + "find/created/" + token);
			} catch (NoClassDefFoundError e) {
				System.out.println("The server are not avaible at this point. Please try again later.");
			}
			ClientResponse<String> response = request.get(String.class);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output = br.readLine();
			if (response.getStatus() == AbstractResource.STATUS_OK) {
				PlayerData.getInstance().getShips().clear();
				JsonReader parser = new JsonReader();
				JsonValue jValue = parser.parse(output);
				if (jValue.isArray()) {
					for (JsonValue value : jValue) {
						String name = value.get("id").getString("name");
						String shipString = value.getString("data");
						byte[] ship = Base64Coder.decode(shipString);
						PlayerData.getInstance().getShips().add(new PlayerShip(name, ship));
					}
				}
			} else {
				output = "Failed : HTTP error code : " + response.getStatus();
				throw new IllegalArgumentException("Wrong login values");
			}
		} catch (Exception e) {
			System.out.println("Exception when fetching ships");
		}
	}

	@Override
	public void run() {
		System.out.println(cmd);
		if (cmd.equals(LOGIN)) {
			loginAccount(userName, password);
			System.out.println("Logged in status" + loggedIn);
			clearParameters();
		} else if (cmd.equals(CREATE_ACCOUNT)) {
			createAccountOnAccServer(userName, email, password);	
			clearParameters();
		} else if (cmd.equals(CREATE_SHIP)) {
			createShip(token, shipToCreate);
		}
	}

	public synchronized boolean resultReady() {
		return resultReady;
	}

	private synchronized void setResultReady() {
		resultReady = true;
	}

}

