package edu.chalmers.matchServer.matchLogic;

import edu.chalmers.nimby.assets.WeaponData;
import edu.chalmers.nimby.model.gameLogic.ProjectileCreator;
import edu.chalmers.nimby.model.projectile.DamageOnHit;
import edu.chalmers.nimby.model.projectile.Projectile;

/**
 * Creates projectiles from the pipe. Used by the server. 
 * @author Adam Grönberg
 */
public class ServerProjectileCreator extends ProjectileCreator {

	private final MatchLogic matchLogic;
	
	public ServerProjectileCreator(final MatchLogic matchLogic) {
		this.matchLogic = matchLogic;
	}
	
	@Override
	public void createEntitiesInQueue() {
		while (projectilesToCreate.hasNext()) {
			ProjectileCreationInfo projectileToSpawn = projectilesToCreate.pull();
			DamageOnHit damageOnHit = new DamageOnHit(1);									//TODO Should not be 1
			Projectile projectile = new Projectile(projectileToSpawn.projectileInfo.world, 
					projectileToSpawn.projectileInfo.position, 0,
					WeaponData.BLUE_LASER_WIDTH, 
					WeaponData.BLUE_LASER_HEIGHT,
					damageOnHit, WeaponData.BLUE_LASER_TEXTURE);
			projectile.setVelocity(projectileToSpawn.projectileInfo.velocity);
			projectile.setUniqueProjectileMatchID(projectileToSpawn.projectileID);
			matchLogic.addIdentifiableEntity(projectile);
		}
	}
}
