package edu.chalmers.nimby.network.match;

/**
 * Used to uniquely identify a entity across several clients.
 * Set only ONCE, {@link UniqueIdentifier} should be final!
 * @author Adam Grönberg
 *
 */
public class UniqueIdentifier {
	public int userID = -1;
	public int entityID = -1;
	private static final int HASHCODE_MULTIPLIER = 41;
	
	public UniqueIdentifier() { }
	
	public UniqueIdentifier(final int userID, final int partID) {
		this.userID = userID;
		this.entityID = partID;
	}
	
	/**
	 * Returns true if the ID is the same.
	 * @param obj	The ID to test
	 * @return	true if equals.
	 */
	@Override
	public final boolean equals(final Object obj) {
		
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UniqueIdentifier)) {
            return false; 
        }
		UniqueIdentifier id = (UniqueIdentifier) obj;
		return userID == id.userID && entityID == id.entityID;
	}
	
	/**
	 * Does not work if you change the value of a unique ID.
	 * @return the hash code used.
	 */
    @Override
	public final int hashCode() {
        return (HASHCODE_MULTIPLIER * (HASHCODE_MULTIPLIER + userID) + entityID);
    }
}
