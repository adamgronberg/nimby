package edu.chalmers.nimby.inputProcessors;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.InputProcessor;

/**
 * A composit for {@link InputProcessor} that broadcasts an input event to a list of {@link InputProcessor}s.
 * @author Viktor Sjölind
 *
 */
public final class InputBroadcaster implements InputProcessor {

	private List<InputProcessor> list;
	
	/**
	 * Creates an InputBroadcaster with an empty list of target {@link InputProcessor}s.
	 */
	public InputBroadcaster() {
		list = new LinkedList<>();
	}
	
	/**
	 * @param inputProcessor The {@link InputProcessor} to add.
	 */
	public void addInputProcessor(final InputProcessor inputProcessor) {
		list.add(inputProcessor);
	}
	

	/**
	 * @param inputProcessor The {@link InputProcessor} to remove.
	 * @return True on success, else false.
	 */
	public boolean removeInputProcessor(final InputProcessor inputProcessor) {
		return list.remove(inputProcessor);
	}
	
	@Override
	public boolean keyDown(final int keycode) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.keyDown(keycode)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean keyUp(final int keycode) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.keyUp(keycode)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean keyTyped(final char character) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.keyTyped(character)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.touchDown(screenX, screenY, pointer, button)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.touchUp(screenX, screenY, pointer, button)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.touchDragged(screenX, screenY, pointer)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.mouseMoved(screenX, screenY)) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean scrolled(final int amount) {
		boolean ret = false;
		for (InputProcessor ip: list) {
			if (ip.scrolled(amount)) {
				ret = true;
			}
		}
		return ret;
	}

}
