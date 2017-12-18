package edu.chalmers.nimby;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Main desktop. Starts LWJG application.
 * @author Adam Grönberg
 *
 */
public final class Main {
	
	/**
	 * Util class.
	 */
	private Main() { }
	
	private static final int DEFAULT_WIDTH = 1280;
	private static final int DEFAULT_HEIGHT = 720;
	
	public static void main(final String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Nimby-game";
		cfg.useGL20 = true;
		cfg.width = DEFAULT_WIDTH;
		cfg.height = DEFAULT_HEIGHT;
		new LwjglApplication(new Nimby(), cfg);
	}
}
