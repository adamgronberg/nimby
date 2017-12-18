package edu.chalmers.matchServer.matchLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.build.BuildEdges;
import edu.chalmers.nimby.controller.MatchConstants;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.factory.PartBuilder;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.gameLogic.ProjectileInfo;
import edu.chalmers.nimby.network.match.MatchNetwork.KeyPress;
import edu.chalmers.nimby.network.match.UniqueIdentifier;

/**
 * Contains all the match logic.
 * @author Adam Grönberg
 *
 */
public final class MatchLogic {
	
	private World world;
	private final Match match;
	
	private static final float TARGET_FPS = 60f;
	private static final float STEP_TIME = 1 / TARGET_FPS;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;

	private final ServerProjectileCreator projectileCreator;
	private final ProjectileRemover projectileRemover;
	private final List<IdentifiableEntity> identifiableEntities;
	
	private BuildEdges boundary;

	/**
	 * Constructor.
	 */
	public MatchLogic(final Match match) {
		identifiableEntities = new LinkedList<IdentifiableEntity>();
		projectileCreator = new ServerProjectileCreator(this);
		projectileRemover = new ProjectileRemover(this);
		this.match = match;
		world = new World(Vector2.Zero, true);
		setWorldLimits(world);
	}
	
	private void setWorldLimits(final World world) {
		boundary = new BuildEdges(world, MatchConstants.BOUNDARY_WIDTH, MatchConstants.BOUNDARY_HEIGHT);
	}
	
	/**
	 * Adds the entities to the update cycle.
	 * @param entities the entities to add to the update cycle
	 */
	public void addIdentifiableEntity(final IdentifiableEntity ... entities) {
		for (IdentifiableEntity entity: entities) {
			if (entity instanceof Equipment) {
				Equipment equipment = (Equipment) entity;
				equipment.setProjectileCreator(projectileCreator);
			} 
			
			IdentifiableEntity idEnt = (IdentifiableEntity) entity;
			identifiableEntities.add(idEnt);
		}
	}
	
	/**
	 * Updates all entities.
	 * @param delta time between calls
	 * @throws InterruptedException 
	 */
	public void update(final float delta) throws InterruptedException {
		
		if (world != null) {
			world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}
		
		projectileCreator.createEntitiesInQueue();
		projectileRemover.removeProjectiles();
		
		Iterator<IdentifiableEntity> itr = identifiableEntities.iterator();
		while (itr.hasNext()) {
			IdentifiableEntity idE = itr.next();
			idE.update(delta, null);
		}
	}
	
	/**
	 * Creates the initialized ships.
	 * @param initializedShips ships to create.
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	public void createShips(final List<byte[]> initializedShips) throws UnsupportedEncodingException, SQLException {
		for (byte[] ship : initializedShips) {
			Part[] parts = PartBuilder.createSpaceShip(ship, world);
			for (Part part: parts) {
				addIdentifiableEntity(part);
			}
		}
	}
	
	/**
	 * Returns the entity with the unique ID.
	 * @param id The ID to find.
	 * @param keyPress the key that was pressed.
	 */
	public void activateIdentifyEntities(final UniqueIdentifier activatedEntity, final KeyPress keyPress, final UniqueIdentifier spawnedEntity) {
		for (IdentifiableEntity entity : identifiableEntities) {
			if (entity.getMatchPartUniqueID().equals(activatedEntity)) {
				if (entity instanceof Equipment) {
					Equipment equipment = (Equipment) entity;
					if (keyPress == KeyPress.UP) {
						equipment.keyUp();
					} else {
						equipment.keyDown();
					}
				}
			}
		}
	}
	
	/**
	 * @return list of all {@link IdentifiableEntity} in the match.
	 */
	public List<IdentifiableEntity> getIdentifiableEntities() {
		return identifiableEntities;
	}
	
	public void putProjectileOnBuildQueue(final Vector2 position, final Vector2 velocity, final UniqueIdentifier uniqueProjectileID) {
		ProjectileInfo projectileInto = new ProjectileInfo(world, position, velocity);
		projectileCreator.addProjectileToQueue(projectileInto, uniqueProjectileID);
	}

	public Match getMatch() {
		return match;
	}
}
