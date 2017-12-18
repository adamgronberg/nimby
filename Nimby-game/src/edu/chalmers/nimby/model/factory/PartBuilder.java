package edu.chalmers.nimby.model.factory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.utils.Base64Coder;

import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.network.match.UniqueIdentifier;

/**
 * Creates {@link Part}s from {@link Blob} or {@link Part} from {@link Blob}.
 * @author Viktor Sjölind
 *
 */
public final class PartBuilder {

	// SHIPPARTS AND EQUIPMENT
	private static final int X = 1;
	private static final int Y = 2;
	private static final int WIDTH = 3;
	private static final int HEIGHT = 4;
	private static final int MASS = 5;
	private static final int PART_NAME = 6;
	private static final int TEXTURE_PATH = 7;
	private static final int PART_ID = 8;
	private static final int USER_ID = 9;
	private static final int SHIP_LOCAL_PART_ID = 10;
	private static final int COOLDOWN = 11;
	private static final int SKILL_CODE = 12;
	private static final int INVOKE_KEY = 13;
	private static final int JOINT_RELATIVE_X = 14;
	private static final int JOINT_RELATIVE_Y = 15;
	private static final int ANGLE = 16;

	// WELD
	private static final int SHIP_LOCAL_PART_ID_1 = 1;
	private static final int SHIP_LOCAL_PART_ID_2 = 2;
	private static final int LIMIT_ANGLE = 3;
	private static final int LOWER_ANGLE = 4;
	private static final int UPPER_ANGLE = 5;
	private static final int MAX_MOTOR_TORQUE = 6;

	// OTHER 
	private static final String WELD = "Weld";
	private static final String SHIP_PART = "ShipPart";
	private static final String EQUIPMENT = "Equipment";
	private static final String DIVIDE = ",";
	private static final String BLOCKEND = "\n";
	private static final char COMMENT_TAG = '#';
	private static final String START = " -START- ";
	private static final String END = " - END - ";
	
	/**
	 * Constructor block. No constructor for the class...
	 */
	private PartBuilder() { }

	/**
	 * Creates a blob from a {@link Part} and all its sub parts.
	 * @param mainPart the {@link Part} to create a blob representation of.
	 * @return A {@link Blob} representing the Part, null if the serialization fails.
	 */
	public static byte[] createBlob(final Part mainPart) {
		String blobString = serializePart(mainPart);
		return blobString.getBytes();
	}

	/**
	 * Serializes a {@link Part} with all its part to a String.
	 * @param part The part to serialize.
	 * @return A String representation of the Part.
	 */
	private static String serializePart(final Part part) {
		String string = serializePart(part, new HashSet<Integer>());
		return Base64Coder.encodeString(string);
	}
	
