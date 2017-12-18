package edu.chalmers.persistence;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Scoreboard;

/**
 * Defines methods specific for ScoreBoard.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/scoreboard")
public interface IScoreboardDao extends Dao<String, Scoreboard> {
	
}
