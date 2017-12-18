package edu.chalmers.nimby.build;


/**
 * Filter enum, for determining collide patterns.
 * @author Mikael Stolpe
 *
 */
public enum BuildColliderFilter {
	NODE_SHIP,
	NODE_EQUIPMENT,
	PART_SHIP,
	PART_EQUIPMENT,
	BOUNDARY;
	
	/**
	 * @return the colDef
	 */
	public long getColDef() {
		return 1 << this.ordinal();
	}
};
