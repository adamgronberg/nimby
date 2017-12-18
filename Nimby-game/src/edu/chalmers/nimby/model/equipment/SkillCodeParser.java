package edu.chalmers.nimby.model.equipment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.chalmers.nimby.model.equipment.skill.Skill;

/**
 * Parses SkillCode and adds it to a {@link EquipmentSkillHandler}.
 * 
 * Example of SkillCode:
 * <code>
 * # Rows that start with '#' are regarded as comments.
 * KEY_UP
 * FIRE_PROJECTILE;missile
 * END
 * 
 * KEY_DOWN
 * BOOST;10;0;0
 * END
 * </code>
 * @author Viktor Sjölind
 *
 */
public final class SkillCodeParser {

	private static final String DIVIDER = ";";
	private static final String KEY_UP = "KEY_UP";
	private static final String KEY_DOWN = "KEY_DOWN";
	private static final String NO_MODE = "NO_MODE";
	private static final String PATH = "edu.chalmers.nimby.model.equipment.skill.";
	private static final String END = "END";

	/**
	 *  NO USE.
	 */
	private SkillCodeParser() { }

	/**
	 * Parses skillCode and adds the created skills to the provided {@link EquipmentSkillHandler}.
	 * @param skillCode The SkillCode to parse formated as a {@link String}.
	 * @param equipmentSkillHandler The {@link EquipmentSkillHandler} to add the skills to.
	 */
	public static void parseSkillCode(final String skillCode, final EquipmentSkillHandler equipmentSkillHandler) {
		//System.out.println("PARSING");
		
		if (equipmentSkillHandler == null) {
			throw new IllegalArgumentException("equipmentSkillHandler cannot be null!");
		}
		
		if (skillCode == null || skillCode.isEmpty()) {
			throw new IllegalArgumentException("skillCode cannot be null or empty!");
		}
		
		Pattern pattern = Pattern.compile("\\{[^\\}]*\\}");
		Matcher matcher = pattern.matcher(skillCode);
		
		String currentMode = NO_MODE;
//		while (scanner.hasNext()) {
		while (matcher.find()) {
			
//			String line = scanner.nextLine().trim().replaceAll("\t", "");
			String line = matcher.group();
			line = line.substring(1, line.length() - 1);
			line = line.replaceAll("\n", "").trim().replaceAll("\t", "");
			
			//System.out.println("read line: "  + line);
			if (line.length() > 0 && line.charAt(0) != '#') {
				String[] split = line.split(DIVIDER);
				
				StringBuilder sb = new StringBuilder("Split: ");
				for (String s : split) {
					sb.append(s).append(" ");
				}
				sb.append("\n");
				//System.out.println(sb.toString());
				
				
				if (currentMode.equals(NO_MODE)) {
					//System.out.println("set mode to: " + split[0]);
					currentMode = split[0];
				} else if (split[0].equals(END)) {
					//System.out.println("set mode to NO_MODE");
					currentMode = NO_MODE;
				} else {
					Skill skill = parseLine(split);
					addSkill(skill, equipmentSkillHandler, currentMode);
				}
			}
		}
	}

	/**
	 * Parses a line of SkillCode and returns a Skill.
	 * @param split The line to parse trimmed and split to an {@link String}Array.
	 * @return A {@link Skill}.
	 */
	private static Skill parseLine(final String[] split) {
		//System.out.println("parseLine");
		
		try {
			Class<?> clazz = Class.forName(PATH + split[0]);
			Constructor<?> constructor = clazz.getConstructors()[0];
			Object[] params = new Object[constructor.getParameterTypes().length];

			if (params.length == 0) {
				return (Skill) clazz.newInstance();
			}
			
			if (split.length < params.length - 1) {
				throw new IllegalArgumentException("Not enough arguments provided by the split!");
			}
			
			for (int i = 0; i < params.length; i++) {
				params[i] = parseType(split[i + 1], constructor.getParameterTypes()[i]);
			}

			return (Skill) constructor.newInstance(params);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generates an Object from a {@link String} parameter and a clazz.
	 * @param param The parameter to the constructor.
	 * @param clazz The {@link Class} of the Object to generate.
	 * @return The generated Object. Returns null if clazz is not primitive or has a constructor that only takes a String.
	 */
	private static Object parseType(final	String param, final Class<?> clazz) {
		//System.out.println("parseType");
		Constructor<?> constructor;
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Integer.parseInt(param);
			} else if (clazz == long.class) {
				return Long.parseLong(param);
			} else if (clazz == float.class) {
				return Float.parseFloat(param);
			} else if (clazz == double.class) {
				return Double.parseDouble(param);
			} else if (clazz == short.class) {
				return Short.parseShort(param);
			} else if (clazz == boolean.class) {
				return Boolean.parseBoolean(param);
			} else if (clazz == char.class) {
				return param.charAt(0);
			} else if (clazz == byte.class) {
				return Byte.parseByte(param);
			}
			return null;
		} else {
			try {
				constructor = clazz.getConstructor(String.class);
				if (constructor != null) {
					return constructor.newInstance(param);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Adds a skill to the {@link EquipmentSkillHandler}.
	 * @param skill The {@link Skill} to add, can not be null.
	 * @param equipmentSkillHandler The {@link EquipmentSkillHandler} to add the skill to, can not be null.
	 * @param currentMode The mode in which to add to the {@link EquipmentSkillHandler}.
	 */
	private static void addSkill(final Skill skill, final EquipmentSkillHandler equipmentSkillHandler, final String currentMode) {
		//System.out.println("addSkill");
		if (skill == null) {
			throw new IllegalArgumentException("skill cannot be null!");
		}
		
		if (equipmentSkillHandler == null) {
			throw new IllegalArgumentException("equipmentSkillHandler cannot be null!");
		}
		
		switch (currentMode) {
		case KEY_UP: equipmentSkillHandler.addOnUp(skill); break;
		case KEY_DOWN: equipmentSkillHandler.addOnDown(skill); break;
		default: throw new IllegalArgumentException("Cannot add skill illegal mode set: " + currentMode);
		}
	}
	
}
