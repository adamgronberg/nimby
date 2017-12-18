package edu.chalmers.nimby.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.model.BodyConstants;

/**
 * Camera controller for the build mode.
 * @author Mikael Stolpe
 *
 */
public class CameraControllerBuild implements CameraController {
	// this is hardcoded, if someone knows a good formula, let me know
	private OrthographicCamera cam;
	boolean movementControll;
	
	/**
	 * Initiates a CameraController for the given camera.
	 * All controls are true by default.
	 * @param cam The camera to control.
	 */
	public CameraControllerBuild() {
		movementControll = true;
	}
	@Override
	public void update() {
		if (movementControll) {
			if (Gdx.input.isKeyPressed(Keys.MINUS)) {
				getCam().zoom += ZOOM_SPEED;
			} else if (Gdx.input.isKeyPressed(Keys.PLUS) || Gdx.input.isKeyPressed(Keys.PERIOD)) {
				getCam().zoom -= ZOOM_SPEED;
			}

			if (getCam().zoom < MIN_ZOOM) {
				getCam().zoom = MIN_ZOOM;
			} else if (getCam().zoom > BuildConstants.MAX_ZOOM) {
				getCam().zoom = BuildConstants.MAX_ZOOM;
			}
			
			float x = 0, y = 0;
			final float calcedMoveSpeed = MOVE_SPEED * getCam().zoom;
					
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				x = -calcedMoveSpeed;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				x = calcedMoveSpeed;
			}

			if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				y = -calcedMoveSpeed;
			} else if (Gdx.input.isKeyPressed(Keys.UP)) {
				y = calcedMoveSpeed;
			}
			getCam().translate(x, y);
			float camX = cam.position.x;
			float camY = cam.position.y;

			Vector2 camMin = new Vector2(cam.viewportWidth, cam.viewportHeight);
			camMin.scl(cam.zoom / 2); //bring to center and scale by the zoom level
			Vector2 camMax = new Vector2(BuildConstants.BOUNDARY_WIDTH * BodyConstants.BOX_TO_WORLD * 2 * BuildConstants.CAMERA_OFFSET, BuildConstants.BOUNDARY_HEIGHT * BodyConstants.BOX_TO_WORLD * 2 * BuildConstants.CAMERA_OFFSET);
			camMax.sub(camMin); //bring to center

			//keep camera within borders
			camX = Math.min(camMax.x, Math.max(camX, camMin.x));
			camY = Math.min(camMax.y, Math.max(camY, camMin.y));

			cam.position.set(camX, camY, cam.position.z);
		}
		getCam().update();

	}
	/**
	 * @return the cam
	 */
	public OrthographicCamera getCam() {
		return cam;
	}
	/**
	 * @param cam the cam to set
	 */
	@Override
	public void setCam(final OrthographicCamera cam) {
		this.cam = cam;
		cam.zoom = BuildConstants.MAX_ZOOM;
	}

}
