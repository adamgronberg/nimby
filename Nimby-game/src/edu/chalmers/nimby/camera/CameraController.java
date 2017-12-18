package edu.chalmers.nimby.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;


/**
 * Utility used to control the camera.
 * @author Viktor Sjölind
 *
 */
public interface CameraController {

	float MOVE_SPEED = 5;
	float ZOOM_SPEED = .05f;
	float MIN_ZOOM = 0.1f;
	
	/**
	 *  Updates the camera and reading controlls.
	 */
	void update();
	void setCam(final OrthographicCamera cam);
}
