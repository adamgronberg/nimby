package edu.chalmers.nimby.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Functions to handle positioning.
 * @author Adam Grönberg
 *
 */
public interface Position {
	
	/**
	 * @return The position of the entity in pixels.
	 */
	Vector2 getPosition();
	
	/**
	 * @return The rotation of the entity in degrees.
	 */
	float getAngle();
}
