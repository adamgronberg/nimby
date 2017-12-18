package edu.chalmers.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


/**
 * The persistent class for the part database table.
 * 
 */
@Entity
public class Part implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String name;

	private int hp;

	private int mass;

	@Lob
	@Column(columnDefinition = "blob")
	private byte[] skillCode;

	private String texture;

	/**
	 * Empty constructor.
	 */
	public Part() {
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
	 * @param na 
	 */
	public void setName(final String na) {
		this.name = na;
	}

	/**
	 * Getter for hp.
	 * @return hp 
	 */
	public int getHp() {
		return this.hp;
	}
	
	/**
	 * Setter for hp.
	 * @param h 
	 */
	public void setHp(final int h) {
		this.hp = h;
	}
	
	/**
	 * Getter for mass.
	 * @return mass
	 */
	public int getMass() {
		return this.mass;
	}
	
	/**
	 * Setter for mass.
	 * @param m 
	 */
	public void setMass(final int m) {
		this.mass = m;
	}

	/**
	 * Getter for skillcode.
	 * @return skillcode.
	 */
	public byte[] getSkillCode() {
		return this.skillCode;
	}
	
	/**
	 * Setter for skillcode.
	 * @param skillC 
	 */
	public void setSkillCode(final byte[] skillC) {
		this.skillCode = skillC;
	}

	/**
	 * Getter for texture.
	 * @return texture
	 */
	public String getTexture() {
		return this.texture;
	}

	/**
	 * Setter for texture.
	 * @param text 
	 */
	public void setTexture(final String text) {
		this.texture = text;
	}
}
