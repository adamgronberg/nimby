package edu.chalmers.nimby.model.projectile;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * A {@link ContactListener} that handles {@link Contact}s for {@link Projectile}s.
 * @author Viktor Sjölind
 *
 */
public class ProjectileColisionHandler implements ContactListener {

	@Override
	public void beginContact(final Contact contact) {
//		System.out.println("Enter BeginContact");
		final Fixture fixtureA = contact.getFixtureA();
		final Fixture fixtureB = contact.getFixtureA();
		
		parseBeginContact(fixtureA, fixtureB, contact);
		parseBeginContact(fixtureB, fixtureA, contact);
//		System.out.println("Exit BeginContact");
	}

	private void parseBeginContact(final Fixture fixtureA, final Fixture fixtureB, final Contact contact) {
//		System.out.println("	Enter parseBeginContact");
		Object userDataA = fixtureA.getUserData();
		Object userDataB = fixtureB.getUserData();
		
//		System.out.println("		UserDataA: " + userDataA);
//		System.out.println("		UserDataB: " + userDataB);
		
		if (userDataA instanceof Projectile) {
			Projectile projectile = (Projectile) userDataA;

			if (fixtureA == projectile.getHitBoxFixture()) {

				//System.out.println("found hitbox");
				projectile.hit(contact);
			} else if (fixtureB == projectile.getAoeFixture() && projectile.hasContact()) {
				projectile.resolveContact(contact);
				//System.out.println("found aoebox");
			}
		}
//		System.out.println("	Exit parseBeginContact");
	}
	
	@Override
	public void endContact(final Contact contact) {
//		System.out.println("Enter EndContact");
		final Fixture fixtureA = contact.getFixtureA();
		final Fixture fixtureB = contact.getFixtureA();

		parseEndContact(fixtureA, fixtureB, contact);
		parseEndContact(fixtureB, fixtureA, contact);
//		System.out.println("Exit EndContact");
	}

	private void parseEndContact(final Fixture fixtureB, final Fixture fixtureA, final Contact contact) {
//		System.out.println("	Enter parseEndContact");
		Object userDataA = fixtureA.getUserData();
		Object userDataB = fixtureB.getUserData();

//		System.out.println("		UserDataA: " + userDataA);
//		System.out.println("		UserDataB: " + userDataB);
		
		if (userDataA instanceof Projectile) {
			Projectile projectile = (Projectile) userDataA;
			
			if (fixtureB == projectile.getCreator()) {
				projectile.arm();
			}
		}
//		System.out.println("	Exit parseEndContact");
	}

	@Override public void preSolve(final Contact contact, final Manifold oldManifold) { }
	@Override public void postSolve(final Contact contact, final ContactImpulse impulse) { }
}
