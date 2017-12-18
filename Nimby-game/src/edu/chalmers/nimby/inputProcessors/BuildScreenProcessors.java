package edu.chalmers.nimby.inputProcessors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.controller.BuildScreen;
import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.SensorData;
import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Common fields and methods for BuildScreenProcessors.
 * @author Mikael Stolpe
 *
 */
public class BuildScreenProcessors {
	protected Body hitBody;
	protected Body groundBody;
	protected BuildPart selectedEquipment;
	protected OrthographicCamera cam;
	protected World world;
	protected BuildScreen bScreen;
	protected Stage stage;
	/**
	 * Callback method for all the fixture checks.
	 */
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(final Fixture fixture) {
			if (fixture.getBody() == groundBody) {
				//				System.out.println("Body!");
				return true;
			}
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				Object userData = fixture.getUserData();
				if (userData instanceof SensorData) {
					//System.out.println("Hit sensor");
					SensorData sensorData = (SensorData) userData;
					BuildPart part = sensorData.getBuildPart();

					if (part.getPart()instanceof Equipment) {
						//System.out.println("You hit an Equipment");
						selectedEquipment = part;
						return false;
					}

				}  else if (hitBody.getUserData() instanceof BuildPart) {
					if (((BuildPart) hitBody.getUserData()).getTrueUserData() instanceof Equipment) {
						//System.out.println("Hit Equipment");
						selectedEquipment = (BuildPart) hitBody.getUserData();
						return false;
					}
				}
				return true;
			} else {
				return true;
			}
		}
	};
	
	/**
	 * Sets hitBody to whatever body is at location x, y in mouse coordinates.
	 * @param x x cord of mouse pos
	 * @param y y cord of mouse pos
	 */
	protected void setHitBody(final float x, final float y) {
		hitBody = null;
		testPoint.set(x, y, 0);	
		// scale mouse coordinates to right form
		cam.unproject(testPoint);
		testPoint.scl(BodyConstants.WORLD_TO_BOX);
		// query the world for a fixture which the mouse hit
		world.QueryAABB(callback, testPoint.x - BuildConstants.OFFSET, testPoint.y - BuildConstants.OFFSET, testPoint.x + BuildConstants.OFFSET, testPoint.y + BuildConstants.OFFSET);
	}
}
