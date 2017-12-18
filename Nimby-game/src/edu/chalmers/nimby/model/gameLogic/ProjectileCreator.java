package edu.chalmers.nimby.model.gameLogic;

import edu.chalmers.nimby.network.match.UniqueIdentifier;
import edu.chalmers.nimby.util.Pipe;

/**
 * Used to create projectiles.
 * @author Adam Grönberg
 *
 */
public abstract class ProjectileCreator {
	
	/**
	 * Omg this is so temp...
	 * @author Adam Grönberg
	 *
	 */
	public class ProjectileCreationInfo {
		public ProjectileInfo projectileInfo;
		public UniqueIdentifier projectileID;
	}
	
	protected Pipe<ProjectileCreationInfo> projectilesToCreate;
	
	public ProjectileCreator() {
		projectilesToCreate = new Pipe<ProjectileCreationInfo>();
	}
	
	public abstract void createEntitiesInQueue();

	public void addProjectileToQueue(final ProjectileInfo projectileToSpawn, final UniqueIdentifier projectileID) {
		ProjectileCreationInfo projectileToCreate = new ProjectileCreationInfo();
		projectileToCreate.projectileID = projectileID;
		projectileToCreate.projectileInfo = projectileToSpawn;
		projectilesToCreate.put(projectileToCreate);
	}
}
