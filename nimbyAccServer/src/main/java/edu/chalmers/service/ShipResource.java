package edu.chalmers.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
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

import edu.chalmers.model.Account;
import edu.chalmers.model.Ship;
import edu.chalmers.persistence.IShipDao;

/**
 * Handles the Ship part of the database and the connection to it.
 * @author Mikael Stolpe
 *
 */
@Path("/ship")
@Stateless
public class ShipResource extends AbstractResource {
	
	@EJB
	private IShipDao shipDao; 
	
	/**
	 * Checks that the dao is up and running.
	 * @return A hello message
	 */
	@GET()
	@Produces("text/plain")
	public String sayHello() {
		getLogger().log(Level.INFO, shipDao.toString());
	    return "Ship RestEasy Service";
	}
	
	
	/**
	 * Saves a ship in the database.
	 * @param token User token.
	 * @param create Ship to create.
	 * @return Ok message if successful, error o/w.
	 */
	@POST()
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create/{token}")
	public Response createShip(@PathParam("token") final String token, final Ship create) {
		if (!isTokenValid(token)) {
			return Response.status(SHIP_NOT_CREATED).entity(WRONG_TOKEN).build();
		}
		getLogger().log(Level.INFO, "Acc :"  + getAccountDao() + "Ship :" + shipDao);
		Account dbAcc = getAccountDao().findByToken(token);
		create.getId().setBuilder(dbAcc.getProfileName());
		Ship dbShip = shipDao.findById(create.getId());

		if (dbShip != null) {
			return Response.status(SHIP_NOT_CREATED).entity(DUPLICATE_CONTENT).build();
		}
		if (create == null || create.getId() == null) {
			return Response.status(SHIP_NOT_CREATED).entity(MISSING_CONTENT).build();
		}
		if (!create.getId().getBuilder().equals(dbAcc.getProfileName())) {
			return Response.status(SHIP_NOT_CREATED).entity(SHIP_MISMATCH).build();
		}
		
		getLogger().log(Level.INFO, "This is the byte arr: ");
		StringBuilder sB = new StringBuilder();
		for (byte b : create.getData()) {
			sB.append(b);
			sB.append(", ");
		}
		getLogger().log(Level.INFO, sB.toString());
		shipDao.persist(create);
		
		return Response.status(CREATED).entity(SHIP_SUCCESSFULLY_CREATED).build();
	}
	

	/**
	 * Updates a ships layout.
	 * @param token User token.
	 * @param update New ship parameters.
	 * @return Ok message if successful, error message o/w.
	 */
	@PUT()
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/update/{token}")
	public Response updateShip(@PathParam("token") final String token, final Ship update) {
		if (!isTokenValid(token)) {
			return Response.status(SHIP_NOT_CREATED).entity(WRONG_TOKEN).build();
		}
		
		if (update == null || update.getId() == null) {
			return Response.status(SHIP_NOT_UPDATED).entity(MISSING_CONTENT).build();
		}
		
		Account dbAcc = getAccountDao().findByToken(token);
		update.getId().setBuilder(dbAcc.getProfileName());
		
		Ship dbShip = shipDao.findById(update.getId());
		Account dbAccount = getAccountDao().findByToken(token);
		
		if (dbShip == null || dbAccount == null || !dbAccount.getProfileName().equals(dbShip.getId().getBuilder())) {
			return Response.status(SHIP_NOT_UPDATED).entity(MISSING_CONTENT).build();
		}
		
		shipDao.update(update);
		
		return Response.status(CREATED).entity(SHIP_SUCCESSFULLY_UPDATED).build();
	}
	
	/**
	 * Deletes a specified ship.
	 * @param token user token
	 * @param shipName ship name
	 * @return ok message or error message
	 */
	@DELETE()
	@Path("/delete/{token}/{shipName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteShip(@PathParam("token") final String token, @PathParam("shipName") final String shipName) {
		if (!isTokenValid(token)) {
			return Response.status(SHIP_NOT_DELETED).entity(WRONG_TOKEN).build();
		}
		if (!isStringValid(shipName)) {
			return Response.status(SHIP_NOT_DELETED).entity(INVALID_STRING).build();
		}
		
		Ship dbShip = shipDao.findByExactName(shipName);
		
		if (dbShip == null) {
			return Response.status(SHIP_NOT_DELETED).entity(NAME_DOESNT_EXIST).build();
		}
		
		shipDao.remove(dbShip);
		
		return Response.status(DELETE_OK).entity(SHIP_SUCCESSFULLY_DELETED).build();
	}

	/**
	 * Sends all the ships with names as param as JSON.
	 * @param shipName Name of ship.
	 * @return Ships as JSON or error message if fail.
	 */
	@GET()
	@Path("/find/byName/{shipName}")
	public Response findShipByName(@PathParam("shipName") final String shipName) {
		if (!isStringValid(shipName)) {
			return Response.status(SHIP_NOT_FOUND).entity(NAME_DOESNT_EXIST).build();
		}
		
		List<Ship> ships = shipDao.findByName(shipName);
		
		GenericEntity<List<Ship>> ge = new GenericEntity<List<Ship>>(ships) {
		};
		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Sends all the users created ships as JSON.
	 * @param token user token.
	 * @return Ships as JSON or error message if fail.
	 */
	@GET()
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find/created/{token}")
	public Response findAllCreated(@PathParam("token") final String token) {
		getLogger().log(Level.INFO, "Entered get by creator: ");
		
		if (!isTokenValid(token)) {
			return Response.status(GET_FAILED).entity(WRONG_TOKEN).build();
		}
		getLogger().log(Level.INFO, "Found valid user");
		Account dbAcc = getAccountDao().findByToken(token);
		getLogger().log(Level.INFO, "Trying to get ships");
		List<Ship> ships = shipDao.findByCreator(dbAcc);
		getLogger().log(Level.INFO, "Successfully got ships");
		GenericEntity<List<Ship>> ge = new GenericEntity<List<Ship>>(ships) {
		};
		getLogger().log(Level.INFO, "Created ship list with size: " + ships.size());
		getLogger().log(Level.INFO, "Exit get by creator: ");
		
		for (Ship s : ships) {
			getLogger().log(Level.INFO, "Ship : " + s.getId().getName() + " bytes...");
			StringBuilder sB = new StringBuilder();
			for (byte b : s.getData()) {
				sB.append(b);
				sB.append(", ");
			}
			getLogger().log(Level.INFO, sB.toString());
		}
		
		
		dbAcc.getAccounts2().size();
		dbAcc.getHasscores().size();
		dbAcc.getShips().size();
		return Response.status(STATUS_OK).entity(ge).build();
	}
}
