package edu.chalmers.nimby.util;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;

/**
 * Filter used by {@link ChatWindow}.
 * @author Lucas Wiman
 *
 */
public class TextFilter implements TextFieldFilter {

	private static final int SPACEBAR = 32;
	
	@Override
	public final boolean acceptChar(final TextField textField, final char key) {
		return (key >= 'a' && key <= 'z') || (key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9') 
				|| key == '_' || key == '-' || key == '@' || key == '.' || key == SPACEBAR
				|| key == '/' || key == '?' || key == '!' || key == 'å' || key == 'Å'
				|| key == 'ä' || key == 'Ä' || key == 'ö' || key == 'Ö' || key == '^'
				|| key == ',' || key == '(' || key == ')' || key == '[' || key == ']';
	}

}
