package edu.chalmers.nimby.model;

/**
 * Constants used to convert between meters used in BOX2D and actual pixels.
 * @author Viktor Sjölind
 *
 */
public interface BodyConstants {
	float BOX_TO_WORLD = 32;
	float WORLD_TO_BOX = 1 / BOX_TO_WORLD;
}
