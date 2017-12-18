package edu.chalmers.nimby.model.equipment.skill;

import java.util.LinkedList;
import java.util.List;

import edu.chalmers.nimby.model.equipment.Equipment;

/**
 * Composite container for {@link Skill}.
 * @author Viktor Sjölind
 *
 */
public class SkillContainer implements Skill {

	private List<Skill> skills;
	
	/**
	 * Creates a new SkillContainer with a linked data structure.
	 */
	public SkillContainer() {
		skills = new LinkedList<>();
	}
	
	/**
	 * Adds a {@link Skill}kill to the container.
	 * @param skill The {@link Skill} to add.
	 */
	public final void add(final Skill skill) {
		skills.add(skill);
	}
	
	@Override
	public final void invoke(final Equipment equipment) {
		for (Skill skill : skills) {
			skill.invoke(equipment);
		}
	}
}
