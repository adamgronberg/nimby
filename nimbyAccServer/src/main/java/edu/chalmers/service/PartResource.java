package edu.chalmers.service;

import java.util.List;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import edu.chalmers.model.Part;
import edu.chalmers.persistence.IPartDao;

/**
 * Handles the Part specific methods for the server, getting of parts and verification of authenticity.
 * @author Mikael Stolpe
 *
 */
@Path("/part")
@Stateless
public class PartResource extends AbstractResource {
	
	@EJB
	private IPartDao partDao;
	
	/**
	 * Checks that the dao is up and running.
	 * @return A hello message
	 */
	@GET()
	@Produces("text/plain")
	public String sayHello() {
		getLogger().log(Level.INFO, partDao.toString());
	    return "Part restEasy";
	}
	
	/**
	 * Returns all the parts available on the server.
	 * @return JSON-list with all parts or empty list
	 */
	@GET()
	@Path("/find/all/")
	public Response getAll() {
		List<Part> parts = partDao.getAll();
		GenericEntity<List<Part>> ge = new GenericEntity<List<Part>>(parts) {
		};
		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Gets a part with a specific id.
	 * @param id id of part
	 * @return JSON - object with part.
	 */
	@GET()
	@Path("/find/byId/{id}")
	public Response byId(@PathParam("id") final String id) {
		if (!isStringValid(id)) {
			return Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		
		Part db = partDao.findById(id);
		if (db == null) {
			return Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		
		return Response.status(STATUS_OK).entity(db).build();
	}

	/**
	 * Checks if a part is valid.
	 * @param compare part to compare.
	 * @return 200 if ok. o/w error
	 */
	@GET()
	@Path("/compare/")
	public Response compare(final Part compare) {
		if (compare == null) {
			return Response.status(PART_COMPARE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		Part db = partDao.findById(compare.getName());
		
		if (!db.equals(compare)) {
			return Response.status(PART_COMPARE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		return Response.status(STATUS_OK).build();
		
	}
}

