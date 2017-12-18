package edu.chalmers.nimby.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.model.gameLogic.Entity;

/**
 * @author Lucas
 */
public final class StageEntity implements Entity {

	private Stage stage;
	
	public StageEntity(final Stage stage) {
		this.stage = stage;
	}

	@Override
	public void update(final float delta, final OrthographicCamera cam) {
		stage.act(delta);
	}
}
