package edu.chalmers.matchServer.matchLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.model.projectile.Projectile;
import edu.chalmers.nimby.network.match.UniqueIdentifier;

public class ProjectileRemover {
	
	private final MatchLogic matchLogic;
	
	public ProjectileRemover(final MatchLogic matchLogic) {
		this.matchLogic = matchLogic;
	}
	
	public final void removeProjectiles() throws InterruptedException {
		List<UniqueIdentifier> entitiesToRemove = new LinkedList<UniqueIdentifier>(); 
		List<IdentifiableEntity> copyOfEntities = new ArrayList<IdentifiableEntity>(matchLogic.getIdentifiableEntities());
		Iterator<IdentifiableEntity> ideItr = copyOfEntities.iterator();
		while (ideItr.hasNext()) {
			IdentifiableEntity identifiableEntity = ideItr.next();
			if (identifiableEntity instanceof Projectile) {
				Projectile projectile = (Projectile) identifiableEntity;
				if (projectile.shouldBeRemoved()) {
					matchLogic.getIdentifiableEntities().remove(projectile);
					entitiesToRemove.add(projectile.getMatchProjectileUniqueID());
				}
			}
		}
		matchLogic.getMatch().removeProjectilesFromClients(entitiesToRemove);
	}
}
