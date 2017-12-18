package edu.chalmers.nimby.inputProcessors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import edu.chalmers.nimby.avatar.Avatar;

/**
 *  InputProcessor for the matchScreen.
 *  Handles key inputs from the user
 * @author Gustav Dahl
 *
 */
public class MatchScreenInputProcessor implements InputProcessor {

	@Override
	public boolean keyDown(final int keycode) {
		//The temp button for calling the avatar
		if (keycode == Input.Keys.SHIFT_RIGHT) { 
			//Sets which type of voice to use
			//Should use voice2text instead
			Avatar.getInstance().setCmd("voice2text");
			new Thread(Avatar.getInstance()).start();
		}
		return false;
	}
	
	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		return false;
	}

}
