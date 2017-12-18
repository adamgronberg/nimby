package edu.chalmers.service;

import java.util.List;
import java.util.logging.Level;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mockito.Mockito;

import edu.chalmers.crypt.BCrypt;
import edu.chalmers.model.Account;
import edu.chalmers.persistence.IAccountDao;
import edu.chalmers.persistence.JpaAccountDao;

/**
 * Handles the account parts of the server, creation of account, update, delete, login.
 * @author Mikael Stolpe
 *
 */
@Path("/account")
@Stateless
public class AccountResource extends AbstractResource {
	


	/**
	 * Checks that the dao is up and running.
	 * @return A hello message
	 */
	@GET()
	@Produces("text/plain")
	public String sayHello() {
		getLogger().log(Level.INFO, getAccountDao().toString());
	    return "Account restEasy";
	}
	
	/**
	 * Used for creating an account.
	 * @param create Account to be created
	 * @return ACCOUNT_NOT_CREATED if failure o/w SUCCESSFULLY_CREATED
	 */
	@POST()
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create/")
	public Response createAccount(final Account create) {
		if (create == null || create.getProfileName() == null || create.getEmail() == null || create.getPassword() == null) {
			return Response.status(ACCOUNT_NOT_CREATED).entity(MISSING_CONTENT).build();
		}
		
		Account db = getAccountDao().findById(create.getProfileName());
		if (db != null) {
			return Response.status(ACCOUNT_NOT_CREATED).entity(NAME_ALREADY_EXISTS).build();
		}
		
		Account dbE = getAccountDao().findByMail(create.getEmail());
		if (dbE != null) {
			return Response.status(ACCOUNT_NOT_CREATED).entity(MAIL_ALREADY_EXISTS).build();
		}
		
		String salt = saltShaker(System.currentTimeMillis());
		String hash = BCrypt.hashpw(create.getPassword(), salt);
		
		create.setHash(hash);
		create.setSalt(salt);
		
		getLogger().log(Level.INFO, "Create account : " + create.getProfileName());
		getLogger().log(Level.INFO, "Create account : " + create.getEmail());
		getLogger().log(Level.INFO, "Create account : " + create.getPassword());
		getLogger().log(Level.INFO, "Create account : " + create.getSalt());
		getLogger().log(Level.INFO, "Create account : " + create.getHash());

		// clear password
		create.setPassword("");
		getAccountDao().persist(create);
		db = getAccountDao().findById(create.getProfileName());
		return Response.status(CREATED).entity(ACC_SUCCESSFULLY_CREATED + ": " + db.getProfileName()).build();
	}
	
	/**
	 * Updates an account.
	 * @param update Account with new values
	 * @return OK message if successful, error message o/w
	 */
	@PUT()
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/update/")
	public Response updateAccount(final Account update) {
		if (update == null) {
			return Response.status(ACCOUNT_NOT_UPDATED).entity(NULL_NOT_ALLOWED).build();
		}
		
		Account db = getAccountDao().findById(update.getProfileName());
		if (db == null) {
			return Response.status(ACCOUNT_NOT_UPDATED).entity(NAME_DOESNT_EXIST).build();
		}
		Account token = getAccountDao().findByToken(update.getToken());
		if (!db.equals(token)) {
			return Response.status(ACCOUNT_NOT_UPDATED).entity(NOT_LOGGED_IN).build();
		}
		getLogger().log(Level.INFO, "Token ok.");
		
		
		if (!db.getEmail().equalsIgnoreCase(update.getEmail())) {
			// Email has changed
			db.setEmail(update.getEmail());
			getLogger().log(Level.INFO, "Email has changed : " + db.getEmail());
		} else {
			getLogger().log(Level.INFO, "Email has not changed");
		}
		
		if (!isPassValid(update)) {
			// Password has changed
			String salt = saltShaker(System.currentTimeMillis());
			String hash = BCrypt.hashpw(update.getPassword(), salt);
			db.setHash(hash);
			db.setSalt(salt);
			getLogger().log(Level.INFO, "Password has changed : " + db.getSalt());
			getLogger().log(Level.INFO, "Password has changed : " + db.getHash());
			getLogger().log(Level.INFO, "Password has changed : " + db.getPassword());
		} else {
			getLogger().log(Level.INFO, "Password has not changed: " + update.getPassword());
		}
		
		
		getAccountDao().update(db);
		return Response.status(CREATED).entity(ACC_SUCCESSFULLY_UPDATED + ": " + db.getProfileName()).build();
		
	}
	
