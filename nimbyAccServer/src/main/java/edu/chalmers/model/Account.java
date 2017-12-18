package edu.chalmers.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * The persistent class for the account database table.
 * 
 */
@Entity

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String profileName;

	private String email;

	private String hash;

	private String password;

	private String salt;

	private int shipSlot;

	private String token;

	//bi-directional many-to-many association to Account
	@ManyToMany(fetch = FetchType.EAGER)
	@ElementCollection(targetClass = Account.class)
	@JoinTable(
		name = "friendwith"
		, joinColumns = {
			@JoinColumn(name = "account2")
			}
		, inverseJoinColumns = {
			@JoinColumn(name = "account1")
			}
		)
	private List<Account> accounts1;

	//bi-directional many-to-many association to Account
	@ManyToMany(mappedBy = "accounts1")
	private List<Account> accounts2;

	//bi-directional one-to-one association to FederationMember
	@OneToOne(mappedBy = "accountBean")
	private FederationMember federationmember;

	//bi-directional many-to-one association to HasScore
	@OneToMany(mappedBy = "accountBean")
	private List<HasScore> hasscores;

	//bi-directional many-to-one association to Ship
	@OneToMany(mappedBy = "account")
	private List<Ship> ships;
	
	/**
	 * Empty Constructor.
	 */
	public Account() {
	}
	
	/**
	 * Getter for profileName.
	 * @return profileName
	 */
	public String getProfileName() {
		return this.profileName;
	}
	
	/**
	 * Setter for profileName.
	 * @param pName name
	 */
	public void setProfileName(final String pName) {
		this.profileName = pName;
	}
	
	/**
	 * Getter for email.
	 * 	@return email
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Setter for email.
	 * @param em email
	 */
	public void setEmail(final String em) {
		this.email = em;
	}

	/**
	 * Getter for hash.
	 * @return hash.
	 */
	@JsonIgnore
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * Setter for hash.
	 * @param h hash
	 */
	@JsonProperty
	public void setHash(final String h) {
		this.hash = h;
	}

	/**
	 * Getter for password.
	 * @return password.
	 */
	@JsonIgnore
	public String getPassword() {
		return this.password;
	}

	/**
	 * Setter for password. 
	 * @param pass new password
	 */
	@JsonProperty
	public void setPassword(final String pass) {
		this.password = pass;
	}
	
	/**
	 * Getter for salt.
	 * @return salt.
	 */
	@JsonIgnore
	public String getSalt() {
		return this.salt;
	}
	
	/**
	 * Setter for salt.
	 * @param sa new salt
	 */
	@JsonProperty
	public void setSalt(final String sa) {
		this.salt = sa;
	}
	
	/**
	 * Getter for nr of shipslots.
	 * @return shipslots.
	 */
	@JsonIgnore
	public int getShipSlot() {
		return this.shipSlot;
	}
	
	/**
	 * Setter for nr of shipslots.
	 * @param sSlot new Sslot
	 */
	@JsonProperty
	public void setShipSlot(final int sSlot) {
		this.shipSlot = sSlot;
	}
	
	/**
	 * Getter for token.
	 * @return token.
	 */ 
	@JsonIgnore
	public String getToken() {
		return this.token;
	}
	
	/**
	 * Setter for token.
	 * @param t new token.
	 */
	@JsonProperty
	public void setToken(final String t) {
		this.token = t;
	}
	
	/**
	 * Getter for accounts1. 
	 * @return accounts1.
	 */
	@JsonIgnore
	public List<Account> getAccounts1() {
		return this.accounts1;
	}
	
	/**
	 * Setter for accounts1.
	 * @param acc1 new accounts.
	 */
	@JsonProperty
	public void setAccounts1(final List<Account> acc1) {
		this.accounts1 = acc1;
	}
	
	/**
	 * Getter for accounts2.
	 * @return accounts2.
	 */
	@JsonIgnore
	public List<Account> getAccounts2() {
		return this.accounts2;
	}

	/**
	 * Setter for accounts2.
	 * @param acc2 new accounts.
	 */
	@JsonProperty
	public void setAccounts2(final List<Account> acc2) {
		this.accounts2 = acc2;
	}

	/**
	 * Getter for federationMember.
	 * @return federationMember.
	 */
	@JsonIgnore
	public FederationMember getFederationmember() {
		return this.federationmember;
	}
	
	/**
	 * Setter for federationMember.
	 * @param fedmember 
	 */
	@JsonProperty
	public void setFederationmember(final FederationMember fedmember) {
		this.federationmember = fedmember;
	}
	
	/**
	 * Getter for hasScores.
	 * @return hasScores.
	 */
	@JsonIgnore
	public List<HasScore> getHasscores() {
		return this.hasscores;
	}

	/**
	 * Setter for hasScores.
	 * @param hScores 
	 */
	@JsonIgnore
	public void setHasscores(final List<HasScore> hScores) {
		this.hasscores = hScores;
	}
	
	/**
	 * Adds a hasscore.
	 * @param hscore score to add
	 * @return added score
	 */
	@JsonIgnore
	public HasScore addHasscore(final HasScore hscore) {
		getHasscores().add(hscore);
		hscore.setAccountBean(this);

		return hscore;
	}
	
	/**
	 * Removes a score.
	 * @param hcore score to remove.
	 * @return removed score.
	 */
	@JsonIgnore
	public HasScore removeHasscore(final HasScore hcore) {
		getHasscores().remove(hcore);
		hcore.setAccountBean(null);

		return hcore;
	}
	
	/**
	 * Getter for ships.
	 * @return all created ships.
	 */
	@JsonIgnore
	public List<Ship> getShips() {
		return this.ships;
	}

	/**
	 * Setter for ships.
	 * @param ss ships to set.
	 */
	public void setShips(final List<Ship> ss) {
		this.ships = ss;
	}
	
	/**
	 * Adds a created ship to this account.
	 * @param ship Ship to add.
	 * @return added ship.
	 */
	public Ship addShip(final Ship ship) {
		getShips().add(ship);
		ship.setAccount(this);

		return ship;
	}
	
	/**
	 * Removes a ship from this account.
	 * @param ship to remove
	 * @return removed ship
	 */
	public Ship removeShip(final Ship ship) {
		getShips().remove(ship);
		ship.setAccount(null);

		return ship;
	}

}