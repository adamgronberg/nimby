package edu.chalmers.nimby.model.gameLogic;

import com.badlogic.gdx.graphics.OrthographicCamera;


/**
 * Used as a base for model classes.
 * @author Viktor Sjölind, Adam Gr�nberg
 *
 */
public interface Entity {
	
	/**
	 * Update the entity.
	 * @param delta The time elapsed since last update, in seconds.
	 * @param cam 
	 */
	void update(final float delta, final OrthographicCamera cam);
	
}
