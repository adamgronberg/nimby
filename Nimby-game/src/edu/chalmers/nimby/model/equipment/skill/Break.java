package edu.chalmers.nimby.model.equipment.skill;

import com.badlogic.gdx.physics.box2d.Body;

import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Skill that adds a linearImpulse to the equipment.
 * @author Victor
 *
 */
public final class Break implements Skill {

	private float forceScale;

	/**
	 * Constructor.
	 * @param forceScale the force to add
	 */
	public Break(final float forceScale) {
		this.forceScale = forceScale;
		
	}
	
	@Override
	public void invoke(final Equipment equipment) {
		Body body = equipment.body;
		body.setLinearVelocity(body.getLinearVelocity().scl(1 / forceScale));
	}
}
