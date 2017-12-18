package edu.chalmers.nimby.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.gameLogic.Part;

/**
 * {@link CameraController} that follows a given {@link Part}.
 * @author Viktor Sjölind
 *
 */
public class CameraControllerFollowShip implements CameraController {
	private OrthographicCamera cam;
	private Part shipToFollow;

	@Override
	public void update() {
		if (Gdx.input.isKeyPressed(Keys.MINUS)) {
			getCam().zoom += ZOOM_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.PLUS) || Gdx.input.isKeyPressed(Keys.PERIOD)) {
			getCam().zoom -= ZOOM_SPEED;
		}

		if (getCam().zoom < MIN_ZOOM) {
			getCam().zoom = MIN_ZOOM;
		}

		if (shipToFollow != null) {
			Vector2 pos = shipToFollow.body.getPosition().cpy().scl(BodyConstants.BOX_TO_WORLD);
			
			float z = getCam().position.z;
			getCam().position.set(pos, z);
		}
		getCam().update();
	}
	
	/**
	 * @param part The {@link Part} to follow with the camera.
	 */
	public void setShipToFollow(final Part part) {
		this.shipToFollow = part;
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
