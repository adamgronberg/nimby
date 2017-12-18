package edu.chalmers.persistence;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Account;

/**
 * Defines methods specific for Account.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/account")
public interface IAccountDao extends Dao<String, Account> {
	/**
	 * Looks for an account with a specific token.
	 * @param token to look for.
	 * @return Account if found.
	 */
	Account findByToken(String token);
	/**
	 * Looks for all accounts with a certain name.
	 * @param name name to look for.
	 * @return Returns all matches to this account.
	 */
	List<Account> findByName(String name);
	/**
	 * Looks for accounts with a specific mail. 
	 * @param email mail to look for.
	 * @return Account if any matches.
	 */
	Account findByMail(String email);
}
