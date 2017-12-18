package edu.chalmers.nimby.model.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;

import edu.chalmers.nimby.model.gameLogic.Part;
/**
 * Proof of concept.
 * @author Viktor Sjölind
 *
 */
public class KnockbackOnHit implements HitEffect {

	private float forceMagnitude;
	
	public KnockbackOnHit(final float forceMagnitude) {
		this.forceMagnitude = forceMagnitude;
	}

	@Override
	public void resolveHit(final Contact contact) {
		// Fixture A is the source.
		// Fixture B is the target.
		Object targetData = contact.getFixtureB().getUserData();
		if (targetData instanceof Part) {
			Part targetPart = (Part) targetData;
			Vector2 force = new Vector2();
			
			force.add(contact.getFixtureA().getBody().getPosition());
			force.sub(targetPart.body.getPosition());
			force.scl(forceMagnitude);
			
			targetPart.body.applyForceToCenter(force, true);
		}
	}

}
