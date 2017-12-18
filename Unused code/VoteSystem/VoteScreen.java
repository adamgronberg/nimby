package edu.chalmers.nimby.controller;

import java.util.HashSet;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.model.Entity;
import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.model.ShipLoader;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet5ShipVote;
import edu.chalmers.nimby.network.match.MatchServerHandle;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.CameraControllerGeneric;
import edu.chalmers.nimby.util.PipeOut;
import edu.chalmers.nimby.view.ChatWindow;
import edu.chalmers.nimby.view.Renderable;
import edu.chalmers.nimby.view.ShipDescriptor;

/**
 * This screen represents the ship selection screen before a match starts.
 * @author Lucas
 *
 */
public class VoteScreen extends AbstractControllerScreen {

	private static final int NUMBER_OF_VOTES = 3;
	
	private final ChatWindow chatWindow;
	private Renderable stageRendrable;
	private Entity stageEntity;
	private final Stage stage;
	private Table shipTable;
	private ScrollPane scrollPane;
	private final ShipLoader shipLoader;
	private HashSet<ShipDescriptor> shipsDescriptions;

	/**
	 * Sets up the vote screen.
	 * @param game The game to use the screen.
	 * @param chat A modular chat window.
	 * @param shipPipe The outpipe
	 */
	public VoteScreen(final Nimby game, final ChatWindow chat, final PipeOut<HashSet<PlayerShip>> shipPipe) {
		super(game, new CameraControllerGeneric());
		this.chatWindow = chat;
		shipLoader = new ShipLoader(shipPipe, this);
		shipsDescriptions = new HashSet<ShipDescriptor>();
		
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
		Bundle chatBundle = new Bundle(chatWindow, null);
		Bundle voteLogicBundle = new Bundle(shipLoader, null);
		addBundles(stageBundle, chatBundle, voteLogicBundle);
	}

	/**
	 * Disposes of the screen and its resources when they are no longer needed.
	 */
	@Override
	public final void dispose() {
		this.dispose();
		stage.dispose();
	}
	
	/**
	 * Renders the screen. Rebuilds everything when the screen changes size.
	 * @param width The width to resize to.
	 * @param height The height to resize to.
	 */
	@Override
	public final void resize(final int width, final int height) {
		getViewScreen().getCam().setToOrtho(false, 1, height / width);
		if (stage != null) {
			stage.clear();
		}
		
		Gdx.input.setInputProcessor(stage);

		TextureAtlas atlas = new TextureAtlas("ui/morebuttons.pack");
		Skin skin = new Skin(atlas);
		Table container = new Table(skin);
		Table innerHold = new Table(skin);

		container.setBounds(0, 0, width, height);

		/*Creating bitmaps */
		BitmapFont white = new BitmapFont(Gdx.files.internal("fonts/white.fnt"), false);

		/*Creating buttonStyle*/
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("blankbutton");
		textButtonStyle.down = skin.getDrawable("blankbuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.BLACK;
		
		scrollPane = new ScrollPane(shipTable);
		scrollPane.setScrollingDisabled(true, false);
		
		TextButton sendButton = new TextButton("Send Votes", textButtonStyle);
		
		/*Adding buttonfunctions*/
		sendButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				Packet5ShipVote shipsVotedOn = new Packet5ShipVote();
				shipsVotedOn.ships = new HashSet<PlayerShip>();
				for (ShipDescriptor discription: shipsDescriptions) {
					if (discription.isVotedOn()) {
						shipsVotedOn.ships.add(discription.getShip());
					}
				}
				if (shipsVotedOn.ships.toArray().length > NUMBER_OF_VOTES) {
					System.out.println("Wrong number of votes! Have to be less then " + NUMBER_OF_VOTES + " number of votes!");
					return;
				}
				MatchServerHandle.getInstance().sendTCPPacket(shipsVotedOn);
			}
		});

		innerHold.add(sendButton);
		innerHold.row();
		innerHold.add(chatWindow);

		container.left().add(scrollPane);
		container.add(innerHold).bottom().right().expand();
		//container.add(chatWindow);
		container.debug();
		
		stage.addActor(container);
		stage.setViewport(width, height, true);
	}
	
	/**
	 * Displays the screen.
	 */
	@Override
	public final void show() {
		DisplayMode[] modes = Gdx.graphics.getDisplayModes();
		Gdx.graphics.setDisplayMode(modes[0].width, modes[0].height, false);
	}
	
	/**
	 * Fills the scrollPane with all available ships.
	 * @param ships the ships to fill the scrollPane with.
	 */
	public final void buildShipList(final HashSet<PlayerShip> ships) {
		Iterator<PlayerShip> it = ships.iterator();
		shipTable = new Table();
		while (it.hasNext()) {
			ShipDescriptor newShip = new ShipDescriptor(it.next());
			shipsDescriptions.add(newShip);
			shipTable.add(newShip);
			shipTable.row();
		}
	}
	
	@Override public void resume() { }
	@Override public void hide() { }
	@Override public void pause() { }
}
