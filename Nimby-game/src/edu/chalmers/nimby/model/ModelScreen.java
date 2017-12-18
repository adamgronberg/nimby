package edu.chalmers.nimby.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.ClientProjectileCreator;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.model.gameLogic.EntityHandeler;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;

/**
 * Contains the model of a screen.
 * @author Victor, Adam
 *
 */
public class ModelScreen {

	private static final float TARGET_FPS = 60f;
	private static final float STEP_TIME = 1 / TARGET_FPS;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;
	
	private final List<Entity> entities;
	private final List<EntityHandeler> entityHandelers;
	private final List<IdentifiableEntity> identifiableEntities;
	private final OrthographicCamera cam;
	private ClientProjectileCreator projectileCreator;
	private Box2DDebugRenderer box2DDebugRenderer;
	private World world;

	/**
	 * Constructor. Initializes class fields.
	 * @param oCam camera used by the screen
	 */
	public ModelScreen(final OrthographicCamera oCam) {
		this.cam = oCam;
		box2DDebugRenderer = new Box2DDebugRenderer();
		entities = new LinkedList<Entity>();
		entityHandelers = new LinkedList<EntityHandeler>();
		identifiableEntities = new LinkedList<IdentifiableEntity>();
	}
	
	public void setClientProjectileCreator(final MatchScreen matchScreen) {
		projectileCreator = new ClientProjectileCreator(matchScreen);
	}

	/**
	 * Sets the world to use.
	 * @param world the world used.
	 */
	public final void setWorld(final World world) {
		this.world = world;
	}
	
	/**
	 * @return the world used
	 */
	public final World getWorld() {
		return world;
	}
	
	/**
	 * Steps the world and updates each entity.
	 * @param delta time since the method was last called
	 */
	public final synchronized void update(final float delta) {

		if (world != null) {
			world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		}
		
		if (projectileCreator != null) {
			projectileCreator.createEntitiesInQueue();
		}
		
		List<EntityHandeler> copyOfHandeler = new ArrayList<EntityHandeler>(entityHandelers);
		Iterator<EntityHandeler> ehItr = copyOfHandeler.iterator();
		while (ehItr.hasNext()) {
			EntityHandeler eh = ehItr.next();
			eh.run();
		}
		
		Iterator<IdentifiableEntity> edItr = identifiableEntities.iterator();
		while (edItr.hasNext()) {
			IdentifiableEntity e = edItr.next();
			e.update(delta, cam);
		}
		
		Iterator<Entity> eItr = entities.iterator();
		while (eItr.hasNext()) {
			Entity e = eItr.next();
			e.update(delta, cam);
		}
		
//		if (world != null) {
//			box2DDebugRenderer.render(world, cam.combined);
//		}
	}

	/**
	 * Adds the entity to the update cycle.
	 * @param entity the entity to add
	 */
	public final synchronized void add(final Entity entity) {
		if (entity instanceof Equipment) {
			Equipment equipment = (Equipment) entity;
			equipment.setProjectileCreator(projectileCreator);
		}
		if (entity instanceof IdentifiableEntity) {
			identifiableEntities.add((IdentifiableEntity) entity);
		} else {
			entities.add(entity);
		}
	}
	
	/**
	 * @return list of all {@link IdentifiableEntity} in the screen.
	 */
	public final synchronized List<IdentifiableEntity> getIdentifiableEntities() {
		return identifiableEntities;
	}
	
	/**
	 * Removes the entity to the update cycle.
	 * @param entity the entity to remove
	 */
	public final synchronized void remove(final Entity entity) {
		if (entity instanceof IdentifiableEntity) {
			identifiableEntities.remove((IdentifiableEntity) entity);
		} else {
			entities.remove(entity);
		}
	}
	
	/**
	 * Adds the {@link EntityHandeler} to update cycle.
	 * @param entityHandeler {@link EntityHandeler} that should be in update cycle.
	 */
	public final synchronized void addEntityHandeler(final EntityHandeler entityHandeler) {
		entityHandelers.add(entityHandeler);
	}
	
	/**
	 * Removes the entity to the update cycle.
	 * @param entityHandeler {@link EntityHandeler} that should be removed from update cycle.
	 */
	public final synchronized void removeEntityHandeler(final EntityHandeler entityHandeler) {
		entityHandelers.remove(entityHandeler);
	}
}
