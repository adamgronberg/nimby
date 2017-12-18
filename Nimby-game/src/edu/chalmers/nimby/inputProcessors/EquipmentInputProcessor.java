package edu.chalmers.nimby.inputProcessors;

import com.badlogic.gdx.InputProcessor;

import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.network.match.MatchNetwork;
import edu.chalmers.nimby.network.match.MatchServerHandle;

/**
 * An {@link InputProcessor} that handles input for {@link Equipment}.
 * @author Viktor Sjölind
 *
 */
public class EquipmentInputProcessor implements InputProcessor {

	private final Equipment equipment;
	private final int key;
	
	/**
	 * Creates an {@link InputProcessor} that handles input to a specified {@link Equipment} object.
	 * @param equipment The {@link Equipment} object to handle input for.
	 */
	public EquipmentInputProcessor(final Equipment equipment) {
		this.equipment = equipment;
		this.key = equipment.getKey();
	}

	@Override
	public final boolean keyDown(final int key) {
		if (this.key == key && equipment.isCooledDown()) {
			equipment.keyDown();
			return true;
		}
		return false;
	}

	@Override
	public final boolean keyUp(final int key) {
		if (this.key == key) {
			MatchServerHandle.getInstance().sendCommandRequest(equipment.getMatchPartUniqueID().entityID, MatchNetwork.KeyPress.DOWN);
			equipment.keyUp();
			return true;
		}
		return false;
	}

	// NOT USED 
	@Override public final boolean keyTyped(final char arg0) { return false; }
	@Override public final boolean mouseMoved(final int arg0, final int arg1) { return false; }
	@Override public final boolean scrolled(final int arg0) { return false; }
	@Override public final boolean touchDown(final int arg0, final int arg1, final int arg2, final int arg3) { return false; }
	@Override public final boolean touchDragged(final int arg0, final int arg1, final int arg2) { return false; }
	@Override public final boolean touchUp(final int arg0, final int arg1, final int arg2, final int arg3) { return false; }
}
