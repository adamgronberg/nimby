package edu.chalmers.nimby;

import com.badlogic.gdx.Game;

import edu.chalmers.nimby.avatar.Avatar;
import edu.chalmers.nimby.chat.TempChat;
import edu.chalmers.nimby.controller.BuildScreen;
import edu.chalmers.nimby.controller.CreateAccountScreen;
import edu.chalmers.nimby.controller.LobbyScreen;
import edu.chalmers.nimby.controller.LoginScreen;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.controller.OptionScreen;
import edu.chalmers.nimby.network.lobby.LobbyServerHandle;
import edu.chalmers.nimby.network.lobby.LobbyServerListener;
import edu.chalmers.nimby.network.match.MatchServerHandle;
import edu.chalmers.nimby.network.match.MatchServerListener;
import edu.chalmers.nimby.options.OptionLogic;
import edu.chalmers.nimby.util.Pipe;
import edu.chalmers.nimby.view.ChatWindow;

/**
 * Controls communication between screens and communication between network listener interface and screens.
 * @author Adam Gr�nberg
 */
public final class Nimby extends Game {

	private ChatWindow chatWindow;

	@Override
	public void create() {
		Pipe<String> chatPipe = new Pipe<>();
		chatWindow = new ChatWindow(chatPipe);
		Avatar.getInstance().init();
		
		MatchServerListener.getInstance().init(this);
		MatchServerHandle.getInstance().init();
		LobbyServerListener.getInstance().init(this, chatPipe);
		LobbyServerHandle.getInstance().init();
		
		initLobbyServerConnection();
		
		new Thread(new TempChat(chatPipe)).start();
		
		OptionLogic.getInstance().loadAndSetOptions();

		//setScreen(new BuildScreen(this));
		setScreen(new LoginScreen(this));
		//setScreen(new OptionScreen(this));
		//setScreen(new MatchScreen(this));
		//createAndSetLobbyScreen();
	}
	
	/**
	 * Initializes the connection to the lobby server.
	 */
	public void initLobbyServerConnection() {
		LobbyServerHandle lobbyServerHandle = LobbyServerHandle.getInstance();
		lobbyServerHandle.connectToServer(NetworkConstants.LOBBY_SERVER_IP, 
				NetworkConstants.LOBBY_SERVER_TCP_PORT, 
				NetworkConstants.LOBBY_SERVER_UDP_PORT);
	}
	
	/**
	 * Initializes the connection to the match server.
	 * @param matchIP The IP of the match to connect to.
	 * @param matchID The ID of the match to connect to.
	 */
	public void initMatchServerConnection(final String matchIP, final int matchID) {
		MatchServerHandle matchServerHandle = MatchServerHandle.getInstance();
		MatchServerHandle.setCurrentMatchID(matchID);
		matchServerHandle.connectToServer(matchIP, NetworkConstants.MATCH_SERVER_TCP_PORT, 
				NetworkConstants.MATCH_SERVER_UDP_PORT);
		matchServerHandle.sendConnectionRequest();
	}
	
	/**
	 * Creates the lobby screen and sets is as screen.
	 */
	public void createAndSetLobbyScreen() {
		setScreen(new LobbyScreen(this));
	}
	
	/**
	 * Creates the build screen and sets is as screen.
	 */
	public void createAndSetBuildScreen() {
		setScreen(new BuildScreen(this));
	}
	
	/**
	 * Creates the match screen and sets is as screen.
	 */
	public void createAndSetMatchScreen() {
		setScreen(new MatchScreen(this));
	}
	
	/**
	 * Creates the Login screen and sets is as screen.
	 */
	public void createAndSetLoginScreen() {
		setScreen(new LoginScreen(this));
	}
	
	/**
	 * Creates the option screen and sets it as screen.
	 */
	public void createAndSetOptionScreen() {
		setScreen(new OptionScreen(this));
	}
	
	/**
	 * Creates the create account screen and sets it as screen.
	 */
	public void createAndSetCreateAccountScreen() {
		setScreen(new CreateAccountScreen(this));
	}
	
	/**
	 * @return chatWindow used.
	 */
	public ChatWindow getChatWindow() {
		return chatWindow;
	}
}
