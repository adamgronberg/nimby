package edu.chalmers.nimby.model.equipment.skill;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.ProjectileInfo;

/**
 * Skill that spawns projectiles.
 * @author Adam Gr�nberg
 */
public class SpawnProjectile implements Skill {
	private final float value;
	private static boolean shouldSpawnProjectiles = true;
	
	public SpawnProjectile(final float projectileName) {
		this.value = projectileName;
	}
	
	@Override
	public void invoke(final Equipment equipment) {
		if (shouldSpawnProjectiles) {
			Vector2 velocity = new Vector2(0, value);
			velocity.rotate(equipment.body.getAngle() * MathUtils.radiansToDegrees);
			ProjectileInfo projectileToSpawn = new ProjectileInfo(equipment.getWorld(), equipment.body.getPosition().cpy().scl(BodyConstants.BOX_TO_WORLD), velocity);
			equipment.getProjectileCreator().addProjectileToQueue(projectileToSpawn, null);
		}
	}
	
	public static void setShouldSpawnProjectiles(final boolean shouldSpawnProjectiles) {
		SpawnProjectile.shouldSpawnProjectiles = shouldSpawnProjectiles;
	}
}
