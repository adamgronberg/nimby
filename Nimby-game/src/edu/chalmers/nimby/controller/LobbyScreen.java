package edu.chalmers.nimby.controller;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.camera.CameraControllerGeneric;
import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.network.lobby.PlayerData;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.CreateLoginAccountHandle;
import edu.chalmers.nimby.view.ChatWindow;

/**
 * LobbyScreen. Contains a chat window, matchmaking, option ect.
 * @author Adam Grönberg, Lucas Wiman
 *
 */
public class LobbyScreen extends AbstractControllerScreen {

	private static final int SCROLL_PANE_WIDTH = 300;
	private static final int SCROLL_PANE_HEIGHT = 200;
	private static final int BUTTON_WIDTH = 410;
	private static final int BUTTON_HEIGHT = 50;
	private static final int FONT_SCALER = 4;
	
	private static final int HEADING_FONT_SIZE = 3;

	private ChatWindow chatWindow;
	
	private byte[] redShip;
	private byte[] blueShip;
	private byte[] greenShip;
	private byte[] brownShip;
	
	private final BitmapFont white;
	private final TextureAtlas atlas;
	private final Skin skin;
	private Image background;
	
	private LabelStyle headingStyle;
	private TextButtonStyle textButtonStyle;
	private ScrollPane sPane;
	private Label heading;
	private Texture spacePicture;
	
	private TextButton matchMakingButton;
	private TextButton buildModeButton;
	private TextButton greenShipButton;
	private TextButton redShipButton;
	private TextButton brownShipButton;
	private TextButton blueShipButton;
	private TextButton optionsButton;
	
	private InputMultiplexer inputMultiplexer;
	
	private Table table;
	private Table buttonHolder;
	private Table shipHolder;
	private TextButton logOutButton;
	private List<TextButton> createdShipButtons;
	private ScrollPaneStyle sStyle;

