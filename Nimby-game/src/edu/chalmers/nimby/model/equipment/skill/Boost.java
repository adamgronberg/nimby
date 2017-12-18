package edu.chalmers.nimby.model.equipment.skill;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Skill that adds a linearImpulse to the equipment.
 * @author Victor
 *
 */
public final class Boost implements Skill {

	private final Vector2 force;

	/**
	 * Constructor.
	 * @param forceScale the force to add
	 */
	public Boost(final float forceScale) {
		this.force = new Vector2(0, forceScale);
	}
	
	@Override
	public void invoke(final Equipment equipment) {
//		Body body = equipment.getRevJoint().getBodyB();
		Body body = equipment.body;
		
		Vector2 pos = body.getPosition();
		Vector2 force2Use = force.cpy().rotate(body.getAngle() * MathUtils.radiansToDegrees);
		
		//System.out.println(force2Use);
		body.applyLinearImpulse(force2Use, pos, true);
	}
}
