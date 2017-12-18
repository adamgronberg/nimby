package edu.chalmers.nimby.network.match;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Base64Coder;

/**
 * Contains correction parameters for the client sync.
 * @author Adam Gr�nberg
 *
 */
public final class EntitySnapshot {
	public transient int playerMatchUniqueID;
	public transient int partMatchUniqueID;
	public transient int projectileMatchUniqueID;
	public transient float angle;
	public transient Vector2 pos;
	public transient Vector2 velocity;
	
	private byte[] serialized;
	private static final String DIVIDER = ";";
	
	private static final int PLAYER_MATCH_UNIQUE_ID = 0;
	private static final int PART_MATCH_UNIQUE_ID = 1;
	private static final int PROJECTILE_MATCH_UNIQUE_ID = 2;
	private static final int ANGLE = 3;
	private static final int POSITION = 4;
	private static final int VELOCITY = 5;
	
	/**
	 * Serializes the EntitySnapshot in base64 encoding.
	 * nulls all the paramters.
	 */
	public EntitySnapshot serialize() {
		if (pos == null || velocity == null) {
			throw new IllegalArgumentException("velocity or pos is null");
		}
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append(playerMatchUniqueID).append(DIVIDER);
		stringBuffer.append(partMatchUniqueID).append(DIVIDER);
		stringBuffer.append(projectileMatchUniqueID).append(DIVIDER);
		stringBuffer.append(angle).append(DIVIDER);
		stringBuffer.append(pos).append(DIVIDER);
		stringBuffer.append(velocity).append(DIVIDER);
		
		String serializeString = stringBuffer.toString();
		serializeString = Base64Coder.encodeString(serializeString);
		
		serialized = serializeString.getBytes();
		
		playerMatchUniqueID = 0;
		partMatchUniqueID = 0;
		projectileMatchUniqueID = 0;
		angle = 0;
		pos = null;
		velocity = null;
		
		return this;
	}
	
	/**
	 * Loads the parameters from the serialized version.
	 */
	public EntitySnapshot unserialize() {
		if (serialized == null || serialized.length == 0) {
			throw new IllegalArgumentException("The object is not serialized!");
		}
		
		String serializeString = new String(serialized);
		serializeString = Base64Coder.decodeString(serializeString);

		String[] split = serializeString.split(DIVIDER);
		
		playerMatchUniqueID = Integer.parseInt(split[PLAYER_MATCH_UNIQUE_ID]);
		partMatchUniqueID = Integer.parseInt(split[PART_MATCH_UNIQUE_ID]);
		projectileMatchUniqueID = Integer.parseInt(split[PROJECTILE_MATCH_UNIQUE_ID]);
		angle = Float.parseFloat(split[ANGLE]);
		pos = stringToVector2(split[POSITION]);
		velocity = stringToVector2(split[VELOCITY]);
		
		return this;
	}

	private Vector2 stringToVector2(final String string) {
		String[] split = string.substring(1, string.length() - 1).split(":");
		
		float x = Float.parseFloat(split[0]);
		float y = Float.parseFloat(split[1]);
		
		Vector2 vector = new Vector2(x, y);
		return vector;
	}
	
}
