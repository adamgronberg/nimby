package edu.chalmers.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.chalmers.model.Federation;
import edu.chalmers.model.FederationMember;
/**
 * Implements the methods for FederationMember DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaFederationMemberDao extends AbstractJpaDao<String, FederationMember> implements IFederationMemberDao {
	
	private static final String JTAPU = "edu.chalmers.nimby";
	    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;
	 
	/**
	 * Basic constructor.
	 */
	public JpaFederationMemberDao() {
		super(FederationMember.class);
	}
	
	@Override
	public List<FederationMember> getAllMembers(final Federation federation) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FederationMember> query = cb.createQuery(FederationMember.class);
        Root<FederationMember> root = query.from(FederationMember.class);
        List<FederationMember> list = new LinkedList<>();
        try {
            query.select(root).where(cb.equal(root.get("federationBean"), federation));
            list.addAll(em.createQuery(query).getResultList());
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for federationMembers in federation: " + federation.getFederationName(), e);
        }
        return list;
    }
}
