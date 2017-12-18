package edu.chalmers.nimby.controller;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.camera.CameraControllerGeneric;
import edu.chalmers.nimby.options.OptionLogic;
import edu.chalmers.nimby.util.Bundle;
import edu.chalmers.nimby.util.ModeComparator;
import edu.chalmers.nimby.view.ChatWindow;

/**
 * This class represents an options screen where the user can change some of the preferences of the game.
 * @author Lucas
 *
 */
public class OptionScreen extends AbstractControllerScreen {

	private static final int BUTTON_WIDTH = 120;
	private static final int SCROLL_WIDTH = 380;
	private static final int BUTTON_HEIGHT = 50;
	private static final int OPTION_WIDTH = 240;
	private static final int SCROLL_HEIGHT = 150;
	private static final int BUTTON_SPACING = 2;
	private static final int FONT_SCALER = 4;
	private static final int OPTION_SPACING = 200;

	private final BitmapFont white;
	private final Skin skin;
	private final TextureAtlas atlas;
	
	private TextButtonStyle textButtonStyle;
	
	private ChatWindow chatWindow;
	
	private DisplayMode[] modes; 

	private Table table;
	private TextButton vSyncButton;
	private TextButton soundButton;
	private TextButton backButton;
	private LabelStyle smallStyle;
	private Label vSync;
	private Label resolutions;
	private ScrollPane scrollPane;
	private ScrollPaneStyle sStyle;
	private Label sound;
	private Label fullScreen;
	private TextButton fullScreenButton;
	
	private Table res;

	private TextButton resolutionButton;
	private TextButton avatarButton;
	private Label avatarLabel;
	private final OptionLogic options;
	private Image background;
	private Texture spacePicture;
	private InputMultiplexer inputMultiplexer;
	private Table buttonHolder;

	/**
	 * Creates the screen.
	 * @param game The game where the screen is going to be used.
	 */
	public OptionScreen(final Nimby game) {
		super(game, new CameraControllerGeneric());
		this.chatWindow = game.getChatWindow();
		
		atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		white = new BitmapFont(Gdx.files.internal("fonts/spaceage.fnt"), false);
		float scaleX = white.getScaleX();
		float scaleY = white.getScaleY();
		white.setScale(scaleX / FONT_SCALER, scaleY / FONT_SCALER);
		options = OptionLogic.getInstance();
		spacePicture = new Texture(Gdx.files.internal("ui/darkspace.png"));
		
		createUI();
		
		Bundle chatBundle = new Bundle(chatWindow, null);
		addBundles(chatBundle);
		
		inputMultiplexer = new InputMultiplexer(stage, chatWindow);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	/**
	 * Renders the screen. At resize everything is built up again.
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
		buttonHolder = new Table(skin);
		background = new Image(spacePicture);
		table.setBackground(background.getDrawable());
		table.setBounds(0, 0, width, height);
		
		createResolutions();

		scrollPane = new ScrollPane(res);
		scrollPane.setStyle(sStyle);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setOverscroll(false, false);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setScrollingDisabled(true, false);
		
		/* set up buttonholder */
		buttonHolder.add(sound);
		buttonHolder.add(soundButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).left();
		buttonHolder.row();
		buttonHolder.add(avatarLabel);
		buttonHolder.add(avatarButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).left();
		buttonHolder.row();
		buttonHolder.add(vSync);
		buttonHolder.add(vSyncButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).left();
		buttonHolder.row();
		buttonHolder.add(fullScreen);
		buttonHolder.add(fullScreenButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).left();
		buttonHolder.row();
		buttonHolder.add(resolutions);
		buttonHolder.add(scrollPane).size(SCROLL_WIDTH, SCROLL_HEIGHT).left();
		buttonHolder.row();
		buttonHolder.add(backButton).size(OPTION_WIDTH, BUTTON_HEIGHT);
		

		/* Set up table.*/
		table.add().expand();
		table.row();
		table.add(buttonHolder);
		table.row();
		table.add(chatWindow).size(chatWindow.CHAT_WIDTH, chatWindow.CHAT_HEIGHT).left();
		
		


		stage.addActor(table);
		stage.setViewport(width, height, true);
	}

