package edu.chalmers.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the hasscore database table.
 * 
 */
@Entity
public class HasScore implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private HasScorePK id;

	private int score;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name = "account", insertable = false, updatable = false)
	private Account accountBean;

	//bi-directional many-to-one association to Scoreboard
	@ManyToOne
	@JoinColumn(name = "scoreBoard", insertable = false, updatable = false)
	private Scoreboard scoreboard;

	/**
	 * Empty constructor.
	 */
	public HasScore() {
	}

	/**
	 * Gtter for id.
	 * @return id 
	 */
	public HasScorePK getId() {
		return this.id;
	}

	/**
	 * Setter for id.
	 * @param i id
	 */
	public void setId(final HasScorePK i) {
		this.id = i;
	}

	/**
	 * Getter for score.
	 * @return score
	 */
	public int getScore() {
		return this.score;
	}
	
	/**
	 * Setter for score.
	 * @param sc score to set.
	 */
	public void setScore(final int sc) {
		this.score = sc;
	}
	
	/**
	 * Getter for AccBean.
	 * @return accbean
	 */
	public Account getAccountBean() {
		return this.accountBean;
	}
	
	/**
	 * Setter for Accbean.
	 * @param accBean 
	 */
	public void setAccountBean(final Account accBean) {
		this.accountBean = accBean;
	}

	/**
	 * Getter for scoreboard.
	 * @return scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	/**
	 * Setter for scoreboard.
	 * @param scoreB new scoreboard
	 */
	public void setScoreboard(final Scoreboard scoreB) {
		this.scoreboard = scoreB;
	}

}