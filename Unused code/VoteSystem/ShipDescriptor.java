package edu.chalmers.nimby.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import edu.chalmers.nimby.controller.VoteScreen;
import edu.chalmers.nimby.model.PlayerShip;

/**
 * Contains the description of a ship and the if the ship is voted for or not.
 * @author Lucas Wiman, Adam Grönberg
 *
 */
public class ShipDescriptor extends Table {
	
	private TextureAtlas atlas;
	private Skin skin;
	//private ScrollPane scrollPane;
	private boolean votedOn = false;
	private PlayerShip ship;
	
	/**
	 * Constructor. Creates the ShipDiscription
	 * @param ship the ship to create a description for.
	 */
	public ShipDescriptor(final PlayerShip ship) {
		this.ship = ship;
		atlas = new TextureAtlas(Gdx.files.internal("ui/morebuttons.pack"));
		skin = new Skin(atlas);
		setSkin(skin);
		
		BitmapFont white = new BitmapFont(Gdx.files.internal("fonts/terminal.fnt"), false);
		
		/* Heading used to represent the ship image temporarily */
		LabelStyle headingStyle = new LabelStyle(white, Color.WHITE);
		Label heading = new Label(ship.getName(), headingStyle);
		heading.setFontScale(1);
		
		/*Creating buttonStyle*/
		final TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("blankbutton");
		textButtonStyle.down = skin.getDrawable("blankbuttonpressed");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		textButtonStyle.fontColor = Color.BLACK;
		
		TextButton voteButton = new TextButton("Vote", textButtonStyle);
		
		/*Adding buttonfunctions*/
		voteButton.addListener(new ClickListener() {
			public void clicked(final InputEvent event, final float x, final float y) {
				votedOn = !votedOn;
				if (votedOn) {
					textButtonStyle.fontColor = Color.CYAN;
				} else {
					textButtonStyle.fontColor = Color.BLACK;
				}
			}
		});
		
		add(heading);
		add(voteButton).bottom();
	}
	
	/**
	 * @return votedOn
	 */
	public final boolean isVotedOn() {
		return votedOn;
	}

	/**
	 * @return the ship
	 */
	public PlayerShip getShip() {
		return ship;
	}
	
	
}