	/**
	 * Disposes of the screen when it is no longer needed.
	 */
	@Override
	public final void dispose() {
		stage.dispose();
	}

	private void createUI() {
		/* Making a button style */
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("spacebutton");
		textButtonStyle.down = skin.getDrawable("spacebuttonpressed");
		textButtonStyle.pressedOffsetX = BUTTON_SPACING;
		textButtonStyle.pressedOffsetY = -BUTTON_SPACING;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.valueOf("7DF9FF");

		sStyle = new ScrollPaneStyle();
		sStyle.vScrollKnob = skin.getDrawable("scrollvertical");

		/* Creating buttons */
		vSyncButton = new TextButton("", textButtonStyle);
		soundButton = new TextButton("", textButtonStyle);
		fullScreenButton = new TextButton("", textButtonStyle);
		avatarButton = new TextButton("", textButtonStyle);

		setDefaultButtonSettings();

		/* Creates a go back button*/
		backButton = new TextButton("Back", textButtonStyle);
		backButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				getGame().setScreen(new LobbyScreen(getGame()));
			}
		});

		vSyncButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				options.toggleVsyncOption();
				options.setVsyncToOption();
				setVsyncButtonText();
			}
		});
		
		avatarButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				options.toggleAvatarOption();
				options.setAvatarToOption();
				setAvatarButtonText();
			}
		});

		fullScreenButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				options.toggleFullScreenOption();
				Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), options.getFullScreenOption());
				setFullscreenButtonText();
			}
		});

		soundButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				options.toggleSoundOption();
				options.setSoundToOption();
				setSoundButtonText();
			}
		});

		/* Labels for options */
		smallStyle = new LabelStyle(white, Color.WHITE);
		vSync = new Label("Vertical Sync", smallStyle);
		resolutions = new Label("Resolutions", smallStyle);
		sound = new Label("Sound", smallStyle);
		fullScreen = new Label("Full Screen", smallStyle);
		avatarLabel = new Label("Avatar", smallStyle);
	}

	private void setDefaultButtonSettings() {
		setSoundButtonText();
		setAvatarButtonText();
		setFullscreenButtonText();
		setVsyncButtonText();
	}
	
	public void createResolutions() {
		res = new Table();

		modes = Gdx.graphics.getDisplayModes();

		Arrays.sort(modes, new ModeComparator());

		for (int i = 0; i < modes.length; i++) {

			final int index = i;
			resolutionButton = new TextButton(modes[index].width + "x" + modes[index].height + "," + modes[index].bitsPerPixel + "bps", textButtonStyle);
			resolutionButton.addListener(new ClickListener() {
				public void clicked(final InputEvent event, final float x, final float y) {
						options.setReselution(index);
						options.setReselutionToOption(modes);
				}
			});
			res.add(resolutionButton).size(SCROLL_WIDTH, BUTTON_HEIGHT);
			res.row();
		}
	}
	
	private void setSoundButtonText() {
		if (options.getSoundOption()) {
			soundButton.setText("ON");
		} else {
			soundButton.setText("OFF");
		}
	}
	
	private void setVsyncButtonText() {
		if (options.getVsyncOption()) {
			vSyncButton.setText("ON");
		} else {
			vSyncButton.setText("OFF");
		}
	}
	
	private void setFullscreenButtonText() {
		if (options.getFullScreenOption()) {
			fullScreenButton.setText("ON");
		} else {
			fullScreenButton.setText("OFF");
		}
	}
	
	private void setAvatarButtonText() {
		if (options.getAvatarOption()) {
			avatarButton.setText("ON");
		} else {
			avatarButton.setText("OFF");
		}
	}

	@Override public void hide() { }
	@Override public void pause() { }
	@Override public void resume() { }

}
