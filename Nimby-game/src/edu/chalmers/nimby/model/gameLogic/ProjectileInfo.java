package edu.chalmers.nimby.model.gameLogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * COntains information about a projectile to spawn.
 * @author Adam Gtönberg
 *
 */
public class ProjectileInfo {
	public final World world;
	public final Vector2 position;
	public final Vector2 velocity;
	
	public ProjectileInfo(final World world, final Vector2 position, final Vector2 velocity) {
		this.world = world;
		this.position = position;
		this.velocity = velocity;
	}
}
