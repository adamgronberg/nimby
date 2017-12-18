package edu.chalmers.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the hasscore database table.
 * 
 */
@Embeddable
public class HasScorePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable = false, updatable = false)
	private String account;

	@Column(insertable = false, updatable = false)
	private String scoreBoard;

	/**
	 * Empty constructor.
	 */
	public HasScorePK() {
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
	 * @param acc account
	 */
	public void setAccount(final String acc) {
		this.account = acc;
	}
	
	/**
	 * Getter for scoreboard.
	 * @return sBoard
	 */
	public String getScoreBoard() {
		return this.scoreBoard;
	}
	
	/**
	 * Setter for scoreBoard.
	 * @param sBoard new Scoreboard.
	 */
	public void setScoreBoard(final String sBoard) {
		this.scoreBoard = sBoard;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HasScorePK)) {
			return false;
		}
		HasScorePK castOther = (HasScorePK) other;
		return 
			this.account.equals(castOther.account)
			&& this.scoreBoard.equals(castOther.scoreBoard);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.account.hashCode();
		hash = hash * prime + this.scoreBoard.hashCode();
		
		return hash;
	}
}