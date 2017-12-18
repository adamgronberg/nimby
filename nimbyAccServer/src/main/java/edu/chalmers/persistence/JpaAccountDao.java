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
import edu.chalmers.model.Account_;

/**
 * Implements the methods for Account DAO.
 * @author Mikael Stolpe
 *
 */
@Stateless
public class JpaAccountDao extends AbstractJpaDao<String, Account> implements IAccountDao {

	private static final String JTAPU = "edu.chalmers.nimby";

	@PersistenceContext(unitName = JTAPU)
	private EntityManager em;
	
	/**
	 * Basic constructor.
	 */
	public JpaAccountDao() {
		super(Account.class);
	}

	@Override
	public Account findByToken(final String token) {
		CriteriaBuilder cB = em.getCriteriaBuilder();
		CriteriaQuery<Account> query = cB.createQuery(Account.class);
		Root<Account> root = query.from(Account.class);
		query.select(root).where(cB.equal(root.get(Account_.token), token));
		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "No such token found");
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for an Account with token: " + token, e);
		}
		return null;
	}

	@Override
	public List<Account> findByName(final String name) {
		List<Account> accountByName = new LinkedList<>();
		String accName = name.trim().toLowerCase();
		for (Account account : getAll()) {
			String check = account.getProfileName().trim().toLowerCase();
			if (check.contains(accName)) {
				accountByName.add(account);
			}
		}
		return accountByName;
	}

	@Override
	public Account findByMail(final String email) {
		CriteriaBuilder cB = em.getCriteriaBuilder();
		CriteriaQuery<Account> query = cB.createQuery(Account.class);
		Root<Account> root = query.from(Account.class);
		query.select(root).where(cB.equal(root.get(Account_.email), email));
		try {
			return em.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "No such email found");
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception was thrown when searching for an Account with email: " + email, e);
		}
		return null;
	}

}
