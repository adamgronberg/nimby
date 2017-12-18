package edu.chalmers.nimby.controller;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.model.Entity;
import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.CameraControllerGeneric;
import edu.chalmers.nimby.util.PipeOut;
import edu.chalmers.nimby.view.ChatWindow;
import edu.chalmers.nimby.view.Renderable;
import edu.chalmers.nimby.view.SeatDescriptor;

public class SeatingScreen extends AbstractControllerScreen {

	private Renderable stageRendrable;
	private final Stage stage;		//Stage for objects to use
	private Entity stageEntity;
	private ChatWindow chatWindow;
	private ScrollPane scrollPane;
	
	//TODO: Needs a class akin to ShipDescriptor, but that shows the seats as buttons.
	
	/**
	 * The constructor for the screen.
	 * @param game The game to be used for the screen.
	 */
	public SeatingScreen(final Nimby game,final ChatWindow chat) {
		super(game, new CameraControllerGeneric());
		this.chatWindow = chat;
		stageRendrable = new Renderable() {
			
			@Override
			public void render(final SpriteBatch batch) {
				Table.drawDebug(stage);
				stage.draw();
			}
			
			@Override public void debug(final ShapeRenderer sr) { }
		};
		
		stageEntity = new Entity() {
			public void update(final float delta, final OrthographicCamera cam) {
				stage.act(delta);
			}
		};
		
		stage = new Stage();
		stage.setCamera(getViewScreen().getCam());
		
		Bundle stageBundle = new Bundle(stageEntity, stageRendrable);
		Bundle chatBundle = new Bundle(chat, null);
		addBundles(stageBundle,chatBundle);
	}

	@Override
	public void dispose() {
		stage.dispose();
		this.dispose();	
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(final int width, final int height) {
		getViewScreen().getCam().setToOrtho(false, 1, height / width);
		if (stage != null) {
			stage.clear();
		} 
		Gdx.input.setInputProcessor(stage);
		
		TextureAtlas atlas = new TextureAtlas("ui/morebuttons.pack");
		Skin skin = new Skin(atlas);
		Table table = new Table(skin);
		Table shipHolder = new Table(skin);
	
		table.setBounds(0, 0, width, height);

		/*Creating bitmaps */
		BitmapFont white = new BitmapFont(Gdx.files.internal("fonts/terminal.fnt"), false);
		
		/* Making a button style */
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("blankbutton");
		textButtonStyle.down = skin.getDrawable("blankbuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.BLACK;
		
		scrollPane = new ScrollPane(shipHolder);
		scrollPane.setScrollingDisabled(true, false);
		
		
		//Creating some ships
		PlayerShip bogus = new PlayerShip();
		bogus.setNames("Asscandy");
		bogus.setNbrOfSeats(3);
		
		PlayerShip bogey = new PlayerShip();
		bogey.setNames("Filthrender");
		bogey.setNbrOfSeats(4);
		
		
		
		shipHolder.add(new SeatDescriptor(bogus));
		shipHolder.row();
		shipHolder.add(new SeatDescriptor(bogey));
		
		table.add(chatWindow);
		table.add(scrollPane);
		table.debug();
		
		 
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		DisplayMode[] modes = Gdx.graphics.getDisplayModes();
		Gdx.graphics.setDisplayMode(modes[0].width, modes[0].height, false);
		
	}

}
