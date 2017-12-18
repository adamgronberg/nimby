package edu.chalmers.nimby.controller;

import edu.chalmers.nimby.model.gameLogic.EntityHandeler;

/**
 * Used to switch to different screens from another thread.
 * @author Adam Grönberg
 *
 */
public final class ScreenChanger extends EntityHandeler {
	
	private final Screen changeTo;
	
	/**
	 * Different screens to change between.
	 * @author Adam Grönberg
	 *
	 */
	public enum Screen { MATCH, LOBBY }
	
	/**
	 * Constructor.
	 * @param changeTo The screen to change to.
	 * @param game the active game.
	 */
	public ScreenChanger(final Screen changeTo, final LobbyScreen lobbyScreen) {
		super(lobbyScreen);
		this.changeTo = changeTo;
	}
	
	@Override
	public void command() {
		LobbyScreen lobbyScreen = (LobbyScreen) screen;
		switch(changeTo) {
		case MATCH:
			lobbyScreen.getGame().createAndSetMatchScreen();
			break;
		default:
			lobbyScreen.getGame().createAndSetLobbyScreen();
		}
	}
}
