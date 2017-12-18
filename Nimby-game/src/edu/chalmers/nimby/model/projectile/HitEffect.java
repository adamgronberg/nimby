package edu.chalmers.nimby.model.projectile;

import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Proof of concept.
 * @author Viktor Sjölind
 *
 */
public interface HitEffect {
	void resolveHit(Contact contact);
}