	/**
	 * Serializes a {@link Part} with all its part to a String.
	 * @param part The part to serialize.
	 * @param serialized a Set containing the {@link UniqueIdentifier}s of already serialized {@link Part}s.
	 * @return The produced String.
	 */
	private static String serializePart(final Part part, final Set<Integer> serialized) {
		StringBuilder sb = new StringBuilder();
		
		if (part instanceof ShipPart) {
			sb.append(SHIP_PART).append(DIVIDE);
		} else if (part instanceof Equipment) {
			sb.append(EQUIPMENT).append(DIVIDE);
		}
		
		sb.append(part.getPosition().x).append(DIVIDE);					//1
		sb.append(part.getPosition().y).append(DIVIDE);					//2
		sb.append(part.width).append(DIVIDE);							//3
		sb.append(part.height).append(DIVIDE);							//4
		sb.append(part.body.getMass()).append(DIVIDE);					//5
		sb.append(part.partName).append(DIVIDE);						//6
		sb.append(part.texturePath).append(DIVIDE);						//7
		sb.append(part.getMatchPartUniqueID().entityID).append(DIVIDE);		//8
		sb.append(part.getMatchPartUniqueID().userID).append(DIVIDE);		//9
		sb.append(part.getShipLocalUniqueID());							//10
		
		if (part instanceof Equipment) {
			Equipment eq = (Equipment) part;
			sb.append(DIVIDE);
			sb.append(eq.getCooldownMax()).append(DIVIDE);					//11
			sb.append(eq.getSkillCode()).append(DIVIDE);				//12
			sb.append(eq.getKey()).append(DIVIDE);						//13
			sb.append(eq.getJointRelativePos().x).append(DIVIDE);		//14
			sb.append(eq.getJointRelativePos().y).append(DIVIDE);		//15
			sb.append(eq.body.getAngle());								//16
		}
		
		serialized.add(part.getShipLocalUniqueID());
		sb.append(BLOCKEND);
		if (part instanceof ShipPart) {
			ShipPart shipPart = (ShipPart) part;
			
			// RECURSIVE CALLS
			if (shipPart.getParent() != null && !serialized.contains(shipPart.getParent().getShipLocalUniqueID())) {
				sb.append(serializePart(shipPart.getParent(), serialized));
			}
			
			for (Part p: shipPart.getChildren()) {
				if (!serialized.contains(p.getShipLocalUniqueID())) {
					sb.append(serializePart(p, serialized));
				}
			}
			
			for (RevoluteJoint j : shipPart.getJoints()) {
				Equipment equipment = (Equipment) j.getUserData();
				if (!serialized.contains(equipment.getShipLocalUniqueID())) {
					sb.append(serializePart(equipment, serialized));
				}
			}
			
			// WELDS
			for (Part p: shipPart.getChildren()) {
				sb.append(WELD).append(DIVIDE);
				sb.append(shipPart.getShipLocalUniqueID()).append(DIVIDE);
				sb.append(p.getShipLocalUniqueID()).append(BLOCKEND);
			}
			
			for (RevoluteJoint j : shipPart.getJoints()) {
				sb.append(WELD).append(DIVIDE);
				sb.append(shipPart.getShipLocalUniqueID()).append(DIVIDE);
				
				Equipment equipment = (Equipment) j.getUserData();
				sb.append(equipment.getShipLocalUniqueID()).append(DIVIDE);
				sb.append(j.isLimitEnabled()).append(DIVIDE);
				sb.append(j.getLowerLimit()).append(DIVIDE);
				sb.append(j.getUpperLimit()).append(DIVIDE);
				sb.append(j.getMaxMotorTorque()).append(BLOCKEND);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Takes a blob and creates a Space ship out of it.
	 * Creating and welding together all parts.
	 * @param blob The blob to read
	 * @param world The world in which to create the ship.
	 * @return A {@link Part} array containing all the parts of the ship.
	 * @throws SQLException Thrown if unable to get BinaryStream from blob.
	 * @throws UnsupportedEncodingException Thrown if unable to convert blob to a string (something went wrong when packing)
	 */
	public static Part[] createSpaceShip(final byte[] blob, final World world) throws SQLException, UnsupportedEncodingException {
		String blobString = new String(blob, StandardCharsets.UTF_8);
		return parseShipString(Base64Coder.decodeString(blobString), world);
	}

	/**
	 * @param blobString the String to parse.
	 * @param world The world in which the parts shall be created
	 * @return an array of {@link Part}s.
	 */
	private static Part[] parseShipString(final String blobString, final World world) {
		final List<Part> pList = new LinkedList<>();
		final Scanner s = new Scanner(blobString);

		while (s.hasNext()) {
			final String line = s.nextLine();
			if (!line.isEmpty() && line.charAt(0) != COMMENT_TAG) {
				final String[] split = line.split(DIVIDE);
				
				// CASE: CONSTRUCT PART
				if (split[0].equals(SHIP_PART) || split[0].equals(EQUIPMENT)) {
					
					// LOAD VARIABLES USED BY ALL PARTS
					final float x = Float.parseFloat(split[X]);
					final float y = Float.parseFloat(split[Y]);
					final Vector2 pos = new Vector2(x, y);

					String texturePath = split[TEXTURE_PATH];
					final float width = Float.parseFloat(split[WIDTH]);
					final float height = Float.parseFloat(split[HEIGHT]);
					final float mass = Float.parseFloat(split[MASS]);
					final String partName = split[PART_NAME];

					final int partId  = Integer.parseInt(split[PART_ID]);
					final int userId  = Integer.parseInt(split[USER_ID]);
					final UniqueIdentifier ud = new UniqueIdentifier();
					ud.entityID = partId;
					ud.userID = userId;
					final int shipLocalPartID = Integer.parseInt(split[SHIP_LOCAL_PART_ID]);

					// IF SHIPPART, CONTRUCT A SHIPPART
					if (split[0].equals(SHIP_PART)) {
						final ShipPart sp = new ShipPart(world, pos, width, height, mass, partName, texturePath);
						sp.setUniquePartMatchID(ud);
						sp.setShipLocalUniqueID(shipLocalPartID);
						pList.add(sp);

					// ELSE IF EQUIPMENT, ADD EQUIPMENT SPECIFIC STUFF AND CONSTRUCT AN EQUIPMENT
					} else if (split[0].equals(EQUIPMENT)) {
						final float cooldown = Float.parseFloat(split[COOLDOWN]);
						final int key = Integer.parseInt(split[INVOKE_KEY]);
						final String skillCode = split[SKILL_CODE];
						
						final float angle = Float.parseFloat(split[ANGLE]);
						
						final Equipment eq = new Equipment(world, pos, angle, width, height, mass, partName, texturePath, cooldown, skillCode, key);
						eq.setUniquePartMatchID(ud);
						eq.setShipLocalUniqueID(shipLocalPartID);
						
						final float jointRelativeX = Float.parseFloat(split[JOINT_RELATIVE_X]);
						final float jointRelativeY = Float.parseFloat(split[JOINT_RELATIVE_Y]);
						eq.getJointRelativePos().set(jointRelativeX, jointRelativeY);
						
						pList.add(eq);
					} 
					
				// CASE: DO WELDING 
				} else if (split[0].equals(WELD)) {
					
					final int shipLocalUniqueID1 = Integer.parseInt(split[SHIP_LOCAL_PART_ID_1]);
					final int shipLocalUniqueID2 = Integer.parseInt(split[SHIP_LOCAL_PART_ID_2]);

					// FIND PARTS
					final Part part1 = findPart(shipLocalUniqueID1, pList);
					final Part part2 = findPart(shipLocalUniqueID2, pList);
					
					if (part1 == null || part2 == null) {
						s.close();
						throw new IllegalArgumentException("Part1 or part2 not found when welding ...");
					}
					
					// START WELDING
					if (part1 instanceof ShipPart) {
						ShipPart shipPart = (ShipPart) part1;
						
						// IF BOTH PARTS ARE INSTANCES OF SHIPPART, DO A NORMAL WELD.
						if (part2 instanceof ShipPart) {
							shipPart.weld(world, (ShipPart) part2);
							
						// ELSE IF PART2 IS AN INSTANCE OF EQUIPMENT, CREATE A REVLOUTEJOINT INSTEAD.
						} else if (part2 instanceof Equipment) {
							
							final boolean limit = Boolean.parseBoolean(split[LIMIT_ANGLE]);
							
							final float lowerAngle = Float.parseFloat(split[LOWER_ANGLE]);
							final float upperAngle = Float.parseFloat(split[UPPER_ANGLE]);
							final float maxMotorTorque = Float.parseFloat(split[MAX_MOTOR_TORQUE]);
							
							Equipment equipment = (Equipment) part2;
							equipment.createRevoluteJoint(shipPart, equipment.getJointRelativePos());
						}
					} else {
						s.close();
						throw new IllegalArgumentException("Part1 is not an instance of ShipPart!");
					}
				}
			}
		}

		// CLEAN UP AND RETURN
		s.close();
		Part[] partList = new Part[pList.size()];
		return pList.toArray(partList);
	}

	/**
	 * @param shipLocalUniqueID The local unique id to search for.
	 * @param pList The {@link List} to 
	 * @return The {@link Part} if found, else null
	 */
	private static Part findPart(final int shipLocalUniqueID, final List<Part> pList) {
		for (Part p: pList) {
			if (p.getShipLocalUniqueID() == shipLocalUniqueID) {
				return p;
			}
		}
		return null;
	}
}
