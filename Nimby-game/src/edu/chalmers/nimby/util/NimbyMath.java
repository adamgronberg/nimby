package edu.chalmers.nimby.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Math utilities.
 * @author Mikael Stolpe
 *
 */
public final class NimbyMath {
	
	private NimbyMath() { }
	
	/**
	 * Converts a local body vector to a world vector.
	 * @param angleRad1 Angle of body relative to the world in radians
	 * @param angleRad2 Angle of the other body relative to the body in radians
	 * @param len The length of the vector
	 * @return The converted vector
	 */
	public static Vector2 createWorldVector(final double angleRad1, final double angleRad2, final float len) {
		double angle = angleRad1 + angleRad2;
		double x = Math.cos(angle);
		double y = Math.sin(angle);
		Vector2 result = new Vector2((float) x, (float) y);
		result.scl(len);
		return result;
	}
}
