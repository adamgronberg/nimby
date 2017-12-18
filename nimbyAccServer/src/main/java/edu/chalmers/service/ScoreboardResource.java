package edu.chalmers.service;

import java.util.logging.Level;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.chalmers.persistence.IScoreboardDao;

/**
 * Will handle scoreboard once we have implemented score.
 * @author Mikael Stolpe
 *
 */
@Path("/scoreboard")
@Stateless
public class ScoreboardResource extends AbstractResource {
	
	@EJB
	private IScoreboardDao scoreboardDao; 
	
	/**
	 * Checks that the dao is up and running.
	 * @return A hello message
	 */
	@GET()
	@Produces("text/plain")
	public String sayHello() {
		getLogger().log(Level.INFO, scoreboardDao.toString());
	    return "Scoreboard RestEasy Service";
	}
	
}
