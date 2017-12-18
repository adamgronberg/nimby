package edu.chalmers.nimby.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import edu.chalmers.nimby.util.CreateLoginAccountHandle;
import edu.chalmers.nimby.util.TextFilter;
import edu.chalmers.nimby.util.ThreadTimeCalculator;
/**
 * This class represents the screen to be used when creating new accounts.
 * @author Lucas and Gustav Dahl
 *
 */
public class CreateAccountScreen extends AbstractControllerScreen {
	
	private BitmapFont white;
	private TextureAtlas atlas;
	private Skin skin;
	private Table table;
	private Label loginLabel;
	private Label passLabel;
	private Label vPasslabel;
	private Label vemailLabel;
	private Label emailLabel;
	private LabelStyle headingStyle;
	private Label heading;
	private TextButton createButton;
	private TextButton backButton;
	private TextField accField;
	private TextField passField;
	private TextField vpassField;
	private TextField emailField;
	private TextField vemailField;
	private TextFilter filter;
	private TextFieldStyle textFieldStyle;
	private TextButtonStyle textButtonStyle;
	private Drawable fieldBackground;
	private static final int BACKGROUND_HEIGHT = 25;
	private static final int PASSWORD_LENGTH = 20;
	private static final float HEADING_SIZE = 1.015f;
	private static final int FIELD_PADDING = 325;
	private static final int BUTTON_WIDTH = 350;
	private static final int BUTTON_HEIGHT = 30;
	private static final int FONT_SCALER = 4;
	private Texture spacePicture;
	private Image background;
	private BitmapFont space;
	
	/**
	 * Sets up a CreateAccountScreen.
	 * @param game The game where the screen is run.
	 */
	public CreateAccountScreen(final Nimby game) {
		super(game, new CameraControllerGeneric());
		white = new BitmapFont(Gdx.files.internal("fonts/white.fnt"), false);
		space = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
		float scaleX = space.getScaleX();
		float scaleY = space.getScaleY();
		space.setScale(scaleX / FONT_SCALER, scaleY / FONT_SCALER);
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		spacePicture = new Texture(Gdx.files.internal("ui/darkspace.png"));
		
		createUI();
	
		Gdx.input.setInputProcessor(stage);
	}

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
		
		table.add(heading);
		table.row();
		table.add(loginLabel);
		table.row();
		table.add(accField).width(FIELD_PADDING);
		table.row();
		table.add(passLabel);
		table.row();
		table.add(passField).width(FIELD_PADDING);
		table.row();
		table.add(vPasslabel);
		table.row();
		table.add(vpassField).width(FIELD_PADDING);
		table.row();
		table.add(emailLabel);
		table.row();
		table.add(emailField).width(FIELD_PADDING);
		table.row();
		table.add(vemailLabel);
		table.row();
		table.add(vemailField).width(FIELD_PADDING);
		table.row();
		table.add(createButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		table.row();
		table.add(backButton).size(BUTTON_WIDTH, BUTTON_HEIGHT);
		
		stage.addActor(table);
		stage.setViewport(width, height, true);
	}

	/**
	 * Disposes of the screen and its resources when they are no longer needed.
	 */
	@Override
	public final void dispose() {
		this.dispose();
		stage.dispose();
	}
	
	public void createUI() {
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
		
		fieldBackground.setMinHeight(BACKGROUND_HEIGHT);
		textFieldStyle.background = fieldBackground;
		
		/* Textfields and adding filters*/
		filter = new TextFilter();
		
		accField = new TextField("Hello", textFieldStyle);
		accField.setTextFieldFilter(filter); 
		passField = new TextField("Password", textFieldStyle);
		passField.setTextFieldFilter(filter);
		vpassField = new TextField("Verify password", textFieldStyle);
		vpassField.setTextFieldFilter(filter);
		emailField = new TextField("Email here", textFieldStyle);
		emailField.setTextFieldFilter(filter);
		vemailField = new TextField("Verify email here", textFieldStyle);
		vemailField.setTextFieldFilter(filter);
		
		passField.setPasswordMode(true);
		passField.setPasswordCharacter('*');
		passField.setMaxLength(PASSWORD_LENGTH);
		
		vpassField.setPasswordMode(true);
		vpassField.setPasswordCharacter('*');
		vpassField.setMaxLength(PASSWORD_LENGTH);
		
		/* Buttons*/
		createButton = new TextButton("Create Account", textButtonStyle);
		backButton = new TextButton("Back", textButtonStyle);
		
		createButton.addListener(new ClickListener() {

			public void clicked(final InputEvent event, final float x, final float y) {
				//Gets the password
				String password = passField.getText(); 
				//Used to verify the password.
				String vpassword = vpassField.getText();
				//Gets the username
				String userName = accField.getText();
				//Gets the email
				String email = emailField.getText();
				//Used to verify email
				String vemail = vemailField.getText();

				if (!(password.equals(vpassword))) { //Tests if the passwords match
					System.err.println("Passwords does not match");

				} else {
					System.err.println("Password ok");
					if (!(email.equals(vemail))) {   //Tests if the email matches
						System.err.println("Email does not match");
					} else {
						System.err.println("Email ok");  //Tries to set up the account

						//TODO Should call create account and login account
						CreateLoginAccountHandle.getInstance().setParameters(userName, vpassword, email, "create", null,null);
						new Thread(new ThreadTimeCalculator()).start();
					}
				}
		} });
		
		backButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				getGame().setScreen(new LoginScreen(getGame()));
			}
		});
		
		/* Headings */
		headingStyle = new LabelStyle(space, Color.WHITE);
		heading = new Label("Account creation", headingStyle);
		heading.setFontScale(HEADING_SIZE);
		
		/* Labels  */
		LabelStyle smallStyle = new LabelStyle(space, Color.CYAN);
		loginLabel = new Label("Enter the login you wish to use", smallStyle);
		passLabel = new Label("Enter the password you wish to use", smallStyle);
		vPasslabel = new Label("Please verify the password", smallStyle);
		emailLabel = new Label("Enter your email here", smallStyle);
		vemailLabel = new Label("Please verify the email", smallStyle);
		
		table = new Table();
		stage.addActor(table);
	}

	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }
}