	/**
	 * Constructor. Creates stage and stage components.
	 * @param game the game
	 */
	public LobbyScreen(final Nimby game) {
		super(game, new CameraControllerGeneric());
		this.chatWindow = game.getChatWindow();
		
		white = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
	
		float scaleX = white.getScaleX();
		float scaleY = white.getScaleY();
		white.setScale(scaleX / FONT_SCALER, scaleY / FONT_SCALER);
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		spacePicture = new Texture(Gdx.files.internal("ui/darkspace.png"));
		
		createUI();
		//createShips();
		
		
		PlayerShip ship = new PlayerShip();		//default ship
		ship.setNames("Red Ship");
		ship.setShipBlob(redShip);
		LobbyServerHandle.getInstance().setMatchShip(ship);

		Bundle chatBundle = new Bundle(chatWindow, null);
		addBundles(chatBundle);

		inputMultiplexer = new InputMultiplexer(stage, chatWindow);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	/**
	 * Adds all ships to the ship holder.
	 */
	private void shipAdds() {
		if (table != null) {
			if (shipHolder == null) {
				shipHolder = new Table(skin);
			}
			shipHolder.clear();
			for (TextButton t : createdShipButtons) {
				shipHolder.add(t).size(BUTTON_WIDTH, BUTTON_HEIGHT);
				shipHolder.row();
			}
		}
	}

//	/**
//	 * TEMP! Creates 4 ships with different colors.
//	 */
//	private void createShips() {
//		redShip = createShip(ShipData.TIE_MAIN_PART_RED);
//		blueShip = createShip(ShipData.TIE_MAIN_PART_BLUE);
//		greenShip = createShip(ShipData.TIE_MAIN_PART_GREEN);
//		brownShip = createShip(ShipData.TIE_MAIN_PART_BROWN);
//	}

	/**
	 * Renders the screen. Rebuilds everything if the screen is resized.
	 * @param width The width to resize to.
	 * @param height The height to resize to.
	 */
	@Override
	public final void resize(final int width, final int height) {
		getViewScreen().getCam().setToOrtho(false, 1, height / width);
		if (stage != null) {
			stage.clear();
		}
		
		table = new Table(skin);
		background = new Image(spacePicture);
		table.setBackground(background.getDrawable());
		
		buttonHolder = new Table(skin);
		
		table.setBounds(0, 0, width, height);
		
		/* Adding buttons to buttonHolder*/
		buttonHolder.add(matchMakingButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();
		buttonHolder.add(buildModeButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();
		buttonHolder.add(optionsButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();
		buttonHolder.add(logOutButton).top().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();

		/* Adds the button and heading to the table */
		addToTable();
		table.debug(); //TODO remove later
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}
	
	/**
	 * Adds all components to the table in correct order.
	 */
	private void addToTable() {
		table.add().expand();
		table.add(buttonHolder);
		table.row();
		table.add(chatWindow).size(ChatWindow.CHAT_WIDTH, ChatWindow.CHAT_HEIGHT).left();
		shipAdds();
		sPane = new ScrollPane(shipHolder);
		sPane.setStyle(sStyle);
		sPane.setScrollingDisabled(true, false);
		table.add(sPane).left().maxSize(SCROLL_PANE_WIDTH, SCROLL_PANE_HEIGHT);
	}

	/**
	 * Disposes of the screen and its resources when they are no longer needed.
	 */
	@Override
	public final void dispose() {
		this.dispose();
		stage.dispose();
	}
	
	public final void changeToMatchScreen() {
		ScreenChanger changer = new ScreenChanger(ScreenChanger.Screen.MATCH, this);
		addEntityHandeler(changer);
	}
	
//	/**
//	 * Creates a a ship (TEMP!).
//	 * @param x	x
//	 * @param y y
//	 * @param texturePath texturePatch
//	 * @return created blob
//	 */
//	private byte[] createShip(final String texturePath) {
//		World world = new World(Vector2.Zero, true);
//		ShipPart partTieMain = new ShipPart(world , new Vector2(0, 0), ShipData.TIE_MAIN_WIDTH, ShipData.TIE_MAIN_HEIGHT, ShipData.PART_MASS, ShipData.TIE_MAIN_NAME, texturePath);
//		partTieMain.generateShipLocalUniqueID();
//		
//		ShipPart partTieLeft = new ShipPart(world , new Vector2(-ShipData.TIE_MAIN_WIDTH / 2 - ShipData.TIE_SIDE_WIDTH / 2, 0), ShipData.TIE_SIDE_WIDTH, ShipData.TIE_SIDE_HEIGHT, ShipData.PART_MASS, ShipData.TIE_LEFT_NAME, ShipData.TIE_LEFT_PART);
//		partTieLeft.generateShipLocalUniqueID();
//		
//		ShipPart partTieRight = new ShipPart(world, new Vector2(ShipData.TIE_MAIN_WIDTH / 2 + ShipData.TIE_SIDE_WIDTH / 2, 0), ShipData.TIE_SIDE_WIDTH, ShipData.TIE_SIDE_HEIGHT, ShipData.PART_MASS, ShipData.TIE_RIGHT_NAME, ShipData.TIE_RIGHT_PART);
//		partTieRight.generateShipLocalUniqueID();
//		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("{KEY_DOWN}");
//		sb.append("{Boost;500}");
//		sb.append("{END}");
//		
//		final float engineCD = 0.1f;
//		Equipment engine = new Equipment(world, 
//				new Vector2(0, 0), 0, 
//				ShipData.TIE_ENGINE_SIZE, 
//				ShipData.TIE_ENGINE_SIZE, 
//				ShipData.PART_MASS, 
//				ShipData.TIE_ENGINE_NAME, 
//				ShipData.TIE_ENGINE_PART, 
//				engineCD, sb.toString(), Keys.W);
//		
//		engine.generateShipLocalUniqueID();
//		
//		sb = new StringBuilder();
//		
//		sb.append("{KEY_DOWN}");
//		sb.append("{SpawnProjectile;500}");
//		sb.append("{END}");
//		
//		final float weaponCD = 0.1f;
//		Equipment weapon = new Equipment(world, 
//				new Vector2(ShipData.TIE_MAIN_WIDTH / 2, ShipData.TIE_MAIN_HEIGHT), 0, 
//				WeaponData.SPACE_CANNON_WIDTH, 
//				WeaponData.SPACE_CANNON_HEIGHT, 
//				WeaponData.SPACE_CANNON_MASS, 
//				WeaponData.SPACE_CANNON_NAME,
//				WeaponData.SPACE_CANNON_TEXTURE, 
//				weaponCD, sb.toString(), Keys.Q);
//		
//		weapon.generateShipLocalUniqueID();
//		
//		partTieMain.weld(world, partTieLeft);
//		partTieMain.weld(world, partTieRight);
//		
//		// this will not be possible
//		engine.createRevoluteJoint(partTieMain, Vector2.Zero.cpy());
//		weapon.createRevoluteJoint(partTieMain, Vector2.Zero.cpy());
//		
//		IdentifiableEntity.resetShipLocalUniqueIDCounter();
//		return PartBuilder.createBlob(partTieMain);
//	}
	
	/**
	 * Creates all the UI elements.
	 */
	private void createUI() {
		if (createdShipButtons == null) {
			createdShipButtons = new LinkedList<>();
		}
		headingStyle = new LabelStyle(white, Color.WHITE);
		heading = new Label("NIMBY", headingStyle);
		heading.setFontScale(HEADING_FONT_SIZE);
		
		/* Making a button style */
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("spacebutton");
		textButtonStyle.down = skin.getDrawable("spacebuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.valueOf("7DF9FF");
		
		sStyle = new ScrollPaneStyle();
		sStyle.vScrollKnob = skin.getDrawable("scrollvertical");
		
		/* Creating buttons.*/
		matchMakingButton = new TextButton("Join Matchmaking", textButtonStyle);
		buildModeButton = new TextButton("Build Mode", textButtonStyle);
		optionsButton = new TextButton("Options", textButtonStyle);
		logOutButton = new TextButton("Log out", textButtonStyle);
		
		// Loads saved ships from the server.
		new Thread(new Runnable() {
			@Override
			public void run() {
				createShipButtons();	
			}
		}).start();
		
		redShipButton = new TextButton("Red Ship", textButtonStyle);
		redShipButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				System.out.println("Selected Red Ship!");
				PlayerShip ship = new PlayerShip();
				ship.setNames("Red Ship");
				ship.setShipBlob(redShip);
				LobbyServerHandle.getInstance().setMatchShip(ship);
			}
		});
		
		greenShipButton = new TextButton("Green Ship", textButtonStyle);
		greenShipButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				System.out.println("Selected Green Ship!");
				PlayerShip ship = new PlayerShip("Green Ship", greenShip);
				LobbyServerHandle.getInstance().setMatchShip(ship);
			}
		});
		
