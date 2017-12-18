package edu.chalmers.persistence;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Part;

/**
 * Defines methods specific for Part.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/part")
public interface IPartDao extends Dao<String, Part> {
	
}
