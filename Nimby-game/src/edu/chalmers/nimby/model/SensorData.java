package edu.chalmers.nimby.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import edu.chalmers.nimby.build.BuildPart;

/**
 * Holds all relevant data for the games sensor objects.
 * @author Mikael Stolpe
 *
 */
public class SensorData {
	private final float relativeAngle;
	private final Vector2 relativePos;
	private Vector2 linearPoleration;
	private boolean occupied;
	private float width;
	private float height;
	private BuildPart buildPart;

	/**
	 * 
	 * @param rAngle relative angle in radians.
	 * @param rPos relative position
	 * @param buildPart 
	 */
	public SensorData(final float rAngle, final Vector2 rPos, final BuildPart buildPart) {
		this.relativeAngle = rAngle;
		this.relativePos = rPos;
		this.occupied = false;
		this.buildPart = buildPart;
	}
	
	/**
	 * @return the angle in degress
	 */
	public final float getAngleDeg() {
		return relativeAngle * MathUtils.radiansToDegrees;
	}
	
	/**
	 * 
	 * @return relativeAngle
	 */
	public final float getAngleRad() {
		return relativeAngle;
	}
	/**
	 * @return the relativePos
	 */
	public final Vector2 getRelativePos() {
		return relativePos.cpy().scl(BodyConstants.BOX_TO_WORLD);
	}
	/**
	 * @return the linearPoleration
	 */
	public final Vector2 getLinearPoleration() {
		return linearPoleration;
	}
	/**
	 * @param linearPoleration the linearPoleration to set
	 */
	public final void setLinearPoleration(final Vector2 linearPoleration) {
		this.linearPoleration = linearPoleration;
	}
	
	/**
	 * Gets the angle which is 90 degrees from the sensors middlepoint.
	 * @return normal as radians.
	 */
	public final float getNormalAngleRad() {
		Vector2 angleVec = relativePos.cpy().sub(linearPoleration.cpy());
		float angle = MathUtils.atan2(angleVec.y, angleVec.x);
		return angle;
	}
	/**
	 * @return the occupied
	 */
	public final boolean isOccupied() {
		return occupied;
	}
	/**
	 * @param occupied the occupied to set
	 */
	public final void setOccupied(final boolean occupied) {
		this.occupied = occupied;
	}
	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}
	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(final float height) {
		this.height = height;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(final float width) {
		this.width = width;
	}

	/**
	 * @return the buildPart
	 */
	public final BuildPart getBuildPart() {
		return buildPart;
	}

}
