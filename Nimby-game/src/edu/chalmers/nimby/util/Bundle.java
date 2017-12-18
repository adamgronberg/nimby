package edu.chalmers.nimby.util;

import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.view.Renderable;

/**
 * Bundle that contains the entity and its renderable components.
 * @author Viktor
 *
 */
public class Bundle {
	
	public Entity entity;
	public Renderable renderable;
	
	/**
	 * Bundle that contains the entity and its renderable components.
	 * @param entity the entity part
	 * @param renderable the renderable part
	 */
	public Bundle(final Entity entity, final Renderable renderable) {
		this.entity = entity;
		this.renderable = renderable;
	}
}
