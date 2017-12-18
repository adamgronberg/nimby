package edu.chalmers.nimby.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses An Object to or from JSON.
 * @author Viktor Sjölind
 *
 */
public final class JSONParser {

	/**
	 * UNUSABLE CLASS IS STATIC!
	 */
	private JSONParser() { };

	private static final String FIELD_REGEXP = "[^,\\{\\[]*(\\[.*\\])*(\\{.*\\})*,?";

	/**
	 * Takes a Object and returns a JSON representation of it as a String.
	 * @param o The object to turn to JSON.
	 * @return The Object represented as a JSON formated String.
	 * @throws IllegalAccessException If fields cannot be accessed.
	 */
	public static String toJSON(final Object o) throws IllegalAccessException {
		return toJSON(o, 0);
	}

	/**
	 * Helper method.
	 * @param o The object to turn to JSON
	 * @param depth used to identify tab length
	 * @return string formated as JSON
	 * @throws IllegalAccessException If fields cannot be accessed.
	 */
	private static String toJSON(final Object o, final int depth) throws IllegalAccessException {
		StringBuilder sb = new StringBuilder();

		if (o instanceof Byte
				|| o instanceof Character
				|| o instanceof Integer
				|| o instanceof Long
				|| o instanceof Float
				|| o instanceof Double
				|| o instanceof Boolean) {
			sb.append(o);

		} else if (o instanceof String 
				|| o instanceof Character) {
			sb.append("\"").append(o).append("\"");

		} else if (o.getClass().isArray()) {
			sb.append("[\n");
			Object[] oarr = (Object[]) o;

			for (int i = 0; i < oarr.length; i++) {
				if (i != 0) {
					sb.append(",\n");
				}
				sb.append(toJSON(oarr[i], depth + 1));
			}
			sb.append("]");
		} else {
			parseFields(sb, o, depth);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param sb The {@link StringBuilder} used to build the JSON string.
	 * @param o The object whos fields shall be parsed.
	 * @param depth The tab depth.
	 * @throws IllegalAccessException If fields cannot be accessed.
	 */
	private static void parseFields(final StringBuilder sb, final Object o, final int depth) throws IllegalAccessException {
		sb.append("{\n");

		Field[] fields =  o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {

			fields[i].setAccessible(true); 
			Object value = fields[i].get(o);

			if (value != null) {
				if (i != 0) {
					sb.append(",\n");
				}

				for (int tabs = depth + 1; tabs > 0; tabs--) {
					sb.append('\t');
				}
				sb.append("\"").append(fields[i].getName()).append("\"");
				sb.append(": ");
				sb.append(toJSON(value, depth + 1));
			}
		}
		sb.append("\n");
		for (int tabs = depth; tabs > 0; tabs--) {
			sb.append('\t');
		}
		sb.append("}");
	}

	/**
	 * Takes a JSON formatted string and creates an Object out of it.
	 * @param jsonString The JSON formated string.
	 * @param type The Type of the object
	 * @return The JSON string represented as an Object ()
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchFieldException 
	 */
	public static Object toObject(final String jsonString, final Class<?> type) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
		//		Object obj = type.getConstructor(null).newInstance(null);
		String whole = jsonString.trim().replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
		
		Object obj;
		if (type.isArray()) {
			return parseFieldValue(whole, type);
		} else {
			obj = type.newInstance();			
		}
		
		parseLine(whole, obj);

		return obj;
	}

	/**
	 * Parses a section of the JSON.
	 * @param line the section to parse
	 * @param obj the current parent object
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static void parseLine(final String line, final Object obj) throws NoSuchFieldException, IllegalAccessException  {
		Scanner ilineScanner = new Scanner(line);

		// Starts with { equals Object
		if (line.charAt(0) == '{') {

			String iline = ilineScanner.next("\\{.*\\}");
			iline = iline.substring(1, iline.length() - 1);

			Pattern pattern = Pattern.compile(FIELD_REGEXP);
			Matcher m = pattern.matcher(iline);

			while (m.find()) {
				String iiLine = m.group();
				if (!iiLine.isEmpty()) {
					// Clean and parse field
					if (iiLine.charAt(iiLine.length() - 1) == ',') {
						iiLine = iiLine.substring(0, iiLine.length());
					}
					parseLine(iiLine, obj);
				}
			}

		} else if (line.charAt(0) == '"') { // is a field
			// getting just the content inside { } 
			Pattern fieldNamePattern = Pattern.compile("\"[^{}]*\":");
			Matcher m = fieldNamePattern.matcher(line);
			m.find();

			// Fieldname
			String fieldName = m.group();
			int startValueIx = fieldName.length();
			fieldName = fieldName.substring(1, fieldName.length() - 2);
			Field f = obj.getClass().getField(fieldName);

			// Value as a String
			String value = (String) line.subSequence(startValueIx, line.length());
			if (value.charAt(value.length() - 1) == ',') {
				value = value.substring(0, value.length() - 1);
			}

			f.set(obj, parseFieldValue(value, f.getType()));			

		}
		ilineScanner.close();
	}

	/**
	 * Parses a field value.
	 * @param value The value as a string of the field.
	 * @param f The Field as a variable.
	 * @return a new instance of the Object type specified by the field
	 */
	private static Object parseFieldValue(final String value, final Class<?> clazz) {
		try {
			// INNER OBJECT 
			if (value != null && !value.isEmpty()) {
				if (value.charAt(0) == '{') {
					return toObject(value,  clazz);

					// SOME KIND OF ARRAY
				} else if (value.charAt(0) == '[') {
					Scanner s = new Scanner(value);
					//System.out.println(value);
					String iline = s.next("\\[.*\\]");
					iline = iline.substring(1, iline.length() - 1);
					s.close();

					final int arrMax = countFields(iline);
					Object[] objarr = (Object[]) Array.newInstance(clazz.getComponentType(), arrMax);

					Pattern pattern1 = Pattern.compile(FIELD_REGEXP);
					Matcher matcher = pattern1.matcher(iline);
					//System.out.println("FILLING AN ARRAY WITH SIZE " + arrMax + " OF TYPE " + clazz.getComponentType().getName());
					for (int i = 0; i < arrMax && matcher.find(); i++) {
						String innerValue = matcher.group();
						//System.out.println("\t" + innerValue);

						if (innerValue.length() > 0 && innerValue.charAt(innerValue.length() - 1) == ',') {
							innerValue = innerValue.substring(0, innerValue.length() - 1);
						}

						Object innerObj = parseFieldValue(innerValue, clazz.getComponentType());
						if (innerObj != null) {
							//System.out.println("ADDING TO ARRAY: " + innerObj);
							objarr[i] = innerObj; 
						}
					}

					return objarr;

				} else {
					// SOME KIND OF PRIMATIVE
					// Any kind of String ?
					if (value.charAt(0) == '"') {
						// Cleaning string...
						return value.subSequence(1, value.length() - 1);

					} else {

						if (clazz == int.class) {
							return Integer.parseInt(value);
						} else if (clazz == short.class) {
							return Short.parseShort(value);
						} else if (clazz == byte.class) {
							return Byte.parseByte(value);
						} else if (clazz == long.class) {
							return Long.parseLong(value);
						} else if (clazz == char.class) {
							return value.charAt(0);
						} else if (clazz == float.class) {
							return Float.parseFloat(value);
						} else if (clazz == double.class) {
							return Double.parseDouble(value);
						} else if (clazz == String.class) {
							return value;
						} else {
							return clazz.getConstructor(String.class).newInstance(value);
						}
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Counts the number of fields in a given string.
	 * @param fields The string to look in.
	 * @return The found number of fields.
	 */
	private static int countFields(final String fields) {
		int count = 0;
		Pattern pattern = Pattern.compile(FIELD_REGEXP);
		Matcher matcher = pattern.matcher(fields);

		while (matcher.find()) {
			count++;
		}
		count--;
		return Math.max(0, count);
	}

//	public static void main(final String[] args) throws Exception {
//		TestPerson t = new TestPerson();
//		TestPerson r = new TestPerson();
//
//		r.name = "Ragnar";
//		r.age = 12;
//		t.name = "Hugo";
//		t.age = 10;
//		t.relatives = new TestPerson[1];
//		t.relatives[0] = r;
//		String json = JSONParser.toJSON(t);
//		System.err.println(json);
//		Object obj = JSONParser.toObject(json, TestPerson.class);
//
//		System.out.println(obj.toString());
//
//		String json2 = JSONParser.toJSON(obj);
//
//		System.err.println(json2);
//		Json j = new Json();
////		System.out.println(t.secondNames);
//		System.out.println(j.toJson(t));
//	}
}
