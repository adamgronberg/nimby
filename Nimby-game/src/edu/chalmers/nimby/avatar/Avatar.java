package edu.chalmers.nimby.avatar;

import voce.SpeechInterface;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.Voice;

import com.badlogic.gdx.graphics.Texture;


/**
 * A helper to the user, which will use text-to-speech and understand speech-to-text.
 * If a specific key in the gamescreens, the avatar should be ready to handle input
 * If something happens in the game which the user would be interested about, it should notify the user
 * @author Gustav Dahl
 */
public final class Avatar implements Runnable {

	private static final String TEXT2VOICE = "text2voice";
	private static final String VOICE2TEXT = "voice2text";
	private static final int TIMETOSLEEP = 200;
	private static final float VOICE_PACE = 120;
	
	private static Texture avatarTexture = null;
	private static Avatar instance = null;
	private static Voice avatarVoice = null;

	private String cmd;
	private String text2say;

	private boolean activated;
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

	/**
	 *  return the which method will be used when creating a new thread.
	 * @return String with the method name
	 */
	public String getCmd() {
		return cmd;
	}
	
	public void setText(final String text2say) {
		this.text2say = text2say;
	}
	
	/**
	 * Sets which method to use when starting a new thread.
	 * @param cmd The method Name
	 */
	public void setCmd(final String cmd) {
		this.cmd = cmd;
	}

	/**
	 * Constructor.
	 */
	private Avatar() { }
	
	/**
	 * Iniates the avatar with required data.
	 */
	public void init() {
		avatarVoice = iniateVoice();
		//Should iniates the texture of sprite animation also
	}
	
	/**
	 * Returns an instance of this class if it has been initalized.
	 * @return The instance of this class
	 */
	public static synchronized Avatar getInstance() {
		if (instance == null) {
			instance = new Avatar();
		}
		return instance;
	}

	/**
	 * Makes the Avatar say the text which comes from input.
	 * @param text2Say is a string which the avatar is supposed to say.
	 */
	public void avatarText2Speech(final String text2Say) {
		
		//Voice avatarVoice = iniateVoice();
		System.out.println("avatar is supposed to say something");
		avatarVoice.allocate();
		avatarVoice.setRate(VOICE_PACE);
		avatarVoice.speak(text2Say);
		//avatarVoice.deallocate();    //tried to comment this out
	}
	
	/**
	 * Iniates the avatars voice.
	 * @return the name of the avatars voice
	 */
	private static Voice iniateVoice() {
		String voiceName = null;
		Voice avatarVoice = null;
		try {
			Voice[] voices = listAllVoices();
			voiceName = voices[0].getName();
		
			VoiceManager manager = VoiceManager.getInstance();
			avatarVoice = manager.getVoice(voiceName);
			System.out.println(voiceName + " was the voiced choosen \n" + "the age is: " + avatarVoice.getAge());
			
		} catch (Exception e) {
			System.out.println("Could not iniate the avatar voice");
		}
		return avatarVoice;
	}
	
	/**
	 * Will list all the available voices.
	 * @return an array containg all avaible voices
	 */
	private static Voice[] listAllVoices() {
		VoiceManager manager = VoiceManager.getInstance();
		Voice[] voices = manager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			System.out.println("  " + voices[i].getName() 
					+ "( " + voices[i].getDomain() + "domain)");
		}
		return voices;
	}

	/**
	 * Will convert speech as input from the use to text.
	 */
	public void avatarSpeech2Text() {
		SpeechInterface.init("bin/voce", false, true, "bin/voce/gram", "commands");	
		boolean quit = false;
		while (!quit) {
			try {
				Thread.sleep(TIMETOSLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (SpeechInterface.getRecognizerQueueSize() > 0) {
				System.out.println("There is something to print");
				String s = voce.SpeechInterface.popRecognizedString();

				// Check if the string contains 'quit'.
				if (-1 != s.indexOf("quit")) {
					quit = true;
				}
				System.out.println("You said: " + s);
				LanguageParser.languageInterpreter(s);
			}
		}
		SpeechInterface.destroy();
		System.exit(0);		
	}

	/**
	 * Returns the avatars texture.
	 * @return The avatars texture
	 */
	public Texture getAvatarTexture() {
		return avatarTexture;
	}
	
	/**
	 * Sets the texture of the avatar. 
	 * @param avatarTexture Texture which will be the texture for the avatar
	 */
	public void setAvatarTexture(final Texture avatarTexture) {
		Avatar.avatarTexture = avatarTexture;
	}
	
	/**
	 * A method that will be used for running the thread.
	 */
	@Override
	public void run() {
		if ((cmd.equals(VOICE2TEXT)) && activated) {
			System.out.println("AVATAR: starting voice2text");
			avatarSpeech2Text();
		} else if ((cmd.equals(TEXT2VOICE)) && activated) {
			System.out.println("AVATAR: starting text2voice");
			avatarText2Speech(text2say);
		}
	}
}
