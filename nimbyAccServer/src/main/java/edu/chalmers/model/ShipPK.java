package edu.chalmers.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ship database table.
 * 
 */
@Embeddable
public class ShipPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String name;

	@Column(insertable = false, updatable = false)
	private String builder;

	/**
	 * Empty constructor.
	 */
	public ShipPK() {
	}
	
	/**
	 * Getter for name.
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Setter for name.
	 * @param n 
	 */
	public void setName(final String n) {
		this.name = n;
	}
	
	/**
	 * Getter for builder.
	 * @return builder
	 */
	public String getBuilder() {
		return this.builder;
	}
	
	/**
	 * Setter for builder.
	 * @param build 
	 */
	public void setBuilder(final String build) {
		this.builder = build;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ShipPK)) {
			return false;
		}
		ShipPK castOther = (ShipPK) other;
		return 
			this.name.equals(castOther.name)
			&& this.builder.equals(castOther.builder);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.name.hashCode();
		hash = hash * prime + this.builder.hashCode();
		
		return hash;
	}
}