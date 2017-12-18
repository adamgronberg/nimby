package edu.chalmers.matchServer;

import edu.chalmers.nimby.network.match.MatchNetwork;
import edu.chalmers.nimby.network.match.UniqueIdentifier;

/**
 * The information given by a client.
 * @author Adam Grönberg
 *
 */
public final class PlayerCommand {
	public final MatchNetwork.KeyPress keyPress;
	public final UniqueIdentifier activatedEntityID; 
	public final UniqueIdentifier spawnedEntityID;
	
	/**
	 * The information about a command given by client.
	 * @param playerID the ID of the player who sent the command.
	 * @param partID the id of the part that was triggered.
	 * @param keyPress the type of trigger.
	 */
	public PlayerCommand(final UniqueIdentifier activatedEntityID, final MatchNetwork.KeyPress keyPress, 
							final UniqueIdentifier spawnedEntityID) {
		this.keyPress = keyPress;
		this.activatedEntityID = activatedEntityID;
		this.spawnedEntityID = spawnedEntityID;
	}
}
