package edu.chalmers.persistence;

import java.util.List;

import javax.ejb.Local;

/**
 * Interface for DAO, defines methods used by all DAOs.
 * @author Mikael Stolpe
 *
 * @param <K> Main parameter used for lookup.
 * @param <E> Specfic class used in db.
 */
@Local
public interface Dao<K, E> {

	/**
	 * Creates the entity in the database.
	 * @param entity to create.
	 */
	void persist(final E entity);
	/**
	 * Deletes the entity from the database.
	 * @param entity to delete.
	 */
	void remove(final E entity);
	/**
	 * Updates the entity in the database.
	 * @param entity to update.
	 */
	void update(final E entity);
	/**
	 * Finds an entity with the id id.
	 * @param id to find.
	 * @return Entity if found.
	 */
	E findById(final K id);
	/** 
	    * Returns all objects in the database.
	    * @return all objects
	    */
	List<E> getAll();
	/** 
	    * Gets a list of objects from the database.
	    * @param firstResult start pos for objects.
	    * @param maxResult number of objects in range
	    * @return a list of objects in range.
	    */
    List<E> getRange(final int firstResult, final int maxResult);
    /** 
     * Get the number of objects in the database.
     * @return the total amount of objects.
     */
    int getCount();
}
