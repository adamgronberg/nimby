package edu.chalmers.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.chalmers.model.Federation;
import edu.chalmers.model.Federation_;
/**
 * Implements the methods for Federation DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaFederationDao extends AbstractJpaDao<String, Federation> implements IFederationDao {
	
	private static final String JTAPU = "edu.chalmers.nimby";
	    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;
	    
	/**
	 * Basic constructor.
	 */
	public JpaFederationDao() {
		super(Federation.class);
	}
	
	@Override
	public List<Federation> findByName(final String name) {
		List<Federation> federationByName = new LinkedList<>();
		String fedName = name.trim().toLowerCase();
		for (Federation federation : getAll()) {
			String check = federation.getFederationName().trim().toLowerCase();
			if (check.contains(fedName)) {
				federationByName.add(federation);
			}
		}
		return federationByName;
	}
	
	@Override
    public Federation findByExactName(final String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Federation> query = cb.createQuery(Federation.class);
        Root<Federation> root = query.from(Federation.class);
        query.select(root).where(cb.equal(root.get(Federation_.federationName), name));
        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "No such federation found");
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for federation with exact name: " + name, e);
        }
        return null;
    }
}
