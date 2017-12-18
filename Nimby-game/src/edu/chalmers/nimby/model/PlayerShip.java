package edu.chalmers.nimby.model;



/**
 * This class represents a ship in the game.
 * @author Lucas, Adam Grönberg
 *
 */
public final class PlayerShip {
	private String shipName;
	private byte[] shipBlob;
	
	public PlayerShip(final String name, final byte[] ship) {
		this.shipName = name;
		this.shipBlob = ship;
	}

	public PlayerShip() { }

	/**
	 * Copy constructor.
	 * @param playerShip
	 */
	public PlayerShip(final PlayerShip playerShip) {
		shipName = playerShip.getName();
		shipBlob = playerShip.getShipBlob();
	}
	
	/**
	 * Returns the name of the ship.
	 * @return the name
	 */
	public String getName() {
		return shipName;
	}
	
	/**
	 * Sets the name of the ship.
	 * @param name name to be set
	 */
	public void setNames(final String name) {
		shipName = name;
	}
	
	/**
	 * @return ship blob
	 */
	public byte[] getShipBlob() {
		return shipBlob;
	}
	
	/**
	 * @param shipBlob ship blob
	 */
	public void setShipBlob(final byte[] shipBlob) {
		this.shipBlob = shipBlob;
	}
}
