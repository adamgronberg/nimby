package edu.chalmers.nimby.avatar;

/**
 * 
 * Will parse the string which will be sent from the avatar, containg the words 
 * it interpreted.
 * 
 * @author Gustav Dahl
 *
 */
public final class LanguageParser {

	/**
	 * This is a utility class, cannot be created.
	 */
	private LanguageParser() { }

	/**
	 * 
	 * @param interpretedString The string which is return from voce
	 */
	public static void languageInterpreter(final String interpretedString) {
		String[] inputAnalysis = interpretedString.split(" ");
		int counter = 0;

		//TODO one option should link to another method with more options
		switch (inputAnalysis[counter]) {
		case "FORWARD":
			break;
		case "LEFT":
			break;
		case "RIGHT":
			break;
		default:
			break;
		}
		//		case "one":
		//			System.out.println("first word: one");
		//			break;
		//		case "two":
		//			System.out.println("first word: two");
		//			break;
		//		case "three":
		//			System.out.println("first word: three");
		//			break;
		//		case "four":
		//			System.out.println("first word: four");
		//			break;
		//		case "five":
		//			System.out.println("first word: five");
		//			break;
		//		case "six":
		//			System.out.println("first word: six");
		//			break;
		//		case "seven":
		//			System.out.println("first word:seven");
		//			break;
		//		case "eight":
		//			System.out.println("You said: eight");
		//			break;
		//		case "nine":
		//			System.out.println("You said: nine");
		//			break;
		//		case "oh":
		//			System.out.println("You said: oh");
		//			break;
		//		default:
		//			break;
		//		}
	}

}
