package edu.chalmers.nimby.util;

import java.util.Comparator;

import com.badlogic.gdx.Graphics.DisplayMode;

/**
 * Compares the width of DisplayModes.
 * @author Viktor Sjölind
 *
 */
public class ModeComparator implements Comparator<DisplayMode> {

	@Override
	public int compare(final DisplayMode mode1, final DisplayMode mode2) {
		if (mode1.bitsPerPixel == mode2.bitsPerPixel) {
			return Integer.compare(mode2.width, mode1.width);
		} else {
			return Integer.compare(mode2.bitsPerPixel, mode1.bitsPerPixel);	
		}
	}

}
