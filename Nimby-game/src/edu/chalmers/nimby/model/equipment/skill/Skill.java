package edu.chalmers.nimby.model.equipment.skill;

import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Interface for a skill to be run by equipment such as:
 * * Fire projectile.
 * * Apply force to body.
 * @author Viktor Sjölind
 *
 */
public interface Skill {

	/**
	 * Executes the skill command.
	 * @param equipment the {@link Equipment} executing the skill.
	 */
	void invoke(final Equipment equipment);
}
