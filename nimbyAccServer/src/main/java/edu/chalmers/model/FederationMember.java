package edu.chalmers.model;

import java.io.Serializable;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * The persistent class for the federationmember database table.
 * 
 */
@Entity
public class FederationMember implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String account;

	private String rank;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name = "account")
	private Account accountBean;

	//bi-directional many-to-one association to Federation
	@ManyToOne
	@JoinColumn(name = "federation")
	private Federation federationBean;
	
	/**
	 * Empty contstructor.
	 */
	public FederationMember() {
	}

	/**
	 * Getter for account.
	 * @return account
	 */
	public String getAccount() {
		return this.account;
	}
	
	/**
	 * Setter for account.
	 * @param acc 
	 */
	public void setAccount(final String acc) {
		this.account = acc;
	}

	/**
	 * Getter for account.
	 * @return account
	 */
	public String getRank() {
		return this.rank;
	}

	/**
	 * Setter for rank.
	 * @param r 
	 */
	public void setRank(final String r) {
		this.rank = r;
	}
	
	/**
	 * Getter for account.
	 * @return account
	 */

	@JsonIgnore
	public Account getAccountBean() {
		return this.accountBean;
	}

	/**
	 * Setter for account bean.
	 * @param accBean 
	 */
	@JsonProperty
	public void setAccountBean(final Account accBean) {
		this.accountBean = accBean;
	}

	/**
	 * Getter for account.
	 * @return account
	 */
	@JsonIgnore
	public Federation getFederationBean() {
		return this.federationBean;
	}

	
	/**
	 * Setter for federationBean.
	 * @param fedBean 
	 */
	@JsonProperty
	public void setFederationBean(final Federation fedBean) {
		this.federationBean = fedBean;
	}

}