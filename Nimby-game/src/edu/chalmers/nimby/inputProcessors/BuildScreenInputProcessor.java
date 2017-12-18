package edu.chalmers.nimby.inputProcessors;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.controller.BuildScreen;
import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.factory.BuildPartGenerator;
import edu.chalmers.nimby.util.Bundle;

/**
 * Hanldes all the inputs when in the {@link BuildScreen}.
 * @author Mikael Stolpe
 *
 */
public class BuildScreenInputProcessor extends BuildScreenProcessors implements InputProcessor {

	private static final float EQUIPMENT_ROTATION_SPEED = 10;
	private MouseJoint mouseJoint;
	private InputMultiplexer mPlexer;

	/**
	 * Initiates the processor.
	 * @param currentWorld World object
	 * @param oCam The camera.
	 * @param buildScreen 
	 * @param st The stage used to render the UI.
	 */
	public BuildScreenInputProcessor(final World currentWorld, final OrthographicCamera oCam, final BuildScreen buildScreen, final Stage st) {
		this.world = currentWorld;
		this.cam = oCam;
		this.stage = st;
		mouseJoint = null;
		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);
		this.bScreen = buildScreen;
	}




	@Override
	public final boolean touchDown(final int x, final int y, final int pointer, final int button) {
		setHitBody(x, y);
		// we hit a correct body
		if (hitBody != null) {
			// if this is a ship define it as held
			boolean bPart = hitBody.getUserData() instanceof BuildPart;
			//System.out.println("part? " + (hitBody.getUserData() instanceof Part));
			//System.out.println("buildpart? " + (hitBody.getUserData() instanceof BuildPart));
			if (bPart) {		
				BuildPart b = (BuildPart) hitBody.getUserData();
				// left button
				if (button == 0) {
					createMouseJoint(b);
				}
				// right button
				if (button == 1) {
					/**
					 * This triggers unWeld, this is however not implemented correctly in ShipPart as of yet
					 */
					//					b.unWeldable(true);
				} 
			}
		}
		return false;
	}


	/**
	 * Creates a mouseJoint for the body.
	 * @param b BuildPart to create the joint for
	 */
	private void createMouseJoint(final BuildPart b) {
		MouseJointDef def = new MouseJointDef();
		b.setHeld(true);
		b.setRotate(false);
		// create and apply mouse joint
		def.bodyA = groundBody;
		def.bodyB = hitBody;
		def.collideConnected = true;
		def.target.set(testPoint.x, testPoint.y);
		def.maxForce = hitBody.getMass() * BuildConstants.FORCE_MULTIPLIER;
		mouseJoint = (MouseJoint) world.createJoint(def);
		hitBody.setAwake(true);
		//System.out.println("joint created");
		//System.out.println(mouseJoint);
	}


	/** another temporary vector. **/
	Vector2 target = new Vector2();

	@Override
	public final boolean touchDragged(final int x, final int y, final int pointer) {
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		if (mouseJoint != null) {
			cam.unproject(testPoint.set(x, y, 0));
			testPoint.scl(BodyConstants.WORLD_TO_BOX);
			mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
		}
		return false;
	}

	@Override
	public final boolean touchUp(final int x, final int y, final int pointer, final int button) {
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			if (hitBody != null && hitBody.getUserData() != null) {
				boolean ship = hitBody.getUserData() instanceof BuildPart;
				if (ship) {
					BuildPart s = (BuildPart) hitBody.getUserData();
					s.setHeld(false);
				}
				world.destroyJoint(mouseJoint);
				mouseJoint = null;
			}
		}
		return false;
	}

	@Override
	public final boolean keyDown(final int keyCode) {
		switch (keyCode) {
		case Keys.B:
			mPlexer = new InputMultiplexer(stage, new BuildScreenKeyBindInputProcessor(world, cam, bScreen, stage));
			Gdx.input.setInputProcessor(mPlexer);
			//			System.out.println("switch fail");
			return true;
		case Keys.SPACE:
			startRotateProcess();
			return true;
		case Keys.Z:
			spawnPart(BuildConstants.SHIP);
			break;
		case Keys.X:
			spawnPart(BuildConstants.SHIPH);
			break;
		case Keys.A:
			spawnPart(BuildConstants.ENGINE);
			break;
		case Keys.Q:
			spawnPart(BuildConstants.WEAPON);
			break;
		case Keys.D:
			deletePart();
			break;
		case Keys.P:
			bScreen.saveShip();
			break;
		case Keys.F11:
			bScreen.clearScreen();
		default:
			break;
		}
		return false;
	}




	@Override
	public final boolean keyUp(final int keyCode) {
		//		switch (keyCode) {
		//		case Keys.SPACE:
		//			System.out.println("PARSE SPACE KEY_UP");
		//			if (selectedEquipment != null) {
		////				selectedEquipment.body.setAngularVelocity(0);
		//				System.out.println("nulling equipment");
		//				selectedEquipment = null;
		//				return true;
		//			}
		//
		//			break;
		//		default:
		//			break;
		//		}
		return false;
	}

	/**
	 * Rotates an equipment.
	 */
	private void startRotateProcess() {
		//System.out.println("PARSE SPACE KEY_DOWN");
		setHitBody(Gdx.input.getX(), Gdx.input.getY());
		//System.out.println(selectedEquipment);
		if (selectedEquipment != null) {
			//System.out.println("Not null, rotate");
			rotateEquipment();
			selectedEquipment = null;
			//			rotate(selectedEquipment, EQUIPMENT_ROTATION_SPEED);
		}

	}
	/**
	 * Rotates an equipment around it's axis.
	 */
	private void rotateEquipment() {
		setHitBody(Gdx.input.getX(), Gdx.input.getY());	
		if (selectedEquipment != null) {
			selectedEquipment.setRotate(true);
		}
	}

	/**
	 * Spawns a new part to the screen.
	 * @param type The pressed key
	 */
	private void spawnPart(final String type) {
		if (mouseJoint == null && hitBody == null) {
			Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			cam.unproject(pos);
			if (pos.x > BuildConstants.BOUNDARY_WIDTH_CHECK || pos.x < 0 
					|| pos.y > BuildConstants.BOUNDARY_HEIGHT_CHECK || pos.y < 0) {
				//System.out.println("outside");
			} else {
				BuildPart b = null;
				if (type.equals(BuildConstants.SHIP)) {
					b = BuildPartGenerator.getShipBuildPart(world, new Vector2(pos.x, pos.y), bScreen.getPipeIn());
				} else if (type.equals(BuildConstants.ENGINE)) {
					b = BuildPartGenerator.getEngineBuildPart(world, new Vector2(pos.x, pos.y), 0, bScreen.getPipeIn());
				} else if (type.equals(BuildConstants.WEAPON)) {
					b = BuildPartGenerator.getWeaponBuildPart(world, new Vector2(pos.x, pos.y), 0, bScreen.getPipeIn());
				} else if (type.equals(BuildConstants.SHIPH)) {
					b = BuildPartGenerator.getShipBuildPartHigh(world, new Vector2(pos.x, pos.y), bScreen.getPipeIn());
				}
				if (b != null) {
					Bundle bundle = new Bundle(b, b);
					bScreen.addBundles(bundle);
					Set<BuildPart> newPartSet = new HashSet<>();
					newPartSet.add(b);
					bScreen.getPartMapSet().put(b, newPartSet);
					bScreen.getPartListSet().add(newPartSet);
					b.getPart().generateShipLocalUniqueID();

				}
			}
		}
	}
	/**
	 * Deletes a part from the screen.
	 */
	private void deletePart() {
		setHitBody(Gdx.input.getX(), Gdx.input.getY());	
		// we hit a correct body
		if (hitBody != null) {
			if (hitBody.getUserData() instanceof BuildPart) {
				BuildPart b = (BuildPart) hitBody.getUserData();
				Set<BuildPart> removeFromThisSet = bScreen.getPartMapSet().remove(b);
				removeFromThisSet.remove(b);
				if (removeFromThisSet.isEmpty()) {
					bScreen.getPartListSet().remove(removeFromThisSet);
				}
				Bundle bundle = new Bundle(b, b);

				world.destroyBody(hitBody);
				bScreen.removeBundles(bundle);
			}
		}
	}



	@Override
	public final boolean mouseMoved(final int screenX, final int screenY) {
		selectedEquipment = null;
		setHitBody(screenX, screenY);
		return false;
	}

	@Override public final boolean scrolled(final int amount) { return false; }
	@Override public final boolean keyTyped(final char character) { return false; }
}
