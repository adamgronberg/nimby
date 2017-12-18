package edu.chalmers.persistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.chalmers.model.Scoreboard;
/**
 * Implements the methods for Scoreboard DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaScoreboardDao extends AbstractJpaDao<String, Scoreboard> implements IScoreboardDao {
	
	private static final String JTAPU = "edu.chalmers.nimby";
	    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;

	/**
	 * Basic constructor.
	 */
	public JpaScoreboardDao() {
		super(Scoreboard.class);
	}
}
