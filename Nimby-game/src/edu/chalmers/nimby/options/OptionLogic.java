package edu.chalmers.nimby.options;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

import edu.chalmers.nimby.avatar.Avatar;
import edu.chalmers.nimby.util.ModeComparator;

/**
 * Contains functions to load and set options from {@link Preferences}.
 * @author Adam
 *
 */
public final class OptionLogic {
	
	private static OptionLogic instance = null;
	
	private Preferences prefs;
	
	private static final String SOUND = "sound", VSYNC = "vSync", FULLSCREEN = "fullScreen", RESOLUTION = "Resolution", AVATAR = "avatar";
	
	private static final int DEFAULT_RES = 5;
	private static final boolean DEFAULT_SOUND = false;
	private static final boolean DEFAULT_VSYNC = false;
	private static final boolean DEFAULT_FULLSCREEN = false;
	private static final boolean DEFAULT_AVATAR = false;
	
	private Music backbroundMusic;
	
	private OptionLogic() { }
	
	public static synchronized OptionLogic getInstance() {
		if (instance == null) {
			instance = new OptionLogic();
		} 
		return instance;
	}
	
	/**
	 * Loads and sets all the options.
	 */
	public void loadAndSetOptions() {
		backbroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/Ace of Space.mp3"));
		prefs = Gdx.app.getPreferences("nimby.options");
		checkSettings();
		setSettings();
	}
	
	/**
	 * Sets all the options.
	 */
	private void setSettings() {
		setSoundToOption();
		setVsyncToOption();
		setAvatarToOption();
		setFullScreenToOption();
		DisplayMode[] modes = Gdx.graphics.getDisplayModes();
		Arrays.sort(modes, new ModeComparator());
		setReselutionToOption(modes);
	}

	/**
	 * Checks if some settings ain't loaded, if so loads them with default values.
	 */
	private void checkSettings() {
		if (!prefs.contains(SOUND)) {
			prefs.putBoolean(SOUND, DEFAULT_SOUND);
		} 
		if (!prefs.contains(VSYNC)) {
			prefs.putBoolean(VSYNC, DEFAULT_VSYNC);
		}
		if (!prefs.contains(FULLSCREEN)) {
			prefs.putBoolean(FULLSCREEN, DEFAULT_FULLSCREEN);
		}
		if (!prefs.contains(AVATAR)) {
			prefs.putBoolean(AVATAR, DEFAULT_AVATAR);
		}
		if (!prefs.contains(RESOLUTION)) {
			prefs.putInteger(RESOLUTION, DEFAULT_RES);
		}
		prefs.flush();
	}

	/**
	 * @return gets the 
	 */
	public boolean getSoundOption() {
		return prefs.getBoolean(SOUND);
	}
	
	public void toggleSoundOption() {
		prefs.putBoolean(SOUND, !getSoundOption());
		prefs.flush();
	}
	
	public boolean getVsyncOption() {
		return prefs.getBoolean(VSYNC);
	}
	
	public void toggleVsyncOption() {
		prefs.putBoolean(VSYNC, !getVsyncOption());
		prefs.flush();
	}
	
	public boolean getFullScreenOption() {
		return prefs.getBoolean(FULLSCREEN);
	}
	
	public void toggleFullScreenOption() {
		prefs.putBoolean(FULLSCREEN, !getFullScreenOption());
		prefs.flush();
	}
	
	public boolean getAvatarOption() {
		return prefs.getBoolean(AVATAR);
	}
	
	public void toggleAvatarOption() {
		prefs.putBoolean(AVATAR, !getAvatarOption());
		prefs.flush();
	}
	
	public int getReselution() {
		return prefs.getInteger(RESOLUTION);
	}
	
	public void setReselution(final int reselution) {
		prefs.putInteger(RESOLUTION, reselution);
		prefs.flush();
	}
	
	public void setAvatarToOption() {
		Avatar.getInstance().setActivated(getAvatarOption());
	}
	
	public void setSoundToOption() {
		if (getSoundOption()) {
			backbroundMusic.play();
		} else {
			backbroundMusic.pause();
		}
	}

	public void setVsyncToOption() {
		Gdx.graphics.setVSync(getVsyncOption());
	}
	
	public void setFullScreenToOption() {
		
	}
	
	public void setReselutionToOption(final DisplayMode[] modes) {
		Gdx.graphics.setDisplayMode(modes[getReselution()].width, modes[getReselution()].height, getFullScreenOption());
	}
}
