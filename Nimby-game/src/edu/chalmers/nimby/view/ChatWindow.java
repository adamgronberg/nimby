package edu.chalmers.nimby.view;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.esotericsoftware.tablelayout.Cell;

import edu.chalmers.nimby.chat.InputDecider;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.util.Pipe;
import edu.chalmers.nimby.util.TextFilter;

/**
 * Chat window, displays and takes the input from the chat.
 * @author Adam Gr�nberg, Lucas Wiman, Gustav Dahl
 *
 */
public class ChatWindow extends Table implements Entity, InputProcessor {

	public static final int CHAT_WIDTH = 325;
	public static final int CHAT_HEIGHT = 225;
	public final float chatInputHeight;
	
	public static final float TEXT_SCALE = 0.01f;
	private static final int NUMBER_OF_CHATMESSAGES = 23;
	
	private Pipe<String> chatPipe;
	private ScrollPane scrollPane;
	private int messageFieldCounter;
	private Table textAreaHolder;
	private Table textInputHolder;
	private float fontSize;
	private boolean chatAreaHoldFull = false;
	
	private final Skin skin;
	private final BitmapFont white;
	private final TextFieldStyle textFieldStyle;
	private final TextField chatField;
	private final TextFilter filter;
	
	/**
	 * Constructor. Creates the window and sets its listener.
	 * @param chatPipe Pipe used to get and put input
	 */
	public ChatWindow(final Pipe<String> chatPipe) {
		this.chatPipe = chatPipe;
		messageFieldCounter = 0;
		
		TextureAtlas atlas = new TextureAtlas("ui/superui.pack");
		skin = new Skin(atlas);
		
		white = new BitmapFont(Gdx.files.internal("fonts/newChatWindowText.fnt"), false);
		fontSize = white.getLineHeight();
		chatInputHeight = white.getLineHeight();
		white.scale(TEXT_SCALE);
		filter = new TextFilter();
		
		/* Making a textfield style */
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.font = white;
		textFieldStyle.focusedFontColor = Color.CYAN;
		
		/*Area where all chat appears*/
		textAreaHolder = new Table();
		textAreaHolder.setWidth(CHAT_WIDTH);
		textAreaHolder.setBackground(skin.getDrawable("chatwindow"));
		textAreaHolder.debug();
		
		textInputHolder = new Table();
		textInputHolder.setBackground(skin.getDrawable("chatbar"));
		textInputHolder.setVisible(false);

		/*Applies the scrollpane to the chat area*/
		scrollPane = new ScrollPane(textAreaHolder);
		scrollPane.setWidth(CHAT_WIDTH);
		scrollPane.setForceScroll(false, true);
		scrollPane.setFlickScroll(true);
		scrollPane.setOverscroll(false, false);
		scrollPane.setScrollingDisabled(true, false);
		//scrollPane.setScrollPercentY(-50);
		
		/*Input chat*/
		chatField = new TextField("", textFieldStyle);
		chatField.setTextFieldFilter(filter);
		textInputHolder.add(chatField).height(chatInputHeight).width(CHAT_WIDTH);

		/*Tries to make the textField react on enter?*/
		chatField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(final TextField textField, final char key) {
				if (key == '\n' || key == '\r') {
					if (!textField.getText().isEmpty()) {
						StringBuilder message = new StringBuilder();   						  //Creates the message
						message.append(chatField.getText());		   						  //Appends the chatfield entry
						InputDecider.inputDecision(message.toString(), chatPipe);
						chatField.setText("");
						getStage().setKeyboardFocus(null);
						textInputHolder.setVisible(false);
					}
				}
			}
		});
		//// TODO OMG TEMP!
		for (int i = 1; i < NUMBER_OF_CHATMESSAGES; i++) {
			TextArea chatArea = new TextArea("", textFieldStyle); //Creates a chatArea with the message
			chatArea.setName("" + messageFieldCounter);
			chatArea.setHeight(fontSize + 1);
			chatArea.setDisabled(true);
			chatArea.setTextFieldFilter(filter);
			messageFieldCounter = (messageFieldCounter + 1) % NUMBER_OF_CHATMESSAGES;
			textAreaHolder.row();
			textAreaHolder.add(chatArea).height(chatInputHeight - 1).width(CHAT_WIDTH - 1);
		}
		//////////////
		
		add(scrollPane).height(CHAT_HEIGHT - chatInputHeight).width(CHAT_WIDTH);
		row();
		add(textInputHolder).height(chatInputHeight).width(CHAT_WIDTH);
		
	}
	
	@Override
	public final void update(final float delta, final OrthographicCamera cam) {
		while (chatPipe.hasNext()) {
			String chatMessage = chatPipe.pull();

			if (!chatAreaHoldFull && messageFieldCounter >= NUMBER_OF_CHATMESSAGES - 1) {
				chatAreaHoldFull = true;
			}
			if (chatAreaHoldFull) {
				removeTableRow(0);
			}
			textAreaHolder.row();

			TextArea chatArea = new TextArea(chatMessage.toString(), textFieldStyle); //Creates a chatArea with the message
			chatArea.setName("" + messageFieldCounter);
			chatArea.setHeight(fontSize + 1);
			chatArea.setDisabled(true);
			chatArea.setTextFieldFilter(filter);
			
			textAreaHolder.add(chatArea).height(chatInputHeight - 1).width(CHAT_WIDTH - 1);
			scrollPane.scrollTo(0, -CHAT_HEIGHT + chatInputHeight, 0, -CHAT_HEIGHT + chatInputHeight);
			messageFieldCounter = (messageFieldCounter + 1) % NUMBER_OF_CHATMESSAGES;
		}
	}

	/**
	 * Sets focus on the chatWindow.
	 */
	public void focusField() {
		getStage().setKeyboardFocus(chatField);
		textInputHolder.setVisible(true);
		chatField.getOnscreenKeyboard();
	}

	/**
	 * Removes the first row in a table.
	 * @param row the row you which to remove. 
	 */
	@SuppressWarnings("unchecked")
	public void removeTableRow(final int row) {
		@SuppressWarnings("rawtypes")
		List<Cell> cells = textAreaHolder.getCells(); 

	    //Remove contents of first row
	    cells.get(0).setWidget(null);

	    //Copy all cells up one row
	    for (int i = 0; i < cells.size() - 1; i++) {
	        cells.set(i, cells.get(i + 1));
	    }

	    //Remove the last row
	    cells.remove(cells.size() - 1);
	}

	@Override
	public boolean keyDown(final int keycode) {
		if (keycode == Input.Keys.ENTER) {
			focusField();
		}
		return false;
	}

	@Override public boolean keyUp(final int keycode) { return false; }
	@Override public boolean keyTyped(final char character) { return false; }
	@Override public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) { return false; }
	@Override public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) { return false; }
	@Override public boolean touchDragged(final int screenX, final int screenY, final int pointer) { return false; }
	@Override public boolean mouseMoved(final int screenX, final int screenY) { return false; }
	@Override public boolean scrolled(final int amount) { return false; }
}

