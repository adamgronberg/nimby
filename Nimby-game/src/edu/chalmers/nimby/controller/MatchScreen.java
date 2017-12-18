package edu.chalmers.nimby.controller;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.build.BuildEdges;
import edu.chalmers.nimby.camera.CameraControllerFollowShip;
import edu.chalmers.nimby.inputProcessors.InputBroadcaster;
import edu.chalmers.nimby.inputProcessors.MatchScreenInputProcessor;
import edu.chalmers.nimby.model.gameLogic.EntitySync;
import edu.chalmers.nimby.model.gameLogic.ProjectileRemover;
import edu.chalmers.nimby.model.gameLogic.ShipBuilder;
import edu.chalmers.nimby.model.projectile.ProjectileColisionHandler;
import edu.chalmers.nimby.network.match.EntitySnapshot;
import edu.chalmers.nimby.network.match.MatchServerHandle;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.view.ChatWindow;

/**
 * The ControllerScreen that manages the actual game (match).
 * @author Viktor Sjölind
 *
 */
public final class MatchScreen extends AbstractControllerScreen {
	
	private static final int BUTTON_WIDTH = 250;
	private static final int BUTTON_HEIGHT = 50;
	private static final int FONT_SCALER = 4;
	private final InputBroadcaster inputBroadcaster;
	private final ChatWindow chatWindow;
	private final BitmapFont white;
	private final TextureAtlas atlas;
	private final Skin skin;
	private Table table;
	private Table buttonHolder;
	private TextFieldStyle textFieldStyle;
	private TextButtonStyle textButtonStyle;
	private Table chatWindowHolder;
	private InputMultiplexer inputMultiplexer;
	
	private TextButton disconnectButton;
	private int shipsBuilt;
	
	/**
	 * Constructs a GameController with a world and predefined Bundles.
	 * @param game The Nimby instance that manages the screens.
	 */
	public MatchScreen(final Nimby game) {
		super(game, new CameraControllerFollowShip());
		chatWindow = game.getChatWindow();
		
		white = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
		float scaleX = white.getScaleX();
		float scaleY = white.getScaleY();
		white.setScale(scaleX / FONT_SCALER, scaleY / FONT_SCALER);
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		
		setBackground(new Texture(Gdx.files.internal("ui/darkspace.png")));
		createUI();
		
		inputBroadcaster = new InputBroadcaster();
		inputBroadcaster.addInputProcessor(new MatchScreenInputProcessor());
		
		World world = new World(Vector2.Zero, true);
		buildBoundaries(world);
		
		BuildEdges b = new BuildEdges(world, MatchConstants.BOUNDARY_WIDTH, MatchConstants.BOUNDARY_HEIGHT);
		Bundle bundle = new Bundle(b, b);
		addBundles(bundle);
		
		
		getModelScreen().setClientProjectileCreator(this);
		getModelScreen().setWorld(world);
		
		inputMultiplexer = new InputMultiplexer(stage, inputBroadcaster, chatWindow);
		
		Bundle chatBundle = new Bundle(chatWindow, null);
		addBundles(chatBundle);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		getModelScreen().getWorld().setContactListener(new ProjectileColisionHandler());
	}

	private void buildBoundaries(World world) {
		// TODO Auto-generated method stub
		
	}

	private void createUI() {
	
		table = new Table(skin);
		buttonHolder = new Table(skin);
		chatWindowHolder = new Table(skin);

		textFieldStyle = new TextFieldStyle();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.font = white;
		textFieldStyle.focusedFontColor = Color.RED;

		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("spacebutton");
		textButtonStyle.down = skin.getDrawable("spacebuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.valueOf("7DF9FF");
		
		disconnectButton = new TextButton("Disconnect", textButtonStyle);
		
		//Add click functionality for the disconnect button here;
		disconnectButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				MatchServerHandle.getInstance().disconnectFromMatchServer();
				game.createAndSetLobbyScreen();
			} 
		});
	}
	
	/**
	 * Creates a {@link ShipBuilder} that builds the ships.
	 * @param ships the ships to build
	 */
	public void buildShips(final byte[] ships, final int packageTotal) {
		addEntityHandeler(new ShipBuilder(ships, this, getModelScreen().getWorld()));
		shipsBuilt++;
		if (shipsBuilt == packageTotal) {
			Log.info("[CLIENT] Building of ships succsesful!");
			MatchServerHandle.getInstance().sendShipBuildingReadyStatus();
		}
	}
	
	/**
	 * Synch The client to the match server.
	 * @param entitySnapshotList the parts from the matchServer.
	 */
	public void synchClient(final List<EntitySnapshot> entitySnapshotList) {
		addEntityHandeler(new EntitySync(entitySnapshotList, this));
	}
	
	/**
	 * Adds {@link InputProcessor} to the {@link InputBroadcaster} used in the match.
	 * @param inputProcessor the {@link InputProcessor} to add.
	 */
	public void addInputProcessor(final InputProcessor inputProcessor) {
		inputBroadcaster.addInputProcessor(inputProcessor);
	}
	
	@Override 
	public void resize(final int width, final int height) {
		if (stage != null) {
			stage.clear();
		}
		
		table = new Table(skin);
		buttonHolder = new Table(skin);
		chatWindowHolder = new Table(skin);
		
		table.setBounds(0, 0, width, height);

		table.add(buttonHolder).top();
		table.row();
		chatWindowHolder.add(chatWindow).size(ChatWindow.CHAT_WIDTH, ChatWindow.CHAT_HEIGHT);
		buttonHolder.add(disconnectButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		table.add(chatWindowHolder).bottom().left();
		table.add().expand();
		
		
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}
	
	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }
	
	/**
	 * Disposes graphical resources when they are no longer in use.
	 */
	@Override public void dispose() {
		stage.dispose();
		this.dispose();
	}
	
	public void removeProjectiles(final List<UniqueIdentifier> idsToRemove) {
		addEntityHandeler(new ProjectileRemover(this, idsToRemove));
	}
}
