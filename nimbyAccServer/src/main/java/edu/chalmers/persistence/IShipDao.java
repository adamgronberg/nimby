package edu.chalmers.persistence;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Account;
import edu.chalmers.model.Ship;
import edu.chalmers.model.ShipPK;

/**
 * Defines methods specific for Ships.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/ship")
public interface IShipDao extends Dao<ShipPK, Ship> {
	/**
	 * Looks for all ships matching a certain name.
	 * @param name name to look for.
	 * @return All ships that match.
	 */
	List<Ship> findByName(String name);
	/**
	 * Looks for a specific name.
	 * @param name name to look for.
	 * @return The exact match.
	 */
	Ship findByExactName(String name);
	
	/**
	 * Looks for all ships a certain account has created.
	 * @param creator Creator of the ship.
	 * @return All created ships.
	 */
	List<Ship> findByCreator(Account creator);
	
}
