package edu.chalmers.service;

import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.ejb.EJB;

import edu.chalmers.crypt.BCrypt;
import edu.chalmers.model.Account;
import edu.chalmers.persistence.IAccountDao;

/**
 * Abstract class containing methods and static strings for services within the package.
 * @author Mikael Stolpe
 *
 */
public class AbstractResource {

	/**
	 * For debugging.
	 */
	private Logger logger = Logger.getAnonymousLogger();
	
	/**
	 * Getter for logger.
	 * @return logger
	 */
	protected final Logger getLogger() {
		return logger;
	}
	
	/**
	 * Setter for logger.
	 * @param log logger
	 */
	protected final void setLogger(final Logger log) {
		this.logger = log;
	}

	@EJB
	private IAccountDao accountDao;
	
	/**
	 * Status codes.
	 */
	public static final int STATUS_OK = 200;
	public static final int CREATED = 201;
	
	public static final int GET_FAILED = 418;
	public static final int LOGIN_FAILED = 419;
	public static final int LOGOUT_FAILED = 420;
	public static final int ACCOUNT_NOT_CREATED = 421;
	public static final int ACCOUNT_NOT_UPDATED = 422;
	public static final int ACCOUNT_NOT_DELETED = 423;
	public static final int FEDERATION_NOT_CREATED = 424;
	public static final int FEDERATION_NOT_UPDATED = 425;
	public static final int FEDERATION_NOT_DELETED = 426;
	public static final int SHIP_NOT_CREATED = 427;
	public static final int SHIP_NOT_UPDATED = 428;
	public static final int SHIP_NOT_DELETED = 429;
	public static final int SHIP_NOT_FOUND = 430;
	public static final int FRIEND_NOT_ADDED = 431;
	public static final int FRIEND_NOT_REMOVED = 432;
	
	public static final int MEMBER_NOT_REMOVED = 433;
	public static final int MEMBER_NOT_ADDED = 434;
	public static final int FEDERATION_CANT_LEAVE = 435;
	
	public static final int FEDERATION_PROMOTE_FAIL = 436;
	public static final int FEDERATION_DEMOTE_FAIL = 437;
	
	public static final int PART_COMPARE_FAIL = 438;
	public static final int DROP_FAIL = 438;
	
	public static final int LOGIN_OK = 207;
	public static final int LOGOUT_OK = 208;
	public static final int DELETE_OK = 209;
	public static final int DROP_OK = 210;	



	/**
	 * Reply messages to sender.
	 */
	public static final String NAME_ALREADY_EXISTS = "This name is already present in the database";
	public static final String MAIL_ALREADY_EXISTS = "This email is already present in the database";
	public static final String NAME_DOESNT_EXIST = "This name doesn't exist in the database";
	

	public static final String NULL_NOT_ALLOWED = "Argument was null";
	
	public static final String ACC_SUCCESSFULLY_CREATED = "Account was created successfully";
	public static final String ACC_SUCCESSFULLY_UPDATED = "Account was updated successfully";
	public static final String ACC_SUCCESSFULLY_DELETED = "Account was deleted successfully";
	public static final String ACC_SUCCESSFULLY_LOGOUT = "Account was logged out successfully";
	public static final String ACC_SUCCESSFULLY_DROPPED = "Token was successfully dropped";
	
	public static final String FED_SUCCESSFULLY_CREATED = "Federation was created successfully";
	public static final String FED_SUCCESSFULLY_UPDATED = "Federation was updated successfully";
	public static final String FED_SUCCESSFULLY_DELETED = "Federation was deleted successfully";
	public static final String FED_SUCCESSFULLY_PROMOTED = "Member was promoted";
	public static final String FED_SUCCESSFULLY_DEMOTED = "Member was demoted";
	public static final String FED_ALREADY_MEMBER = "This account is already a member of an federation";
	public static final String FED_ALREADY_EXISTS = "This federation name already exists";
	public static final String FED_NOT_MEMBER = "This account is not a member of a/this federation";
	public static final String FED_REMOVED = "Member removed";
	public static final String FED_PROMOTE_FAIL = "Failed to promote this member";
	public static final String FED_DEMOTE_FAIL = "Failed to demote this member";
	public static final String FED_INVALID_RANK = "Invalid rank for this operation";
	public static final String FED_MEMBER_ADDED = "Member successfully added";

	public static final String SHIP_SUCCESSFULLY_CREATED = "Ship was created successfully";
	public static final String SHIP_SUCCESSFULLY_UPDATED = "Ship was updated successfully";
	public static final String SHIP_SUCCESSFULLY_DELETED = "Ship was deleted successfully";
	public static final String SHIP_MISMATCH = "Builder and logged in account do not match";
	public static final String FRIEND_ADDED = "Friend added successfully";
	public static final String FRIEND_ALREADY = "Already friends";
	public static final String FRIEND_N_REMOVED = "Not friends";
	public static final String FRIEND_REMOVED = "Friend removed successfully";
	
	
	public static final String WRONG_TOKEN = "No such token or doesn't match the account";
	public static final String MISSING_CONTENT = "You did not specify all the required parameters";
	public static final String DUPLICATE_CONTENT = "This content is already present in the database";
	public static final String INVALID_STRING = "Invalid or no parameter entered";
	public static final String NOT_LOGGED_IN = "You are not holding a proper token, i.e. not logged in";
	public static final String WRONG_PASSWORD = "This is not the correct password for this account";
	

	
	
	/**
	 * Ranks available in federation.
	 */
	public static final String TOP = "Admiral";
	public static final String MIDDLE = "General";
	public static final String BOTTOM = "Cadet";
	
	
	public static final int LOG_ROUNDS = 12;
	
	/**
	 * Gives a new random salt for hashing password.
	 * @param seed Random number, use i.e. date stamp.
	 * @return Random secure number.
	 */
	public final String saltShaker(final Long seed) {
		SecureRandom shaker = new SecureRandom();
		shaker.setSeed(seed);
		return BCrypt.gensalt(LOG_ROUNDS, shaker);
	}

	/**
	 * Generates a random token for temp-saving in user account.
	 * @param seed seed to initate randomizer with.
	 * @return A random token.
	 */
	public final String tokenGenerator(final Long seed) {
		SecureRandom tokenizer = new SecureRandom();
		tokenizer.setSeed(seed);
		return "" + tokenizer.nextLong();
	}
	
	/**
	 * Checks if a token is correct.
	 * @param token token to check.
	 * @return true if ok, false o/w.
	 */
	public boolean isTokenValid(final String token) {
		if (!isStringValid(token)) {
			return false;
		}
		Account stored = getAccountDao().findByToken(token);
		
		if (stored == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a string is null.
	 * @param check String to check.
	 * @return true if fine.
	 */
	public boolean isStringValid(final String check) {
		return !(check == null);
	}

	/**
	 * Getter for account dao.
	 * @return accountDao.
	 */
	public final IAccountDao getAccountDao() {
		return accountDao;
	}

	/**
	 * Setter for acount dao.
	 * @param accDao new accountDao.
	 */
	public final void setAccountDao(final IAccountDao accDao) {
		this.accountDao = accDao;
	}
	
}
