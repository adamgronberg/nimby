package edu.chalmers.nimby.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Contains functions to move and zoom the camera.
 * @author Viktor
 *
 */
public class CameraControllerGeneric implements CameraController {
	private OrthographicCamera cam;
	boolean movementControll;
	
	/**
	 * Initiates a CameraController for the given camera.
	 * All controls are true by default.
	 * @param cam The camera to controll.
	 */
	public CameraControllerGeneric() {
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
	}

}
