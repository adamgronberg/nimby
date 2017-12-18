package edu.chalmers.nimby.util;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;

import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.model.SensorData;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.gameLogic.ShipPart;

/**
 * Utitilty classes for Part.
 * @author Mikael Stolpe
 *
 */
public final class PartsUtil {
	
	private PartsUtil() { }
	
	/**
	 * Gets the root of the parents.
	 * @param ship one start part
	 * @return the root
	 */
	public static ShipPart getRoot(final ShipPart ship) {
//		System.out.println(ship.isRoot());
		if (ship.isRoot()) {
			return ship;
		}
		ShipPart next = null;
		if (ship.getParent() == null) {
			//System.out.println("getRoot: no valid root here");
			throw new IllegalArgumentException("No valid root!");
		}
		next = (ShipPart) ship.getParent();
		ShipPart root = getRoot(next);
		return root;
	}
	

	/**
	 * Copies a Fixture and adds it to the given Body.
	 * @param fixture the {@link Fixture} to copy.
	 * @param body The {@link Body} to add it to.
	 */
	public static void copyFixtureToBody(final Fixture fixture, final Body body) {
		// CREATE A FIXTURE DEF THAT REPRESENTS THE FIXTURE
		FixtureDef def = new FixtureDef();
		def.density = fixture.getDensity();
		def.filter.categoryBits = fixture.getFilterData().categoryBits;
		def.filter.groupIndex = fixture.getFilterData().groupIndex;
		def.filter.maskBits = fixture.getFilterData().maskBits;
		def.friction = fixture.getFriction();
		def.isSensor = fixture.isSensor();
		def.restitution = fixture.getRestitution();

		// VARIABLES USED IN THE POSITIONING ALGORITHMS BELOW
		Vector2 targetBodyPos = body.getPosition().cpy();
		Vector2 fixtureBodyPos = fixture.getBody().getPosition().cpy();
		Shape shape = fixture.getShape();
		
		if (fixture.getUserData() instanceof ShipPart) {
			ShipPart shipPart = (ShipPart) fixture.getUserData();

			// Update relative pos and angle
			float angle = shipPart.getAngle();
			Vector2 temp = fixtureBodyPos.sub(targetBodyPos);
			shipPart.getRelativePos().add(temp);
			shipPart.getRelativePos().rotate(angle);
			shipPart.internalAngle += body.getAngle() - shipPart.internalAngle;
			
			// Update body reference
			shipPart.body = body;

			// Copy joint list
			List<RevoluteJoint> tempJoints = new LinkedList<>();
			for (RevoluteJoint rj : shipPart.getJoints()) {
				tempJoints.add(rj);
			}
			
			// Clear the joint list
			shipPart.getJoints().clear();
			
			// Re add the joint list to the new body.
			for (RevoluteJoint rj : tempJoints) {
				Equipment equipment = (Equipment) rj.getUserData();
				equipment.createRevoluteJoint(shipPart, equipment.getJointRelativePos());
			}
			
		} 
	
		// SET POSITION OF SHAPE
		// -------- CASE: SHAPE IS A POLYGON SHAPE ---------------------------------------------------------
		if (shape instanceof PolygonShape) {
			PolygonShape polygonShape = (PolygonShape) shape;
			float[] newVertexeses = new float[polygonShape.getVertexCount() * 2];
			for (int i = 0; i < newVertexeses.length; i += 2) {
				Vector2 vectorPos = new Vector2();
				polygonShape.getVertex(i / 2, vectorPos);
				vectorPos.add(fixtureBodyPos);
				
				//				Vector2 pos = vectorPos;
				vectorPos.rotate(MathUtils.radiansToDegrees * body.getAngle() * -1);
				newVertexeses[i] = vectorPos.x;
				newVertexeses[i + 1] = vectorPos.y;
			}
			polygonShape.set(newVertexeses);


			//--------- CASE: SHAPE IS A CIRCLE SHAPE --------------------------------------------------
		} else if (shape instanceof CircleShape) {
			CircleShape circleShape = (CircleShape) shape;

			Vector2 newPosition = circleShape.getPosition();
			if (fixture.getBody().getUserData() instanceof BuildPart) {
				BuildPart bp = (BuildPart) fixture.getBody().getUserData();
				newPosition.add(bp.getPart().getRelativePos());
			}

			newPosition.rotate(MathUtils.radiansToDegrees * body.getAngle() * -1);
			circleShape.setPosition(newPosition);
		}
		
		// SET SHAPE AND CREATE NEW FIXTURE WITH USER DATA
		def.shape = shape;
		Fixture newFixture = body.createFixture(def);
		newFixture.setUserData(fixture.getUserData());
		
		if (fixture.getUserData() instanceof Part) {
			Part part = (Part) fixture.getUserData();
			
			part.setFixture(newFixture);
		} else if (fixture.getUserData() instanceof SensorData) {
			SensorData sensorData = (SensorData) fixture.getUserData();
			BuildPart bPart = (BuildPart) sensorData.getBuildPart();
			
			bPart.getSensors().add(newFixture);
		}
	}
}
