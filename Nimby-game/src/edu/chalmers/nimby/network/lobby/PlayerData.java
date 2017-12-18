package edu.chalmers.nimby.network.lobby;

import java.util.LinkedList;
import java.util.List;

import edu.chalmers.nimby.model.PlayerShip;

/**
 * This players data.
 * @author Mikael Stolpe
 *
 */
public final class PlayerData {
	private static PlayerData instance = null;
	private List<PlayerShip> ships; 
	
	public static synchronized PlayerData getInstance() {
		if (instance == null) {
			instance = new PlayerData();
		} 
		return instance;
	}

	private PlayerData() {
	}
	
	/**
	 * @return the ships
	 */
	public List<PlayerShip> getShips() {
		if (ships == null) {
			ships = new LinkedList<>();
		}
		return ships;
	}
}
