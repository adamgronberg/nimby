package edu.chalmers.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Implements methods shared between all DAOs.
 * @author Mikael Stolpe
 *
 * @param <K> Main parameter used for lookup.
 * @param <E> Specfic class used in db.
 */
public abstract class AbstractJpaDao<K, E> implements Dao<K, E> {

	private Class<E> entityClass;
	
	private static final String JTAPU = "edu.chalmers.nimby";
    
	@PersistenceContext(unitName = JTAPU)
	private EntityManager entityManager;
	
	/**
	 * Constructor which initates the DAO with the spcific class used.
	 * @param entityCla Class type to use.
	 */
	public AbstractJpaDao(final Class<E> entityCla) {
		this.setEntityClass(entityCla);
	}

	@Override
	public void persist(final E entity) { 
		getEntityManager().persist(entity); 
	}
	
	@Override
	public void remove(final E entity) { 
		getEntityManager().remove(entity); 
	}
	
	@Override
	public void update(final E entity) {
		getEntityManager().merge(entity);
	}
	
	@Override
	public E findById(final K id) { 
		return getEntityManager().find(getEntityClass(), id); 
	}
	
   @Override
   public List<E> getRange(final int first, final int items) {
       List<E> found = new ArrayList<>();
       CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
       cq.select(cq.from(getEntityClass()));
       Query q = getEntityManager().createQuery(cq);
       q.setFirstResult(first);
       q.setMaxResults(items);
       
       found.addAll(q.getResultList());
       
       return found;
   }
   
   @Override
   public int getCount() {
       CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
       Root<E> rt = cq.from(getEntityClass());
       cq.select(getEntityManager().getCriteriaBuilder().count(rt));
       Query q = getEntityManager().createQuery(cq);
       int count = ((Long) q.getSingleResult()).intValue();
       return count;
   }
   
   @Override
   public List<E> getAll() {
       return getRange(0, getCount());
   }

   /**
    * Getter for entitymanager.
    * @return entityManager
    */
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * Setter for entityManager.
	 * @param entityMana new entityManager.
	 */
	public void setEntityManager(final EntityManager entityMana) {
		this.entityManager = entityMana;
	}
	
	/**
	 * Getter for entityClass.
	 * @return entityClass.
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}
	
	/**
	 * Setter for entityClass.
	 * @param entityCla new entityClass.
	 */
	public void setEntityClass(final Class<E> entityCla) {
		this.entityClass = entityCla;
	}

}