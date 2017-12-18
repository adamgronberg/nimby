package edu.chalmers.nimby.model.gameLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.chalmers.nimby.controller.AbstractControllerScreen;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.model.projectile.Projectile;
import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Bundle;

/**
 * Removes projectiles from the match.
 * @author Adam Grönberg 	OMG TEMP
 *
 */
public class ProjectileRemover extends EntityHandeler {

	List<UniqueIdentifier> idsofProjectilesToRemove;
	
	public ProjectileRemover(final MatchScreen screen, final List<UniqueIdentifier> idsofProjectilesToRemove) {
		super(screen);
		this.idsofProjectilesToRemove = idsofProjectilesToRemove;
	}

	@Override
	public void command() {
		List<IdentifiableEntity> entities = screen.getIdentifiableEntities();
		List<IdentifiableEntity> copyOfEntities = new ArrayList<IdentifiableEntity>(entities);
		Iterator<IdentifiableEntity> ideItr = copyOfEntities.iterator();
		while (ideItr.hasNext()) {
			IdentifiableEntity entity = (IdentifiableEntity) ideItr.next();
			if (entity instanceof Projectile) {
				Projectile projectile = (Projectile) entity;
				for (UniqueIdentifier projectilesToRemove: idsofProjectilesToRemove) {
					if (entity.getMatchProjectileUniqueID().equals(projectilesToRemove)) {
						screen.removeBundles(new Bundle(projectile, projectile));
					}
				}
			}
		}
	}

}
