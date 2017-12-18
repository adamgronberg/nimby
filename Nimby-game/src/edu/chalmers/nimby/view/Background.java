package edu.chalmers.nimby.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A class used for backgrounds on the screens.
 * @author Lucas
 *
 */
public final class Background {
	
	private final Sprite sprite;
	
	/**
	 * Sets up the Background.
	 * @param texture The texture used for the background.
	 */
	public Background(final Texture texture) {
		if (texture == null) {
			throw new IllegalArgumentException("Texture cant be null");
		}
		this.sprite = new Sprite(texture);
	}
	
	/**
	 * Renders the background.
	 * @param batch The spritebatch
	 */
	public void render(final SpriteBatch batch) {
		batch.draw(sprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
	}

	



}
