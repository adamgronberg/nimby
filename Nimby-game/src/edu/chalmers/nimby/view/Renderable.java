package edu.chalmers.nimby.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A component that is renderable by libgdx using a {@link SpriteBatch} and / or a {@link ShapeRenderer}.
 * @author Viktor Sjölind
 *
 */
public interface Renderable {

	/**
	 * Renders the Renderable.
	 * @param batch The SpriteBatch used to render.
	 * @param font 
	 */
	void render(final SpriteBatch batch, BitmapFont font);

	/**
	 * Draws simple graphics used for debugging.
	 * @param sr The Shaperenderer used to draw lines.
	 * @param font 
	 */
	void debug(final ShapeRenderer sr);

}
