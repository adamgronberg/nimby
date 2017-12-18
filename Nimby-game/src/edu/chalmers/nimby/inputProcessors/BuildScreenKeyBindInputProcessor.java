package edu.chalmers.nimby.inputProcessors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.controller.BuildScreen;
import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Simple processor which captures one key pressed and stores it in an {@link Equipment}.
 * @author Mikael stolpe
 *
 */
public class BuildScreenKeyBindInputProcessor extends BuildScreenProcessors implements InputProcessor {

	private InputMultiplexer mPlexer;

	/**
	 * Initiates the processor.
	 * @param currentWorld World object
	 * @param oCam The camera.
	 */
	public BuildScreenKeyBindInputProcessor(final World currentWorld, final OrthographicCamera oCam, final BuildScreen buildScreen, final Stage st) {
		this.world = currentWorld;
		this.cam = oCam;
		this.stage = st;
		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);
		this.bScreen = buildScreen;
	}
	@Override
	public boolean touchDown(final int x, final int y, final int pointer, final int button) {
		setHitBody(x, y);
		// we hit a correct body
		if (hitBody != null) {
			if (selectedEquipment != null) {
				//System.out.println("Setting new key");
				((Equipment) selectedEquipment.getTrueUserData()).setKey(button);
			}
		}
		mPlexer = new InputMultiplexer(stage, new BuildScreenInputProcessor(world, cam, bScreen, stage));
		Gdx.input.setInputProcessor(mPlexer);
		return false;
	}

	/** another temporary vector. **/
	Vector2 target = new Vector2();

	@Override
	public boolean keyDown(final int keyCode) {
		setHitBody(Gdx.input.getX(), Gdx.input.getY());	
		if (hitBody != null) {
			if (selectedEquipment != null) {
					//System.out.println("Setting new key");
					((Equipment) selectedEquipment.getTrueUserData()).setKey(keyCode);
				}
			}
		mPlexer = new InputMultiplexer(stage, new BuildScreenInputProcessor(world, cam, bScreen, stage));
		Gdx.input.setInputProcessor(mPlexer);
		return false;
	}


	@Override public boolean touchDragged(final int x, final int y, final int pointer) { return false; }
	@Override public boolean touchUp(final int x, final int y, final int pointer, final int button) { return false; }
	@Override public boolean keyTyped(final char character) { return false; }
	@Override public boolean keyUp(final int keyCode) { return false; }
	@Override public boolean mouseMoved(final int screenX, final int screenY) { return false; }
	@Override public boolean scrolled(final int amount) { return false; }

}
