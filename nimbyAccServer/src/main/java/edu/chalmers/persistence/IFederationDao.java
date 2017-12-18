package edu.chalmers.persistence;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Account;
import edu.chalmers.model.Federation;

/**
 * Defines methods specific for federation.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/federation")
public interface IFederationDao extends Dao<String, Federation> {
	/**
	 * Looks for all federations with a similar name.
	 * @param name to look for.
	 * @return All federations which match.
	 */
	List<Federation> findByName(String name);
	/**
	 * Looks for a Federation with a specific name.
	 * @param name Name to look for.
	 * @return Federation if it's a match.
	 */
	Federation findByExactName(String name);
}
