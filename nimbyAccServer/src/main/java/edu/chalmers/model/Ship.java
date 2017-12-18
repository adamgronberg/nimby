package edu.chalmers.model;

import java.io.Serializable;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * The persistent class for the ship database table.
 * 
 */
@Entity
public class Ship implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ShipPK id;

	@Lob
	@Column(columnDefinition = "blob")
	private byte[] data;

	private String description;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name = "builder", insertable = false, updatable = false)
	private Account account;

	/**
	 * Empty constructor.
	 */
	public Ship() {
	}

	/**
	 * Getter for id.
	 * @return id
	 */
	public ShipPK getId() {
		return this.id;
	}

	/**
	 * Setter for id.
	 * @param i 
	 */
	public void setId(final ShipPK i) {
		this.id = i;
	}

	/**
	 * Getter for data.
	 * @return data
	 */
	public byte[] getData() {
		return this.data;
	}

	/**
	 * Getter for data.
	 * @param dat 
	 */
	public void setData(final byte[] dat) {
		this.data = dat;
	}

	/**
	 * Getter for description.
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	
	/**
	 * Getter for description.
	 * @param desc 
	 */ 
	public void setDescription(final String desc) {
		this.description = desc;
	}

	/**
	 * Getter for account.
	 * @return account
	 */
	@JsonIgnore
	public Account getAccount() {
		return this.account;
	}


	/**
	 * Setter for account.
	 * @param acc  
	 */
	@JsonProperty
	public void setAccount(final Account acc) {
		this.account = acc;
	}

}