package edu.chalmers.nimby.model;

import com.badlogic.gdx.graphics.OrthographicCamera;

import edu.chalmers.nimby.Nimby;

/**
 * Used to start a VoteScreen after a game is found and ready.
 * @author Adam Grönberg
 *
 */
public class VoteScreenChanger implements Entity {

	private boolean matchFound = false;
	private Nimby game;
	
	/**
	 * Constructor.
	 * @param game Nimby
	 */
	public VoteScreenChanger(final Nimby game) {
		this.game = game;
	}

	@Override
	public final void update(final float delta, final OrthographicCamera cam) {
		synchronized (this) {
			if (matchFound) {
				game.createAndSetVoteScreen();
			}
		}
	}

	/**
	 * Sets the ready status to true.
	 */
	public final synchronized void setMatchFound() {
		matchFound = true;
	}
}
