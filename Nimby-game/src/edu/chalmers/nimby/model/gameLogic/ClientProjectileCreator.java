package edu.chalmers.nimby.model.gameLogic;

import edu.chalmers.nimby.assets.WeaponData;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.model.projectile.DamageOnHit;
import edu.chalmers.nimby.model.projectile.Projectile;
import edu.chalmers.nimby.network.match.MatchServerHandle;
import edu.chalmers.nimby.util.Bundle;

/**
 * Creates projectiles from the pipe. Used by the client. 
 * @author Adam Grönberg
 */
public class ClientProjectileCreator extends ProjectileCreator {
	
	private final MatchScreen matchScreen;
	
	public ClientProjectileCreator(final MatchScreen matchScreen) {
		this.matchScreen = matchScreen;
	}

	@Override
	public void createEntitiesInQueue() {
		while (projectilesToCreate.hasNext()) {
			ProjectileCreationInfo projectileToSpawn = projectilesToCreate.pull();
			DamageOnHit damageOnHit = new DamageOnHit(1);				//TODO Should not be 1
			Projectile projectile = new Projectile(projectileToSpawn.projectileInfo.world, 
					projectileToSpawn.projectileInfo.position, 0,
					WeaponData.BLUE_LASER_WIDTH, 
					WeaponData.BLUE_LASER_HEIGHT,
					damageOnHit, WeaponData.BLUE_LASER_TEXTURE);
			projectile.setVelocity(projectileToSpawn.projectileInfo.velocity);
			projectile.generatProjectileUniqueMatchID();
			projectileToSpawn.projectileID = projectile.getMatchProjectileUniqueID();
			
			MatchServerHandle.getInstance().sendProjectileSpawnRequest(projectileToSpawn);
			matchScreen.addBundles(new Bundle(projectile, projectile));
		}
	}
}
