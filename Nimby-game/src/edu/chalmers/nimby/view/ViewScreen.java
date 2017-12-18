package edu.chalmers.nimby.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;

import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.camera.CameraController;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.model.projectile.Projectile;

/**
 * Manages the view of a {@link AbstractControllerScreen}.
 * @author Viktor Sjölind
 *
 */
public class ViewScreen {

	private OrthographicCamera cam;
	private ShapeRenderer sr;
	private SpriteBatch batch;
	private CameraController camControll;
	private Stage stage;
	private BitmapFont font;
	private List<Renderable> equipmentRenderables;
	private List<Renderable> shipPartRenderables;
	private List<Renderable> renderables;
	private Background background;

	/**
	 * @return the cam
	 */
	public final OrthographicCamera getCam() {
		return cam;
	}

	/**
	 * @return the camControll
	 */
	public final CameraController getCamControll() {
		return camControll;
	}

	/**
	 * Constructor. Initiates the view screen.
	 * @param ocam the camera to use
	 * @param cameraController the camera controller to use
	 */
	public ViewScreen(final OrthographicCamera ocam, final CameraController cameraController) {
		this.cam = ocam;
		//		cam.translate(-Gdx.graphics.getWidth()/2, - Gdx.graphics.getHeight()/2);
		equipmentRenderables = new LinkedList<Renderable>();
		shipPartRenderables = new LinkedList<Renderable>();
		renderables = new LinkedList<Renderable>();
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("fonts/terminal.fnt"), true);

		setCamControll(cameraController);
	}

	/**
	 * Render loop. Renders all entities added to the loop.
	 * @param delta the time since last call
	 */
	public final synchronized void render(final float delta) {
		getCamControll().update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		batch.setProjectionMatrix(stage.getCamera().combined);
		batch.begin();
		if (background != null) {
			background.render(batch);
		}
		batch.end();
		
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		render(shipPartRenderables);
		render(equipmentRenderables);
		render(renderables);
		batch.end();

		sr.setProjectionMatrix(cam.combined);
		sr.begin(ShapeType.Line);
		
		for (Renderable r : shipPartRenderables) {
			r.debug(sr);
		}
		for (Renderable r : equipmentRenderables) {
			r.debug(sr);
		}
		for (Renderable r : renderables) {
			r.debug(sr);
		}
		sr.end();

		//UI render
		if (stage != null) {
			stage.draw();
		}
	}
	
	/**
	 * @param list List to render.
	 */
	private void render(final List<Renderable> list) {
		Iterator<Renderable> itr = list.iterator();
		while (itr.hasNext()) {
			Renderable r = itr.next();
			r.render(batch, font);
		}
		batch.flush();
	}

	/**
	 * Adds a entity to the render loop. Depending on what type it adds them in different 
	 * order to make {@link Equipment} be played over {@link ShipPart}
	 * @param renderable the entity to add to render
	 */
	public final synchronized void add(final Renderable renderable) {
		if (renderable instanceof BuildPart) {
			if (((BuildPart) renderable).getTrueUserData() instanceof Equipment) {
				equipmentRenderables.add(renderable);	
			} else if (((BuildPart) renderable).getTrueUserData() instanceof ShipPart) {
				shipPartRenderables.add(renderable);
			}
		} else if (renderable instanceof Equipment || renderable instanceof Projectile) {
			equipmentRenderables.add(renderable);	
		} else if (renderable instanceof ShipPart) {
			shipPartRenderables.add(renderable);
		} else {
			renderables.add(renderable);
		}

	}

	/**
	 * Removes a entity form the render loop.
	 * @param renderable the entity to remove
	 */
	public final synchronized void remove(final Renderable renderable) {
		if (renderable instanceof BuildPart) {
			if (((BuildPart) renderable).getTrueUserData() instanceof Equipment) {
				equipmentRenderables.remove(renderable);	
			} else if (((BuildPart) renderable).getTrueUserData() instanceof ShipPart) {
				shipPartRenderables.remove(renderable);
			}
		} else if (renderable instanceof Equipment || renderable instanceof ShipPart || renderable instanceof Projectile) {
			if (renderable instanceof Equipment || renderable instanceof Projectile) {
				equipmentRenderables.remove(renderable);	
			} else if (renderable instanceof ShipPart) {
				shipPartRenderables.remove(renderable);
			}
		} else {
			renderables.remove(renderable);
		}
	}

	/**
	 * Sets the cameras view port.
	 * @param width The width of the view 
	 * @param height The height of the view
	 */
	public final void resize(final int width, final int height) {
		cam.viewportHeight = height;
		cam.viewportWidth = width;
	}

	/**
	 * @param camControll the camControll to set
	 */
	public final void setCamControll(final CameraController camControll) {
		this.camControll = camControll;
	}

	/**
	 * @param stage the stage to set
	 */
	public final void setStage(final Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Sets the background for the viewscreen.
	 * @param texture texture to be set.
	 */
	public void setBackground(final Texture texture) {
		background = new Background(texture);
	}
}