	/**
	 * Deletes an account.
	 * @param token Valid token for account.
	 * @param pass Valid pass for account.
	 * @return @return OK message if successful, error message o/w 
	 */
	@DELETE()
	@Path("/delete/{token}/{pass}")
	public Response deleteAcc(@PathParam("token") final String token, @PathParam("pass") final String pass) {
		if (!isTokenValid(token)) {
			return Response.status(ACCOUNT_NOT_DELETED).entity(WRONG_TOKEN).build();
		}
		
		Account db = getAccountDao().findByToken(token);
		Account delete = new Account();
		delete.setPassword(pass);
		delete.setProfileName(db.getProfileName());

		if (!isPassValid(delete)) {
			return Response.status(ACCOUNT_NOT_DELETED).entity(WRONG_PASSWORD).build();
		}
		
		getAccountDao().remove(db);
		
		return Response.status(DELETE_OK).entity(ACC_SUCCESSFULLY_DELETED).build();
	}
	
	/**
	 * Gets an account from the database and sends as JSON.
	 * @param token Token for account.
	 * @return Account as JSON if successful, error message o/w.
	 */
	@GET()
	@Path("/find/acc/{token}")
	public Response getAcc(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(GET_FAILED).entity(WRONG_TOKEN).build();
		}
		getLogger().log(Level.INFO, "" + token + ": " + getAccountDao());
		Account db = getAccountDao().findByToken(token);
		if (db == null) {
			return Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		return Response.status(STATUS_OK).entity(db).build();
	}
	
	/**
	 * Gets all the accounts which match a name and sends as JSON.
	 * @param name Name to look for.
	 * @return JSON-list if successfull, error message o/w.
	 */
	@GET()
	@Path("/find/byName/{name}")
	public Response findByName(@PathParam("name") final String name) {
		if (!isStringValid(name)) {
			return Response.status(GET_FAILED).entity(INVALID_STRING).build();
		}
		List<Account> db = getAccountDao().findByName(name);
		if (db.isEmpty()) {
			return Response.status(GET_FAILED).entity(NAME_DOESNT_EXIST).build();
		}
		
		GenericEntity<List<Account>> ge = new GenericEntity<List<Account>>(db) {
		}; 
		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Used to login to the server.
	 * @param login Account to login
	 * @return Token if successfull, error-code if not
	 */
	@POST()
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/login/")
	public Response login(final Account login) {
		getLogger().log(Level.INFO, "accountDao = " + getAccountDao());
		getLogger().log(Level.INFO, "Login: " + getAccountDao());
		Account db = getAccountDao().findById(login.getProfileName());
		if (db == null) {
			return Response.status(LOGIN_FAILED).entity(NAME_DOESNT_EXIST).build();
		}
		if (!isPassValid(login)) {
			return Response.status(LOGIN_FAILED).entity(WRONG_PASSWORD).build();
		}
		
		String token = tokenGenerator(System.currentTimeMillis());
		db.setToken(token);
		getAccountDao().update(db);
		
		return Response.status(LOGIN_OK).entity(token).build();
		
	}
	
	/**
	 * Used by the game-server to drop an inactive account or by the user to logout.
	 * @param token token for account to drop or logout
	 * @return LOGOUT_FAILED if no such token or LOGOUT_OK if successfull
	 */
	@GET()
	@Path("/logout/{token}")
	public Response logout(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(DROP_FAIL).entity(WRONG_TOKEN).build();
		}
		
		Account db = getAccountDao().findByToken(token);
		db.setToken(null);
		getAccountDao().update(db);
		return Response.status(DROP_OK).entity(ACC_SUCCESSFULLY_DROPPED).build();
	}
	
