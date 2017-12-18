package edu.chalmers	.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the scoreboard database table.
 * 
 */
@Entity
public class Scoreboard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String name;

	//bi-directional many-to-one association to HasScore
	@OneToMany(mappedBy = "scoreboard")
	private List<HasScore> hasscores;

	/**
	 * Empty constructor.
	 */
	public Scoreboard() {
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
	 * Getter for hasscores.
	 * @return hasscores
	 */
	public List<HasScore> getHasscores() {
		return this.hasscores;
	}

	/**
	 * Setter for hasscores. 
	 * @param hscores 
	 */
	public void setHasscores(final List<HasScore> hscores) {
		this.hasscores = hscores;
	}

	/**
	 * Add hasscore.
	 * @param hcore 
	 * @return added score
	 */
	public HasScore addHasscore(final HasScore hcore) {
		getHasscores().add(hcore);
		hcore.setScoreboard(this);

		return hcore;
	}
	
	/**
	 * Removes a score.
	 * @param hasscore score to remove
	 * @return removed score
	 */
	public HasScore removeHasscore(final HasScore hasscore) {
		getHasscores().remove(hasscore);
		hasscore.setScoreboard(null);

		return hasscore;
	}

}