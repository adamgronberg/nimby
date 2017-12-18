package edu.chalmers.nimby.network.match;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.chalmers.nimby.Nimby;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet1ConnectToMatchAnswer;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet2InitialisedShips;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet4MatchServerSnapShot;
import edu.chalmers.nimby.network.match.MatchNetwork.Packet7MatchServerProjectilesToRemove;

/**
 * Used to listen to the match server.
 * @author Adam Grönberg
 *
 */
public final class MatchServerListener extends Listener {
	
	public static final long WAIT_FOR_LOAD = 100;
	
	private Nimby game;
	
	private static MatchServerListener instance = null;

	/**
	 * Constructor.
	 */
	public MatchServerListener() { }
	
	/**
	 * Returns the instance of this class if it has been initialized.
	 * @return the instance, null if the LobbyServerHandle hasn't been initialized
	 */
	public static synchronized MatchServerListener getInstance() {
		if (instance == null) {
			instance = new MatchServerListener();
		} 
		return instance;
	}
	
	/**
	 * Initiates the MatchServerListener.
	 * @param game the game used
	 * @param pipes A class that holds all pipes
	 */
	public void init(final Nimby game) {
		this.game = game;
  	}

	/**
	 * Called when receiving a package from match server.
	 * @param server The server received from
	 * @param data	The data received
	 */
	@Override
	public void received(final Connection server, final Object data) {
		if (data instanceof Packet4MatchServerSnapShot) {
			Packet4MatchServerSnapShot updates = (Packet4MatchServerSnapShot) data;
			MatchScreen matchScreen = (MatchScreen) game.getScreen();
			matchScreen.synchClient(updates.partSnapshotList);
		} else if (data instanceof Packet7MatchServerProjectilesToRemove) {
			Packet7MatchServerProjectilesToRemove projectilesToRemove = (Packet7MatchServerProjectilesToRemove) data;
			MatchScreen matchScreen = (MatchScreen) game.getScreen();
			matchScreen.removeProjectiles(projectilesToRemove.idsToRemove);
		} else if (data instanceof Packet1ConnectToMatchAnswer) {
			Packet1ConnectToMatchAnswer idAnwser = (Packet1ConnectToMatchAnswer) data;
			IdentifiableEntity.setUserID(idAnwser.uniqueID);	
		}  else if (data instanceof Packet2InitialisedShips) {
			Packet2InitialisedShips ship = (Packet2InitialisedShips) data;
			while (!(game.getScreen() instanceof MatchScreen)) { 
				try {
					Thread.sleep(WAIT_FOR_LOAD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			MatchScreen matchScreen = (MatchScreen) game.getScreen();
			matchScreen.buildShips(ship.ship, ship.totalPackages);
		}
	}
}
