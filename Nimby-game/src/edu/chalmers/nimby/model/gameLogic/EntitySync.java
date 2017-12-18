package edu.chalmers.nimby.model.gameLogic;

import java.util.List;

import edu.chalmers.nimby.assets.WeaponData;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.model.projectile.DamageOnHit;
import edu.chalmers.nimby.model.projectile.Projectile;
import edu.chalmers.nimby.network.match.EntitySnapshot;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Bundle;


/**
 * Synchs the client to the matchServer.
 * @author Adam Gr�nberg
 */
public final class EntitySync extends EntityHandeler {

	private final List<EntitySnapshot> entitySnapshotList;

	/**
	 * Constructor.
	 * @param entitySnapshotList The entities to synch.
	 * @param matchScreen the screen the {@link EntityHandeler} is active on.
	 */
	public EntitySync(final List<EntitySnapshot> entitySnapshotList, final MatchScreen matchScreen) {
		super(matchScreen);
		this.entitySnapshotList = entitySnapshotList;
	}

	@Override
	public void command() {
		MatchScreen matchScreen = (MatchScreen) screen;
		List<IdentifiableEntity> entityList = matchScreen.getIdentifiableEntities();
		for (EntitySnapshot entitySnap: entitySnapshotList) {
			entitySnap.unserialize();
			boolean entityExists = false;
			for (IdentifiableEntity idEntity: entityList) {
				if (idEntity.getMatchPartUniqueID().entityID == entitySnap.partMatchUniqueID) {
					if (idEntity instanceof Part) {
						Part part = (Part) idEntity;
						part.body.setLinearVelocity(entitySnap.velocity);
						part.body.setTransform(entitySnap.pos, entitySnap.angle);
						entityExists = true;
					}
				} else if (idEntity.getMatchProjectileUniqueID().userID == entitySnap.playerMatchUniqueID 
						&& idEntity.getMatchProjectileUniqueID().entityID == entitySnap.projectileMatchUniqueID) {
					Projectile projectile = (Projectile) idEntity;
					projectile.body.setLinearVelocity(entitySnap.velocity);
					projectile.body.setTransform(entitySnap.pos, entitySnap.angle);
					entityExists = true;
				}
			}
			if (!entityExists) {
				DamageOnHit damageOnHit = new DamageOnHit(1);				//TODO Should not be 1
				Projectile projectile = new Projectile(matchScreen.getModelScreen().getWorld(), 
						null, 0,
						WeaponData.BLUE_LASER_WIDTH, 
						WeaponData.BLUE_LASER_HEIGHT,
						damageOnHit, WeaponData.BLUE_LASER_TEXTURE);
				projectile.body.setTransform(entitySnap.pos, entitySnap.angle);
				projectile.body.setLinearVelocity(entitySnap.velocity);
				projectile.setUniqueProjectileMatchID(new UniqueIdentifier(entitySnap.playerMatchUniqueID, entitySnap.projectileMatchUniqueID));
				matchScreen.addBundles(new Bundle(projectile, projectile));
			}
		}
	}
}
