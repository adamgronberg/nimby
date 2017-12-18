package edu.chalmers.nimby.controller;

import java.util.List;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.camera.CameraController;
import edu.chalmers.nimby.model.ModelScreen;
import edu.chalmers.nimby.model.gameLogic.EntityHandeler;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.Pipe;
import edu.chalmers.nimby.util.PipeIn;
import edu.chalmers.nimby.view.StageEntity;
import edu.chalmers.nimby.view.StageRenderable;
import edu.chalmers.nimby.view.ViewScreen;

/**
 * The base of every ControllerScreen.
 * @author Viktor Sjölind
 *
 */
public abstract class AbstractControllerScreen implements Screen {

	private static Pipe<Bundle> bundlePipe;
	
	private final ModelScreen modelScreen;
	private final ViewScreen viewScreen;
	private final OrthographicCamera cam;
	private final CameraController camControll;
	protected final Nimby game;
	protected final Stage stage;

	/**
	 * Initiates the basics.
	 * @param game The main instance of the Nimby class that sets current screen.
	 * @param camControll the camController used by the screen
	 */
	public AbstractControllerScreen(final Nimby game, final CameraController camControll) {
		this.game = game;
		stage = new Stage();
		bundlePipe = new Pipe<Bundle>();

		StageRenderable stageRendrable = new StageRenderable(stage);
		StageEntity stageEntity = new StageEntity(stage);
		cam = new OrthographicCamera();
		cam.setToOrtho(true);
		
		this.camControll = camControll;
		camControll.setCam(cam);
		modelScreen = new ModelScreen(cam);
		viewScreen = new ViewScreen(cam, camControll);
		viewScreen.setStage(stage);
		
		Bundle stageBundle = new Bundle(stageEntity, stageRendrable);
		addBundles(stageBundle);
	}
	
	/**
	 * Adds provided bundles to the screen.
	 * @param bundles The bundles to add, can be null.
	 */
	public final void addBundles(final Bundle ... bundles) {
		if (bundles != null) {
			for (Bundle b: bundles) {
				if (b.entity != null) {
					modelScreen.add(b.entity);
				}
				if (b.renderable != null) {	
					viewScreen.add(b.renderable);
				}
			}
		}
	}
	
	/**
	 * Removes provided bundles to the screen.
	 * @param bundles The bundles to add, can be null.
	 */
	public final void removeBundles(final Bundle ... bundles) {
		if (bundles != null) {
			for (Bundle b: bundles) {
				if (b.entity != null) {
					modelScreen.remove(b.entity);
				}
				if (b.renderable != null) {	
					viewScreen.remove(b.renderable);
				}
			}
		}
	}
	
	/**
	 * Adds the {@link EntityHandeler} to update cycle.
	 * @param entityHandeler {@link EntityHandeler} that should be in update cycle.
	 */
	protected final synchronized void addEntityHandeler(final EntityHandeler entityHandeler) {
		modelScreen.addEntityHandeler(entityHandeler);
	}
	
	/**
	 * Removes the entity to the update cycle.
	 * @param entityHandeler {@link EntityHandeler} that should be removed from update cycle.
	 */
	public final synchronized void removeEntityHandeler(final EntityHandeler entityHandeler) {
		modelScreen.removeEntityHandeler(entityHandeler);
	}
	
	/**
	 * @return list of all {@link Entity} in the screen.
	 */
	public final List<IdentifiableEntity> getIdentifiableEntities() {
		return modelScreen.getIdentifiableEntities();
	}
	
	/**
	 * Used as the GAME LOOP.
	 * @param delta The time elapsed since last frame in seconds
	 */
	@Override
	public final void render(final float delta) {
		if (bundlePipe.hasNext()) {
			addBundles(bundlePipe.pull());
		}
		viewScreen.render(delta);
		modelScreen.update(delta);
	}
	
	/**
	 * Gets the identifiableEntityPipe that can be used to add entities from external threads safely.
	 * @param identifiableEntityPipe
	 */
	public static final PipeIn<Bundle> getAddBundlePipe() {
		return bundlePipe;
	}
	
	/**
	 * Sets the identifiableEntityPipe that can be used to add entities from external threads safely.
	 * @param identifiableEntityPipe
	 */
	public static final void setBundlePipe(final Pipe<Bundle> bundlePipe) {
		AbstractControllerScreen.bundlePipe = bundlePipe;
	}

	/**
	 * @return the modelScreen
	 */
	public final ModelScreen getModelScreen() {
		return modelScreen;
	}

	/**
	 * @return the viewScreen
	 */
	public final ViewScreen getViewScreen() {
		return viewScreen;
	}
	
	/**
	 * @return the game
	 */
	public final synchronized Nimby getGame() {
		return game;
	}
	
	/**
	 * @return the camera
	 */
	public final OrthographicCamera getCamera() {
		return cam;
	}
	
	@Override public final void show() { }

	/**
	 * @return the camControll
	 */
	public final CameraController getCamControll() {
		return camControll;
	}
	
	/**
	 * Sets the background used by the screen.
	 * @param texture Texture to be used for the background.
	 */
	public void setBackground(final Texture texture) {
		viewScreen.setBackground(texture);
	}
}