		blueShipButton = new TextButton("Blue Ship", textButtonStyle);
		blueShipButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				System.out.println("Selected Blue Ship!");
				PlayerShip ship = new PlayerShip();
				ship.setNames("Blue Ship");
				ship.setShipBlob(blueShip);
				LobbyServerHandle.getInstance().setMatchShip(ship);
			}
		});
		
		brownShipButton = new TextButton("Brown Ship", textButtonStyle);
		brownShipButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				System.out.println("Selected Brown Ship!");
				PlayerShip ship = new PlayerShip();
				ship.setNames("Brown Ship");
				ship.setShipBlob(brownShip);
				LobbyServerHandle.getInstance().setMatchShip(ship);
			}
		});
		
		/*Adding buttonfunctions*/
		buildModeButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				getGame().setScreen(new BuildScreen(getGame()));
			}
		});

		matchMakingButton.addListener(new ClickListener() {
			private boolean isJoined = false;
			
			public void clicked(final InputEvent event, final float x, final float y) {
				LobbyServerHandle handle = LobbyServerHandle.getInstance();
				if (!isJoined) {
					matchMakingButton.setText("Leave Matchmaking");
					handle.sendMatchQueueRequest(true);
				} else {
					matchMakingButton.setText("Join Matchmaking");
					handle.sendMatchQueueRequest(false);
				}
				isJoined = !isJoined;
			}
		});
		
		optionsButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				game.createAndSetOptionScreen();
			}
		});
		
		logOutButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				LobbyServerHandle.getInstance().sendLogoutRequest();
				game.createAndSetLoginScreen();
			}
		});
		
		table = new Table();
		stage.addActor(table);
	}
	
	private void createShipButtons() {
		CreateLoginAccountHandle.fetchCreatedShips(LobbyServerHandle.getInstance().getSessionToken());
		List<PlayerShip> fetchedShips = PlayerData.getInstance().getShips();
		for (PlayerShip ship : fetchedShips) {
			final String buttonText = ship.getName();
			final byte[] currentShip = ship.getShipBlob();
			//System.out.println(buttonText);
			TextButton button = new TextButton(buttonText, textButtonStyle);
			button.size(BUTTON_WIDTH, BUTTON_HEIGHT);
			button.addListener(new ClickListener() {
				public void clicked(final InputEvent event, final float x, final float y) {
					//System.out.println("" + buttonText);
					PlayerShip ship = new PlayerShip(buttonText, currentShip);
					LobbyServerHandle.getInstance().setMatchShip(ship);
				}
			});
			createdShipButtons.add(button);
		
		}
		shipAdds();
	}

	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }
}
