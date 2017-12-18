package edu.chalmers.nimby.assets;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;

/**
 * Temp! Stores ship images. Loads a part if it hasn't been loaded before.
 * @author Adam Grönberg
 *
 */
public final class ImagesLoader {

	private HashMap<String, Texture> textureMap;
	private static ImagesLoader instance = null;
	
	/**
	 * Constructor. Singelton.
	 */
	private ImagesLoader() { 
		textureMap = new HashMap<String, Texture>();
	}
	
	/**
	 * @return gets the ShipImage generator.
	 */
	public static ImagesLoader getInstance() {
		if (instance == null) {
			instance = new ImagesLoader();
		}
		return instance;
	}
	
	/**
	 * Gets the texture requested. Tries to load it if it hasent been done before.
	 * @param texturePath the path to the texture
	 * @return the loaded texture
	 */
	public Texture getTexture(final String texturePath) {
		if (!textureMap.containsKey(texturePath)) {
			textureMap.put(texturePath, new Texture(texturePath));
		}
		return textureMap.get(texturePath);
	}
	
	
}
