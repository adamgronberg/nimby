package edu.chalmers.nimby.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import edu.chalmers.model.Ship;
import edu.chalmers.model.ShipPK;
import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.build.BuildEdges;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.camera.CameraControllerBuild;
import edu.chalmers.nimby.inputProcessors.BuildScreenInputProcessor;
import edu.chalmers.nimby.model.NodeSnapper;
import edu.chalmers.nimby.model.WeldPipe;
import edu.chalmers.nimby.model.factory.PartBuilder;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.CreateLoginAccountHandle;
import edu.chalmers.nimby.util.PartsUtil;
import edu.chalmers.nimby.util.Pipe;
import edu.chalmers.nimby.util.PipeIn;
import edu.chalmers.nimby.util.ThreadTimeCalculator;
import edu.chalmers.nimby.view.ChatWindow;
/**
 * This class represents the screen to be used when building ships.
 * @author Lucas Wiman and Mikael Stolpe
 *
 */
public class BuildScreen extends AbstractControllerScreen {

	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_WIDTH = 150;
	private static final int DIALOG_HEIGHT = 100;
	private static final String ERROR_MESSAGE = "Invalid ship";
	private static final String NAME_ALREADY_TAKEN = "Name already taken, try again";
	private static final String NAME_MESSAGE = "Enter a ship name";
	private static final String DEFAULT_NAME = "Name";
	private static final float DIALOG_WIDTH = 100;
	private static final int FONT_SCALER = 4;
	private static final float TEXT_FIELD_SIZE = 100;

	private World world;

	private InputMultiplexer mPlexer;
	private ChatWindow chatWindow;
	private Dialog d;
	

	private Map<BuildPart, Set<BuildPart>> partMapSet;
	private List<Set<BuildPart>> partListSet;
	private Pipe<HashMap<String, List<BuildPart>>> pipe;

	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Table buttonHolder;
	private Table chatHolder;
	private Table dialogHolder;

	private BitmapFont white;
	private TextButton saveButton;
	private TextButton clearButton;
	private TextButton backButton;
	private TextField shipNameEntry;
	private TextFieldStyle textFieldStyle;
	private TextButtonStyle textButtonStyle;
	private WindowStyle wStyle;
	private TextButton okButton;
	
	private TextButton cancelButton;
	private String shipName;
	private Ship create;
	private byte[] blobToSave;
	
	private Thread createShipThread;
	private Entity createShipEntity;
	private boolean timeToCreateShip;
	private BitmapFont space;

	public BuildScreen(final Nimby game) {
		super(game, new CameraControllerBuild());
		IdentifiableEntity.resetShipLocalUniqueIDCounter();
		this.chatWindow = game.getChatWindow();
		white = new BitmapFont(Gdx.files.internal("fonts/terminal.fnt"), false);
		space = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
		float scaleX = space.getScaleX();
		float scaleY = space.getScaleY();
		space.setScale(scaleX / FONT_SCALER, scaleY / FONT_SCALER);
		this.world = new World(Vector2.Zero.cpy(), true);
		
		setBackground(new Texture(Gdx.files.internal("ui/darkspace.png")));
		initStage();
		
		
		pipe = new Pipe<HashMap<String, List<BuildPart>>>();

		WeldPipe wPipe = new WeldPipe(pipe, this);
		Bundle pipeBundle = new Bundle(wPipe, null);
		addBundles(pipeBundle);

		partMapSet = new HashMap<>();
		partListSet = new LinkedList<>();

		// We need to define the area which the user is allowed to build / move the build bodies
		setBuildLimits();
		getModelScreen().setWorld(world);
		
		createShipEntity = new Entity() {
			@Override
			public void update(final float delta, final OrthographicCamera cam) {
				
				if (CreateLoginAccountHandle.getInstance().resultReady()) {
					checkShipCreateSuccess();
					createShipThread = null;
										
				}
			}
		};
		
		//bundles
		Bundle chatBundle = new Bundle(chatWindow, null);
		Bundle shipBundle = new Bundle(createShipEntity,null);		
		
		
		addBundles(chatBundle);

		addBundles(shipBundle);
		
		mPlexer = new InputMultiplexer(stage, new BuildScreenInputProcessor(world, getCamera(), this, stage));
		Gdx.input.setInputProcessor(mPlexer);
		world.setContactListener(new NodeSnapper());
	}