	/**
	 * Makes a link bewteen two accounts as friends.
	 * @param token Token for one account.
	 * @param friendName Name of other account
	 * @return Ok if successful, error message o/w.
	 */
	@POST()
	@Path("/friend/add/{token}/{friendName}")
	public Response addFriend(@PathParam("token") final String token, @PathParam("friendName") final String friendName) {
		if (!isTokenValid(token) || !isStringValid(friendName)) {
			return Response.status(FRIEND_NOT_ADDED).entity(MISSING_CONTENT).build();
		}
		
		Account dbAcc = getAccountDao().findByToken(token);
		Account dbFriend = getAccountDao().findById(friendName);
		
		if (dbFriend == null || dbFriend.equals(dbAcc)) {
			return Response.status(FRIEND_NOT_ADDED).entity(MISSING_CONTENT).build();
		}
		List<Account> friends = dbAcc.getAccounts1();
		List<Account> friends2 = dbFriend.getAccounts1();
		
		for (Account acc : friends) {
			getLogger().log(Level.INFO, "" + acc.getProfileName());
		}
		
		for (Account acc : friends2) {
			getLogger().log(Level.INFO, "" + acc.getProfileName());
		}
		
		if (friends.contains(dbFriend) || friends2.contains(dbAcc)) {
			return Response.status(FRIEND_NOT_ADDED).entity(FRIEND_ALREADY).build();
		}
		
		friends.add(dbFriend);
		friends2.add(dbAcc);

		dbAcc.setAccounts1(friends);
		dbFriend.setAccounts1(friends2);
		
		getAccountDao().update(dbAcc);
		getAccountDao().update(dbFriend);
		
		return Response.status(STATUS_OK).entity(FRIEND_ADDED).build();
	}
	
	/**
	 * Removes the link between two accounts as friends.
	 * @param token Token of one account
	 * @param friendName Name of other account
	 * @return OK if successful, error-message o/w.
	 */
	@PUT()
	@Path("/friend/remove/{token}/{friendName}")
	public Response removeFriend(@PathParam("token") final String token, @PathParam("friendName") final String friendName) {
		if (!isTokenValid(token) || !isStringValid(friendName)) {
			return Response.status(FRIEND_NOT_REMOVED).entity(MISSING_CONTENT).build();
		}
		
		Account dbAcc = getAccountDao().findByToken(token);
		Account dbFriend = getAccountDao().findById(friendName);
		if (dbAcc.equals(dbFriend)) {
				return Response.status(FRIEND_NOT_REMOVED).entity(MISSING_CONTENT).build();
		}
		List<Account> friends = dbAcc.getAccounts1();
		
		if (!friends.contains(dbFriend)) {
				return Response.status(FRIEND_NOT_REMOVED).entity(FRIEND_N_REMOVED).build();
		}
		List<Account> friends2 = dbFriend.getAccounts1();
		friends2.remove(dbAcc);
		friends.remove(dbFriend);
		dbAcc.setAccounts1(friends);
		dbFriend.setAccounts1(friends2);
		getAccountDao().persist(dbAcc);
		getAccountDao().persist(dbFriend);
		
		return Response.status(STATUS_OK).entity(FRIEND_REMOVED).build();
	}
	
	/**
	 * Gets a list of all friends associated with the account.
	 * @param token Token of account
	 * @return JSON-list of friends, error message if fail.
	 */
	@GET()
	@Path("/find/friends/{token}")
	public Response listFriends(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		getLogger().log(Level.INFO, "" + token + ": " + getAccountDao());
		Account db = getAccountDao().findByToken(token);
		
		
		List<Account> friends = db.getAccounts1();
		
		GenericEntity<List<Account>> ge = new GenericEntity<List<Account>>(friends) {
		};
		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Checks if a password is correct.
	 * @param acc Account to check password for.
	 * @return true if ok, false o/w
	 */
	private boolean isPassValid(final Account acc) {
		Account db = getAccountDao().findById(acc.getProfileName());
		String salt = db.getSalt();

		String recievedHash = BCrypt.hashpw(acc.getPassword(), salt);
		getLogger().log(Level.INFO, db.getHash() + "   :   " + recievedHash);
		getLogger().log(Level.INFO, acc.getPassword());
		return recievedHash.equals(db.getHash());
		
	}
	
	/**
	 * Sets the account dao to Mockito version.
	 * @return Mocked account dao.
	 */
	public IAccountDao mockDao() {
		setAccountDao(Mockito.mock(JpaAccountDao.class));
		return getAccountDao();
	}
}
