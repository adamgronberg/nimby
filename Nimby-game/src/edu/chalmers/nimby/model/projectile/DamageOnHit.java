package edu.chalmers.nimby.model.projectile;

import com.badlogic.gdx.physics.box2d.Contact;

import edu.chalmers.nimby.model.gameLogic.Part;
/**
 * Proof of concept.
 * @author Viktor Sjölind
 *
 */
public class DamageOnHit implements HitEffect {

	private int damage;

	public DamageOnHit(final int dmg) {
		this.damage = dmg;
	}
	
	@Override
	public void resolveHit(final Contact contact) {
		// Fixture A is the source.
		// Fixture B is the target.
		Object targetData = contact.getFixtureB().getUserData();
		if (targetData instanceof Part) {
			Part targetPart = (Part) targetData;
			targetPart.hurt(damage);
		}
	}

}