	/**
	 * Sets the stage.
	 */
	private void initStage() {
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		table = new Table(skin);
		buttonHolder = new Table(skin);
		chatHolder = new Table(skin);
		dialogHolder = new Table(skin);
		
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.font = white;
		textFieldStyle.focusedFontColor = Color.RED;

		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("spacebutton");
		textButtonStyle.down = skin.getDrawable("spacebuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = space;
		textButtonStyle.fontColor = Color.valueOf("7DF9FF");

		saveButton = new TextButton("Save", textButtonStyle);
		saveButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				saveShip();
			} 
		});

		clearButton = new TextButton("Clear", textButtonStyle);
		clearButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				clearScreen();
			} 
		});
		
		backButton = new TextButton("Back", textButtonStyle);
		backButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				getGame().setScreen(new LobbyScreen(getGame()));
			} 
		});

		chatWindow.setBackground(skin.getDrawable("chatwindow"));
		
		buttonHolder.add(saveButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();
		buttonHolder.add(clearButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonHolder.row();
		buttonHolder.add(backButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		chatHolder.add(chatWindow).size(ChatWindow.CHAT_WIDTH, ChatWindow.CHAT_HEIGHT);
		
		
		table.add(chatHolder).expand().bottom().left();
		table.add(buttonHolder).bottom();
	}

	/**
	 * Creates the boundaries which limits the build mode area.
	 */
	private void setBuildLimits() {
		BuildEdges b = new BuildEdges(world);
		Bundle bundle = new Bundle(b, b);
		addBundles(bundle);
	}

	@Override
	public final void resize(final int width, final int height) {
		if (stage != null) {
			stage.clear();
		}
		table.setBounds(0, 0, width, height);
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}
	
	@Override
	public void dispose() {
		this.dispose();
		stage.dispose();
		world.dispose();
	}

	/**
	 * Will save ships if possible.
	 */
	public final void saveShip() {
		if (canSave()) { 
			//System.out.println("Your token : " + LobbyServerHandle.getInstance().getSessionToken());
			createSaveDialog();
		} else {
			createErrorDialogWindow();
		}
	}
	
	/**
	 * Creates a dialog which handles the save event for a ship.
	 */
	private void createSaveDialog() {
		ShipPart mainPart = findMainPart();
		blobToSave = PartBuilder.createBlob(mainPart);
		shipName = null;
		create = new Ship();
		
		wStyle = new WindowStyle();
		wStyle.background = skin.getDrawable("dialogback");
		wStyle.titleFont = white;
		wStyle.background.setTopHeight(DIALOG_HEIGHT);
		wStyle.background.setRightWidth(DIALOG_WIDTH);

		d = new Dialog(NAME_MESSAGE, wStyle);
		shipNameEntry = new TextField(DEFAULT_NAME, textFieldStyle);
		shipNameEntry.size(TEXT_FIELD_SIZE);

		okButton = new TextButton("Ok", textButtonStyle);
		okButton.addListener(new ClickListener() {
			
			public void clicked(final InputEvent event, final float x, final float y) {
				//System.out.println(shipNameEntry.getText());
				shipName = shipNameEntry.getText();
				ShipPK key = new ShipPK();
				String builder = "builder";
				key.setBuilder(builder);
				key.setName(shipName);
				create.setId(key);
				create.setData(blobToSave);
				String input = null;
				Json parser = new Json();
				parser.setOutputType(JsonWriter.OutputType.json);
				input = parser.toJson(create);
				
				CreateLoginAccountHandle.getInstance().setParameters(null, null, null, "createShip",
						LobbyServerHandle.getInstance().getSessionToken(), input);
				
				timeToCreateShip = true;
				createShipThread = new Thread(new ThreadTimeCalculator());
				createShipThread.start();

			} });
		
		cancelButton = new TextButton("Cancel", textButtonStyle);
		
		cancelButton.addListener(new ClickListener() {

			public void clicked(final InputEvent event, final float x, final float y) {
				d.remove();
			} 
		});
		dialogHolder.clear();
		dialogHolder.add(shipNameEntry);
		dialogHolder.row();
		dialogHolder.add(okButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		dialogHolder.add(cancelButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);

		d.add(dialogHolder).expand().left();
		d.show(stage);
	}

	/**
	 * Looks for the main part.
	 * @return the main part
	 */
	private ShipPart findMainPart() {
		ShipPart ship = null;
		outer:
			for (BuildPart p : getPartListSet().get(0)) {
				if (p.getTrueUserData() instanceof ShipPart) {
					ship = (ShipPart) p.getTrueUserData();
					break outer;
				}
			}
		ShipPart root;
		if (ship != null) {
			root = PartsUtil.getRoot(ship);
		} else {
			//System.out.println("findMainPart: no valid root here");
			throw new IllegalArgumentException("No valid root!");
		}
		return root;
	}

	/**
	 * Clears the screen of all parts.
	 */
	public final void clearScreen() {
		getGame().setScreen(new BuildScreen(getGame()));
	}

	/**
	 * @return the partListSet
	 */
	public final List<Set<BuildPart>> getPartListSet() {
		return partListSet;
	}

	/**
	 * @param partListSet the partListSet to set
	 */
	public final void setPartListSet(final List<Set<BuildPart>> partListSet) {
		this.partListSet = partListSet;
	}

	/**
	 * @return the partMapSet
	 */
	public final Map<BuildPart, Set<BuildPart>> getPartMapSet() {
		return partMapSet;
	}

	/**
	 * @param partMapSet the partMapSet to set
	 */
	public final void setPartMapSet(final Map<BuildPart, Set<BuildPart>> partMapSet) {
		this.partMapSet = partMapSet;
	}

	/**
	 * Checks if the user can save.
	 * @return true if saveable.
	 */
	public final boolean canSave() {
		return partListSet.size() == 1;
	}

	/**
	 * Getter for Pipein.
	 * @return the pipe
	 */
	public final PipeIn<HashMap<String, List<BuildPart>>> getPipeIn() {
		return pipe;
	}

	/**
	 * Creates a dialog window that shows up when the user tries to save an invalid ship.
	 */
	public final void createErrorDialogWindow() {
		wStyle = new WindowStyle();
		wStyle.background = skin.getDrawable("dialogback");
		wStyle.titleFont = white;
		wStyle.background.setTopHeight(DIALOG_HEIGHT);

		d = new Dialog(ERROR_MESSAGE, wStyle);

		okButton = new TextButton("Ok", textButtonStyle);
		okButton.size(BUTTON_WIDTH, BUTTON_HEIGHT);
		okButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				d.hide();
			} 
		});

		d.add(okButton).expand().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);			 
		d.show(stage);
	}

	/**
	 * This method calls a dialog window that shows up if a player attempts to save a ship with a name that already exists.
	 */
	public final void createNameEntryErrorDialogWindow() {

		wStyle = new WindowStyle();
		wStyle.background = skin.getDrawable("dialogback");
		wStyle.titleFont = white;
		wStyle.background.setTopHeight(DIALOG_HEIGHT);

		d = new Dialog(NAME_ALREADY_TAKEN, wStyle);

		okButton = new TextButton("Ok", textButtonStyle);
		okButton.size(BUTTON_WIDTH, BUTTON_HEIGHT);
		okButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				d.remove();
			} 
		});
		d.add(okButton).expand().left().size(BUTTON_WIDTH, BUTTON_HEIGHT);			 
		d.show(stage);
	}
	
	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }
	
	/*
	 * Checks if the ships was created successful
	 */
	private void checkShipCreateSuccess() {
		boolean shipCreated = CreateLoginAccountHandle.getInstance().isCreateShipSuccesful();
		System.out.println("ship create ok?: " + shipCreated);
		if (shipCreated && timeToCreateShip) {
			System.out.println("Ship were created successfully");
			CreateLoginAccountHandle.getInstance().clearParameters();
			d.remove();
			timeToCreateShip = false;
		} else if (!shipCreated && timeToCreateShip){
			d.remove();
			createNameEntryErrorDialogWindow();
		}

	}
	

}
