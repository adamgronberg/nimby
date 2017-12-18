package edu.chalmers.nimby.model.gameLogic;

import edu.chalmers.nimby.controller.AbstractControllerScreen;

/**
 * Entity that handles other entities.
 * @author Adam Grönberg
 *
 */
public abstract class EntityHandeler {
	
	protected final AbstractControllerScreen screen;
	
	/**
	 * Constructor.
	 * @param screen the {@link AbstractControllerScreen} that the {@link EntityHandeler} runs on.
	 */
	public EntityHandeler(final AbstractControllerScreen screen) {
		this.screen = screen;
	}
	
	/**
	 * Runs the EntityHandelers command then removes it from screens EntityHandeler list.
	 */
	public final void run() {
		command();
		screen.removeEntityHandeler(this);
	}
	
	
	/**
	 * Runs a set of instructions.
	 */
	public abstract void command();
}
