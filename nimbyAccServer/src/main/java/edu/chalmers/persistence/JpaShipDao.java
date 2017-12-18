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

import edu.chalmers.model.Account;
import edu.chalmers.model.Ship;
import edu.chalmers.model.ShipPK;
import edu.chalmers.model.Ship_;
/**
 * Implements the methods for Ship DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaShipDao extends AbstractJpaDao<ShipPK, Ship> implements IShipDao {
	
	private static final String JTAPU = "edu.chalmers.nimby";
	    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;
	    
	/**
	 * Basic constructor.
	 */
	public JpaShipDao() {
		super(Ship.class);
	}
	
	@Override
	public List<Ship> findByName(final String name) {
		List<Ship> byName = new LinkedList<>();
		String shipName = name.trim().toLowerCase();
		for (Ship ship : getAll()) {
			String check = ship.getId().getName().trim().toLowerCase();
			if (check.contains(shipName)) {
				byName.add(ship);
			}
		}
		return byName;
	}
	
    @Override
    public Ship findByExactName(final String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ship> query = cb.createQuery(Ship.class);
        Root<Ship> root = query.from(Ship.class);
        query.select(root).where(cb.equal(root.get(Ship_.id.getName()), name));
        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "No such shipName found");
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for ship with exact name: " + name, e);
        }
        return null;
    }

	@Override
	public List<Ship> findByCreator(final Account creator) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ship> query = cb.createQuery(Ship.class);
        Root<Ship> root = query.from(Ship.class);
        List<Ship> list = new LinkedList<>();
        Logger.getAnonymousLogger().log(Level.INFO, "Trying to get list inside jpadao");
        try {
            query.select(root).where(cb.equal(root.get("account"), creator));
            list.addAll(em.createQuery(query).getResultList());
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for ships with builder: " + creator.getProfileName(), e);
        }
        return list;
    }
}
