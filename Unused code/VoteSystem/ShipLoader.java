package edu.chalmers.nimby.model;

import java.util.HashSet;

import com.badlogic.gdx.graphics.OrthographicCamera;

import edu.chalmers.nimby.controller.VoteScreen;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.PipeOut;

/**
 * Loads the ships from the shipPipe then removes itself.
 * @author Adam Grönberg
 *
 */
public class ShipLoader implements Entity {

	private final PipeOut<HashSet<PlayerShip>> shipPipe;
	private final VoteScreen voteScreen;
	
	/**
	 * Constructor.
	 * @param voteScreen the screen the logic should be on.
	 * @param shipPipe shipPipe
	 */
	public ShipLoader(final PipeOut<HashSet<PlayerShip>> shipPipe, final VoteScreen voteScreen) {
		this.shipPipe = shipPipe;
		this.voteScreen = voteScreen;
	}
	
	@Override
	public final void update(final float delta, final OrthographicCamera cam) {
		if (shipPipe.hasNext()) {
			voteScreen.buildShipList(shipPipe.pull());
			voteScreen.removeBundles(new Bundle(this, null));
		}
	}
}
