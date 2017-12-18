package edu.chalmers.nimby.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.model.gameLogic.ShipPart;

/**
 * ContactListener for the BuildScreen, handles all the collision related to this screen such as sensor collisions.
 * @author Mikael Stolpe
 *
 */
public class NodeSnapper implements ContactListener {

	Body heldBody = null;
	Body notHeldBody = null;
	BuildPart heldPart = null;
	BuildPart notHeldPart = null;
	SensorData heldData = null;
	SensorData notHeldData = null;
	private boolean heldIsShipPart;
	private boolean notHeldIsShipPart;

	@Override
	public final void beginContact(final Contact contact) {
		heldBody = null;
		notHeldBody = null;
		heldData = null;
		notHeldData = null;
		heldPart = null;
		heldIsShipPart = false;
		notHeldIsShipPart = false;
		
//		System.out.println("start");
//		System.out.println("A: " + contact.getFixtureA());
//		System.out.println("B: " + contact.getFixtureB());
//		System.out.println("A : " + (contact.getFixtureA().getUserData() instanceof BuildPart));
//		System.out.println("A : " + (contact.getFixtureA().getUserData() instanceof Part));
//		System.out.println("A : " + (contact.getFixtureA().getUserData() instanceof ShipPart));
//		
//		System.out.println("B : " + (contact.getFixtureB().getUserData() instanceof BuildPart));
//		System.out.println("B : " + (contact.getFixtureB().getUserData() instanceof Part));
//		System.out.println("B : " + (contact.getFixtureB().getUserData() instanceof ShipPart));
				
		// if this holds sensor data it is a sensor, buildpart and in concequence holds a body.
		if (contact.getFixtureA().getUserData() instanceof SensorData && contact.getFixtureB().getUserData() instanceof SensorData) {

			BuildPart tempPart1 = (BuildPart) contact.getFixtureA().getBody().getUserData();
			BuildPart tempPart2 = (BuildPart) contact.getFixtureB().getBody().getUserData();
			
			if (tempPart1.isHeld() || tempPart2.isHeld()) {
				// initilize which one the user is holding.
				if (tempPart1.isHeld()) {
					setHeldNotHeld(contact.getFixtureA(), contact.getFixtureB());
				} else if (tempPart2.isHeld()) {
					setHeldNotHeld(contact.getFixtureB(), contact.getFixtureA());
				} 
				if (heldBody != null) {
					heldPart = heldData.getBuildPart();
					notHeldPart = notHeldData.getBuildPart();
					if (heldData != null) {
						float newAngle = 0;
						// Calculate the new angle if no angle has been set yet
						if (heldPart.getNewAngle() == null) {
							// Checks if we need to compensate for an angle
							boolean rotateNeeded  = heldData.getNormalAngleRad() % MathUtils.PI == 0;
//							System.out.println("rotateNeeded:" + rotateNeeded);
							
							// Calculate new angle
							newAngle += notHeldBody.getAngle() * MathUtils.radiansToDegrees - heldData.getNormalAngleRad() * MathUtils.radiansToDegrees - notHeldData.getNormalAngleRad() * MathUtils.radiansToDegrees;
							
							// compensate if neccesary
							if (rotateNeeded) {
								newAngle += BuildConstants.HALF_CIRCLE_ANGLE;
							}
							
//							System.out.println("change to angle:" + newAngle);
							
							// Shouldn't happen but still here if we overshoot
							if (newAngle < -BuildConstants.HALF_CIRCLE_ANGLE) {
								newAngle += BuildConstants.FULL_CIRCLE_ANGLE;
							}
							if (newAngle >  BuildConstants.HALF_CIRCLE_ANGLE) {
								newAngle -= BuildConstants.FULL_CIRCLE_ANGLE;
							}

							if (notHeldIsShipPart) {
								//System.out.println("held is ship: " + heldIsShipPart + " not held is ship" + notHeldIsShipPart);
								if (heldIsShipPart) {
									heldPart.setRevolute(false);
								} else { 
									heldPart.setRevolute(true);
								}
								// set which are the current sensors involved 
								heldPart.setCurrentSensors(heldData, notHeldData);
								// set the new angle
								heldPart.setNewAngle(newAngle);
								// the new position
//								Vector2 inPos = notHeldData.getBuildPart().getPosition().cpy();
//								inPos.sub(heldData.getBuildPart().getRelativePos());
								heldPart.setPositionData(notHeldPart.getPosition().scl(BodyConstants.WORLD_TO_BOX), notHeldBody.getAngle());
								// and set the part to be welded
								heldPart.setOtherPart(notHeldPart);
							}	

						}
					}
				}
			}
		}
	}
	
	/**
	 * Sets values which is held and not held.
	 * @param fixtureA Held
	 * @param fixtureB Not Held
	 */
	private void setHeldNotHeld(final Fixture fixtureA, final Fixture fixtureB) {
		heldBody = fixtureA.getBody();
		notHeldBody = fixtureB.getBody();
		heldData = (SensorData) fixtureA.getUserData();
		notHeldData = (SensorData) fixtureB.getUserData();
		heldIsShipPart = ((SensorData) fixtureA.getUserData()).getBuildPart().getTrueUserData() instanceof ShipPart;
		notHeldIsShipPart = ((SensorData) fixtureB.getUserData()).getBuildPart().getTrueUserData() instanceof ShipPart;
	}

	@Override
	public void endContact(final Contact contact) {
	}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse contactImpulse) {
	}

	@Override
	public void preSolve(final Contact contact, final Manifold man) {
	}

}
