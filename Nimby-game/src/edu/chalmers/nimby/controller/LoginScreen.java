package edu.chalmers.nimby.controller;


import java.lang.Thread.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.camera.CameraControllerGeneric;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.CreateLoginAccountHandle;
import edu.chalmers.nimby.util.TextFilter;
import edu.chalmers.nimby.util.ThreadTimeCalculator;

/**
 * This class represents the Main menu screen in the game.
 * @author Lucas and Gustav Dahl
 *
 */
public class LoginScreen extends AbstractControllerScreen {
	
	private static final int BUTTON_PADDING_LENGTH = 15;
	private static final int MAX_PASSWORD_LENGTH = 20;
//	private static final int HEADING_FONT_SCALE = 3;			TODO Not used? Lucas?
	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_WIDTH = 410;
	private static final int FILTER_BACKGROUND_HEIGHT = 25;
	private static final int FONT_SCALER = 4;
	private static final float HEADING_FONT_SCALER = 1.6f;
	
	private TextField accField, passField;
	
	private final BitmapFont white;
	private final BitmapFont space;
	private final TextureAtlas atlas;
	private final Skin skin;
	private Thread loginThread;
	private Entity loginEntity;
	private TextButtonStyle textButtonStyle;
	private TextFieldStyle textFieldStyle;
	private TextButton exitButton;
	private TextButton createAccButton;
	private TextButton optionsButton;
	private TextButton loginButton;
	private LabelStyle headingStyle;
	private LabelStyle smallStyle;
	private Label loginLabel;
	private Label passLabel;
	private Label heading;
	private Table table;
	private Drawable fieldBackground;
	private Texture spacePicture;
	private Image background;
	private BitmapFont lSpace;
	
	/**
	 * The constructor for the screen.
	 * @param game The game to be used for the screen.
	 */
	public LoginScreen(final Nimby game) {
		super(game, new CameraControllerGeneric());
		
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		white = new BitmapFont(Gdx.files.internal("fonts/terminal.fnt"), false);
		space = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
		lSpace = new BitmapFont(Gdx.files.internal("fonts/largespaceage.fnt"), false);
		float smallScaleX = white.getScaleX();
		float smallScaleY = white.getScaleY();
		space.setScale(smallScaleX / FONT_SCALER, smallScaleY / FONT_SCALER);
		
		spacePicture = new Texture(Gdx.files.internal("ui/darkspace.png"));
		
		loginEntity = new Entity() {
			@Override
			public void update(final float delta, final OrthographicCamera cam) {
				if (loginThread != null && loginThread.getState() == State.TERMINATED) {
					boolean loggedIn = CreateLoginAccountHandle.getInstance().isLoggedIn();
					if (loggedIn) {
						getGame().createAndSetLobbyScreen();
					}
				}
			}
		};
		
		Bundle loginBundle = new Bundle(loginEntity, null);
		addBundles(loginBundle);
		createUI();
		Gdx.input.setInputProcessor(stage);
	}
	
	/**
	 * Renders the screen. Rebuilds everything when the screen is resized.
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
		table.setBounds(0, 0, width, height);
		background = new Image(spacePicture);
		table.setBackground(background.getDrawable());

		/* Sets up the table */
		table.add(heading);
		table.row();
		table.add(loginLabel);
		table.row();
		table.add(accField).width(BUTTON_WIDTH);
		table.row();
		table.add(passLabel);
		table.row();
		table.add(passField).width(BUTTON_WIDTH);
		table.row();
		table.add(loginButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		table.row();
		table.add(createAccButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		table.row();
		table.add(exitButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}
	

	/**
	 * Disposes of the screen and its resources when it is no longer needed.
	 */
	@Override
	public final void dispose() {
		this.dispose();
		stage.dispose();
	}
	
	private void createUI() {
		
		/* Making a button style */
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("spacebutton");
		textButtonStyle.down = skin.getDrawable("spacebuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = space;
		textButtonStyle.fontColor = Color.valueOf("7DF9FF");
		
		/* Making a textfield style */
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.font = white;
		textFieldStyle.focusedFontColor = Color.CYAN;
		fieldBackground = skin.getDrawable("chatwindow");
		fieldBackground.setMinHeight(FILTER_BACKGROUND_HEIGHT);
		textFieldStyle.background = fieldBackground;
		
		/* Textfields and textfilters */
		TextFilter filter = new TextFilter();
		accField = new TextField("", textFieldStyle);
		accField.setTextFieldFilter(filter);
		
		passField = new TextField("", textFieldStyle);
		passField.setTextFieldFilter(filter);
		passField.setPasswordMode(true);
		passField.setPasswordCharacter('*');
		passField.setMaxLength(MAX_PASSWORD_LENGTH);
		
		/* Creating buttons.*/
		exitButton = new TextButton("Exit", textButtonStyle);
		createAccButton = new TextButton("Account Creation", textButtonStyle);
		optionsButton = new TextButton("Options", textButtonStyle);
		
		/* Changes screen to Account creation.*/
		createAccButton.pad(BUTTON_PADDING_LENGTH);
		createAccButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				game.createAndSetCreateAccountScreen();
			}
		});
		
		/* Adds a button that exits*/
		exitButton.pad(BUTTON_PADDING_LENGTH);
		exitButton.addListener(new ClickListener() {
				public void clicked(final InputEvent event, final float x, final float y) {
					Gdx.app.exit();
				}
		});
		
		/*Adds a button that logins*/
		loginButton = new TextButton("Login", textButtonStyle);
		loginButton.pad(BUTTON_PADDING_LENGTH);
		loginButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				String userName = accField.getText();
				String password = passField.getText();
				if (!(userName.isEmpty()) && !(password.isEmpty())) {
					
					CreateLoginAccountHandle.getInstance().setParameters(userName, password, null, "login", null, null);
					
					loginThread = new Thread(new ThreadTimeCalculator());
					loginThread.start();
					
				} else {
					System.out.println("User didnt not fill all the required login fields.");
				}
			}
		});
		
		/*Adds a button that switches to options*/
		optionsButton.pad(BUTTON_PADDING_LENGTH);
		optionsButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				game.createAndSetOptionScreen();
			}
		});
		
		/* Heading */
		headingStyle = new LabelStyle(lSpace, Color.WHITE);
		
		heading = new Label("NIMBY", headingStyle);
		heading.setFontScale(HEADING_FONT_SCALER, HEADING_FONT_SCALER);
		
		/* Labels for login/password */
		smallStyle = new LabelStyle(space, Color.CYAN);
		loginLabel = new Label("Id", smallStyle);
		passLabel = new Label("Password", smallStyle);
	}

	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }
}
