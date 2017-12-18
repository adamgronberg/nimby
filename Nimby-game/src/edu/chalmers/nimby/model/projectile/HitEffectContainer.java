package edu.chalmers.nimby.model.projectile;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Proof of concept.
 * @author Viktor Sjölind
 *
 */
public class HitEffectContainer implements HitEffect {

	private List<HitEffect> list;
	
	public HitEffectContainer() {
		list = new LinkedList<>();
	}
	
	@Override
	public void resolveHit(final Contact contact) {
		for (HitEffect h: list) {
			h.resolveHit(contact);
		}
	}

}
