package edu.chalmers.nimby.model.equipment.skill;

import com.badlogic.gdx.physics.box2d.Body;

import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Skill that adds a linearImpulse to the equipment.
 * @author Victor
 *
 */
public final class Rotation implements Skill {

	private final float impulse;

	/**
	 * Constructor.
	 * @param force the force to add
	 */
	public Rotation(final float force) {
		this.impulse = force;
	}
	
	@Override
	public void invoke(final Equipment equipment) {
		Body body = equipment.getRevJoint().getBodyB();
		body.setAngularVelocity(impulse);
	}
}
