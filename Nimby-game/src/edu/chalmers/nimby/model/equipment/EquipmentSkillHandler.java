package edu.chalmers.nimby.model.equipment;

import edu.chalmers.nimby.model.equipment.skill.Skill;
import edu.chalmers.nimby.model.equipment.skill.SkillContainer;

/**
 * A skill handler for {@link Equipment} that manages manages skills in two groups,
 * on key up and on key down.
 * @author Viktor Sjölind
 *
 */
public class EquipmentSkillHandler {

	private SkillContainer onUp;
	private SkillContainer onDown;
	private Equipment equipment;
	
	/**
	 * An EquipmentSkillHandler that handles up and down presses.
	 * @param equipment The {@link Equipment} to handle the input for.
	 */
	public EquipmentSkillHandler(final Equipment equipment) {
		this.equipment = equipment;
		onUp = new SkillContainer();
		onDown = new SkillContainer();
	}
	
	/**
	 * Invokes the skills that should trigger on key up.
	 */
	public final void keyUp() {
		onUp.invoke(equipment);
	}
	
	/**
	 * Invokes the skills that should trigger on key down.
	 */
	public final void keyDown() {
		onDown.invoke(equipment);
	}

	/**
	 * Adds a {@link Skill} to the list of skills run at keyUp().
	 * @param skill The {@link Skill} to add.
	 */
	public final void addOnUp(final Skill skill) {
		onUp.add(skill);
	}

	/**
	 * Adds a {@link Skill} to the list of skills run at keyDown().
	 * @param skill The {@link Skill} to add.
	 */
	public final void addOnDown(final Skill skill) {
		onDown.add(skill);
	}
}
