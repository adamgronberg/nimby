package edu.chalmers.model;

import java.io.Serializable;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;


/**
 * The persistent class for the federation database table.
 * 
 */
@Entity
public class Federation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String federationName;

	@Lob
	@Column(columnDefinition = "blob")
	private byte[] logo;

	//bi-directional many-to-one association to FederationMember
	@OneToMany(mappedBy = "federationBean")
	private List<FederationMember> federationmembers;
	
	/**
	 * Empty constructor.
	 */
	public Federation() {
	}
	
	/**
	 * Getter for federation name.
	 * @return fedName
	 */
	public String getFederationName() {
		return this.federationName;
	}
	
	/**
	 * Setter for federation name.
	 * @param fedName new name
	 */
	public void setFederationName(final String fedName) {
		this.federationName = fedName;
	}

	/**
	 * Getter for logo.
	 * @return logo
	 */
	public Object getLogo() {
		return this.logo;
	}

	/**
	 * Getter for logo. 
	 * @param logo
	 */
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	
	/**
	 * Getter for fedmembers.
	 * @return FederationMembers
	 */
	@JsonIgnore
	public List<FederationMember> getFederationmembers() {
		return this.federationmembers;
	}
	
	/**
	 * Getter Setter for fedMem.
	 * @param fedMems new fedMem list
	 */
	@JsonProperty
	public void setFederationmembers(final List<FederationMember> fedMems) {
		this.federationmembers = fedMems;
	}
	
	/**
	 * Add federation Member.
	 * @param newMem new member
	 * @return new member
	 */
	public FederationMember addFederationmember(final FederationMember newMem) {
		getFederationmembers().add(newMem);
		newMem.setFederationBean(this);

		return newMem;
	}
	
	/**
	 * Remove federation member.
	 * @param remove mem to remove
	 * @return old member
	 */

	public FederationMember removeFederationmember(final FederationMember remove) {
		getFederationmembers().remove(remove);
		remove.setFederationBean(null);

		return remove;
	}

}