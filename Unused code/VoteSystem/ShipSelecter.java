package edu.chalmers.matchServer.selecter;

import java.util.HashMap;
import java.util.HashSet;

import edu.chalmers.nimby.model.PlayerShip;
import edu.chalmers.nimby.network.match.PlayerConnection;

/**
 * Calculates what ships a team should use in the game.
 * @author Adam Gr�nberg
 *
 */
public class ShipSelecter {
		
		private HashMap<PlayerShip, Integer> shipVotes;
		
		/**
		 * Constructor.
		 */
		public ShipSelecter() {
			shipVotes = new HashMap<PlayerShip, Integer>();
		}
		
		/**
		 * Creates a shipPool, containing all players ships.
		 * @param players the players to get the ships from
		 * @return a list containing all ships.
		 */
		public static final HashSet<PlayerShip> createShipPool(final HashSet<PlayerConnection> players) {
			HashSet<PlayerShip> ships = new HashSet<PlayerShip>();
			for (PlayerConnection player: players) {
				ships.addAll(player.getShips());
			}
			return ships;	
		}

		/**
		 * Adds the votes to the ships.
		 * @param votes the ships the player voted on.
		 */
		public final synchronized void addShipVotes(final HashSet<PlayerShip> votes) {
			for (PlayerShip ship: votes) {
				if (shipVotes.containsKey(ship)) {
					shipVotes.put(ship, shipVotes.get(ship) + 1);
				}
			}
		}
		
		/**
		 * Algorithm that selects the ships with the most votes.
		 * @return the ships that was selected.
		 * @param numberOfPlayers number of players in the team.
		 */
		public final HashSet<PlayerShip> selectShips(final int numberOfPlayers) { //TODO: Not fully implemented as of yet.
			int playersToBeSeated = numberOfPlayers;
			int highestVote = 0;
			PlayerShip mostPopularShip = null;
			HashSet<PlayerShip> selectedShips = new HashSet<PlayerShip>();
			
			while (playersToBeSeated > 0) {
				for (PlayerShip ship :shipVotes.keySet()) { //For each ship among those that have been voted on.
					if (shipVotes.get(ship) > highestVote) {
						highestVote = shipVotes.get(ship); //Determine the most popular ship
						mostPopularShip = ship;			   //Sets the most popular ship
					}
				}
				
				if (mostPopularShip.getNbrOfSeats() > playersToBeSeated) { //Special case
					shipVotes.remove(mostPopularShip);					//Removes the ship from the shipVotes
				} else {
					playersToBeSeated -= mostPopularShip.getNbrOfSeats(); //There are now less players to be seated.
					selectedShips.add(mostPopularShip);					  //This ship is now among the selected
					shipVotes.remove(mostPopularShip);
				}
			
				highestVote = 0; //Resets the highestVote for going through the shipVotes again.
			
			}
			
			
			
			return selectedShips;
		}
}
