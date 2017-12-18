package edu.chalmers.nimby.model.gameLogic;

import edu.chalmers.nimby.network.match.UniqueIdentifier;

/**
 * Used to identify objects between different clients and match server.
 * @author Adam Grönberg
 *
 */
public abstract class IdentifiableEntity implements Entity {
	
	private static int partIDCounter = 0;
	private static int uniqueUserID = 0;
	private static int projectileIDCounter = 0;
	
	private int playerID = -1;
	private int projectileID = -1;
	private int partID = -1;
	
	private static int shipLocalUniqueIDCounter = 0;
	private int shipUniqueID;

	/**
	 * Gets the unique ID of a object.
	 * @return the unique ID, first object is userID, second is the object partID
	 */
	public final UniqueIdentifier getMatchPartUniqueID() {
		UniqueIdentifier partUniqueID = new UniqueIdentifier(playerID, partID);
		return partUniqueID;
	}
	
	public final UniqueIdentifier getMatchProjectileUniqueID() {
		UniqueIdentifier projectileUniqueID = new UniqueIdentifier(playerID, projectileID);
		return projectileUniqueID;
	}
	
	/**
	 * Gets the locally unique ID for this object. used locally within a ship to weld parts.
	 * @return locally unique ID
	 */
	public final int getShipLocalUniqueID() {
		return shipUniqueID;
	}
	
	/**
	 * Sets this user unique ID. The ID is given by the game server and is unique per user on that game.
	 * @param uniqueUserID the ID to set for this user
	 */
	public static final void setUserID(final int uniqueUserID) {
		IdentifiableEntity.uniqueUserID = uniqueUserID;
	}
	
	/**
	 * Sets the ID of a part. Used when creating a part from the network.
	 * @param uniqueID Sets the UniqueID.
	 */
	public final void setUniqueProjectileMatchID(final UniqueIdentifier uniqueID) {
		this.projectileID = uniqueID.entityID;
		this.playerID = uniqueID.userID;
	}
	
	/**
	 * Sets the ID of a part. Used when creating a part from the network.
	 * @param uniqueID Sets the UniqueID.
	 */
	public final void setUniquePartMatchID(final UniqueIdentifier uniqueID) {
		this.partID = uniqueID.entityID;
		this.playerID = uniqueID.userID;
	}
	
	/**
	 * Sets the ID of a part. Used when constructing a part that came from another client.
	 * @param uniqueID Sets the UniqueID.
	 */
	public final void setShipLocalUniqueID(final int uniqueID) {
		this.shipUniqueID = uniqueID;
	}
	
	/**
	 * Generates a match uniqueID.
	 */
	public final void generatePartUniqueMatchID() {
		this.playerID = uniqueUserID;
		this.partID = partIDCounter++;
	}
	
	/**
	 * Generates a match unique ID locally.
	 */
	public final void generatProjectileUniqueMatchID() {
		this.playerID = uniqueUserID;
		this.projectileID = projectileIDCounter++;
	}
	
	/**
	 * Generates a uniqueID for a part within a ship.
	 */
	public final void generateShipLocalUniqueID() {
		shipUniqueID = shipLocalUniqueIDCounter++;
	}
	
	/**
	 * Tests if a entity has the same {@link UniqueIdentifier}.
	 * @param entity the {@link IdentifiableEntity} to compare.
	 * @return	true if same {@link UniqueIdentifier}.
	 */
	public final boolean sameUniquePartID(final IdentifiableEntity entity) {
		if (entity == null) {
			return false;
		}
		return playerID == entity.playerID && partID == entity.partID;
	}
	
	/**
	 * Tests if a entity has the same {@link UniqueIdentifier}.
	 * @param entity the {@link IdentifiableEntity} to compare.
	 * @return	true if same {@link UniqueIdentifier}.
	 */
	public final boolean sameUniqueProjectileID(final IdentifiableEntity entity) {
		if (entity == null) {
			return false;
		}
		return playerID == entity.playerID && projectileID == entity.projectileID;
	}
	
	/**
	 * Resets the shipLocalUniqueIDCounter. Use this when a ship is done.
	 */
	public static void resetShipLocalUniqueIDCounter() {
		shipLocalUniqueIDCounter = 0;
	}
	
	/**
	 * Resets the match unique ID counters. Use this when a match has ended.
	 */
	public static void resetIDCounters() {
		uniqueUserID = 0;
		partIDCounter = 0;
		projectileIDCounter = 0;
	}
	
	/**
	 * @return the user ID that is unique for the current match.
	 */
	public static int getUserUniqueID() {
		return uniqueUserID;
	}
}
