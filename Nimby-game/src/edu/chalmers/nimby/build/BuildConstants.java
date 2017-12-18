package edu.chalmers.nimby.build;

import edu.chalmers.nimby.model.BodyConstants;

/**
 * constants for the build mode.
 * @author Mikael Stolpe
 *
 */
public interface BuildConstants {
	float OFFSET = 0.0001f;
	float HALF_CIRCLE_ANGLE = 180;
	float FULL_CIRCLE_ANGLE = 360;
	float ANGLE_STEP = 45f; //5f;
	float BOUNDARY_WIDTH = 3840 * BodyConstants.WORLD_TO_BOX;
	float BOUNDARY_HEIGHT = 2160 * BodyConstants.WORLD_TO_BOX;
	float BOUNDARY_WIDTH_CHECK = BOUNDARY_WIDTH * 2 * BodyConstants.BOX_TO_WORLD;
	float BOUNDARY_HEIGHT_CHECK = BOUNDARY_HEIGHT * 2 * BodyConstants.BOX_TO_WORLD;
	float BOUNDARY_THICKNESS = 0.1f;
	float CAMERA_OFFSET = 1.3f;
	float FORCE_MULTIPLIER = 1000f;
	float SHIP_SENSOR_RADIUS = 40f * BodyConstants.WORLD_TO_BOX;
	float EQUIPMENT_SENSOR_RADIUS = 30f * BodyConstants.WORLD_TO_BOX;
	String UNWELD = "unWeld";
	String WELD = "weld";
	String SHIP = "spawn ship";
	String ENGINE = "spawn engine";
	String WEAPON = "spawn weapon";
	String CLEAR = "You must clear the screen before saving";
	String CLEAR_TEXT = "Just do it, tard";
	String CLEAR_OK_TEXT = "Ok, I'm sorry for being so stupid, I'll fix it right away, Sir!";
	String SHIPH = "spawn high ship";
	float HALF_DIVIDER = 0.5f;
	float MAX_TORQUE = 20000;
	float MAX_ZOOM = 5.64f;
	
}
