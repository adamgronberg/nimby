package edu.chalmers.persistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.chalmers.model.Part;
/**
 * Implements the methods for Part DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaPartDao extends AbstractJpaDao<String, Part> implements IPartDao {
	
	private static final String JTAPU = "edu.chalmers.nimby";
	    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;

	/**
	 * Basic constructor.
	 */
	public JpaPartDao() {
		super(Part.class);
	}
}
