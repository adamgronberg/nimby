package edu.chalmers.nimby.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author Lucas
 */
public final class StageRenderable implements Renderable {

	private Stage stage;
	
	public StageRenderable(final Stage stage) {
		this.stage = stage;
	}

	@Override
	public void render(final SpriteBatch batch, final BitmapFont font) {
		Table.drawDebug(stage);
	}

	@Override public void debug(final ShapeRenderer sr) { }
}
