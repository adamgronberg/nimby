package edu.chalmers.nimby.model.equipment;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import edu.chalmers.nimby.assets.ShipData;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.model.SensorData;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.gameLogic.ProjectileCreator;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.util.KeyCodeToAscii;

/**
 * The base of all equipment such as Engines, Weapons etc...
 * 
 * @author Viktor Sjölind
 * 
 */
public class Equipment extends Part {

	private static final float COOLDOWN_ZERO = 0.1f;
	private final float cooldownMax;
	private final boolean usesCooldown;
	private final String skillCode;
	private final EquipmentSkillHandler skillHandler;
	private float cooldownValue;
	private int key;
	private RevoluteJoint revJoint;
	private SensorData secondData;
	private SensorData firstData;
	private Vector2 jointRelativePos;
	private ProjectileCreator projectileCreator;
	
	/**
	 * @param world The world in which the Equipment is in.
	 * @param angle 
	 * @param position The position of the Equipment in the world (pixels).
	 * @param width width of the part.
	 * @param height height of the part.
	 * @param mass mass of the part.
	 * @param partName the name of the part.
	 * @param texturePath The texture path to use.
	 * @param cooldown The time in seconds before the Equipment can be activated again after being activated. If 0 cooldown will not be used.
	 * @param skillCode The skillCode to use.
	 * @param key The key that activates the part.
	 */
	public Equipment(final World world, final Vector2 position, final float angle, final float width, 
			final float height, final float mass, final String partName, final String texturePath, 
			final float cooldown, final String skillCode, final int key) {
		super(world, position, angle, width, height, mass, partName, texturePath);
		if (cooldown < 0) {
			throw new IllegalArgumentException("Cooldown can not be negative!");
		}
		this.skillCode = skillCode;
		this.cooldownMax = cooldown;
		this.cooldownValue = COOLDOWN_ZERO;
		this.key = key;

		usesCooldown = cooldown > 0;

		this.skillHandler = new EquipmentSkillHandler(this);
		SkillCodeParser.parseSkillCode(skillCode, skillHandler);
		revJoint = null;
		jointRelativePos = Vector2.Zero.cpy();
	}

	@Override
	public final void update(final float delta, final OrthographicCamera cam) {
		//System.out.println("Vel: " + body.getLinearVelocity() + " Pos: " + body.getPosition());
		if (usesCooldown && cooldownValue > 0) {
			cooldownValue -= delta;
		} 
	}

	/**
	 * Checks if the Equipment is cooled down. 
	 * Always returns true if the Equipment does not use cooldown.
	 * @return True / false.
	 */
	public final boolean isCooledDown() {
		return !usesCooldown || cooldownValue <= COOLDOWN_ZERO;
	}

	/**
	 * Create a revolute joint.
	 * @param otherPart other part.
	 * @param weldPointRelativePos The relative weldpoint of the other part
	 * @param limit should the angle be limited?
	 * @param lowerAngle lower angle
	 * @param upperAngle upper angle
	 * @param maxMotorTorque 
	 */
	public final void createRevoluteJoint(final ShipPart otherPart, final Vector2 weldPointRelativePos) {
		jointRelativePos = weldPointRelativePos.cpy();
		RevoluteJointDef rDef = new RevoluteJointDef();
		rDef.bodyA = body;
		rDef.bodyB = otherPart.body;
		rDef.collideConnected = false;
		rDef.localAnchorA.set(Vector2.Zero.cpy());
		rDef.referenceAngle = body.getAngle();
		rDef.localAnchorB.set(otherPart.getRelativePos().cpy().add(getJointRelativePos()));
		revJoint = (RevoluteJoint) getWorld().createJoint(rDef);

		revJoint.setUserData(this);
		revJoint.enableLimit(true);
		otherPart.getJoints().add(revJoint);
	}

	/**
	 * Destroys a joint and removes it from otherDatas list.
	 */
	public final void destroyRevoluteJoint() {
		if (revJoint != null) {
			//System.out.println("Destroy");
			if (revJoint.getBodyB().getUserData() instanceof BuildPart) {
				BuildPart b = (BuildPart) revJoint.getBodyB().getUserData();
				if (b.getTrueUserData() instanceof ShipData) {
					((ShipPart) b.getTrueUserData()).getJoints().remove(revJoint);
				} else {
					throw new IllegalStateException("You've managed to create a revolute joint with two equipments?");
				}
			}
			revJoint = null;
			if (secondData != null && firstData != null) {
				secondData.setOccupied(false);
				firstData.setOccupied(false);
			}
		}
	}

	@Override
	public final void render(final SpriteBatch batch, final BitmapFont font) {
		Vector2 p = getPosition();

		sprite.setRotation(getAngle());
		sprite.setPosition(p.x - halfWidth, p.y - halfHeight);

		sprite.draw(batch);
	
//		font.draw(batch, body.getAngle() * MathUtils.radiansToDegrees + "", p.x, p.y + 20);
	}

	/**
	 * Maxes the cooldown to the max value.
	 */
	public final void maxCooldown() {
		cooldownValue = cooldownMax;
	}

	/**
	 * Resets the cooldown to 0.
	 */
	public final void resetCooldown() {
		cooldownValue = 0;
	}

	/**
	 * Invoke keyUp skills.
	 */
	public final void keyUp() {
		skillHandler.keyUp();
	}

	/**
	 * Invoke keyDown skills.
	 */
	public final void keyDown() {
		skillHandler.keyDown();
	}

	/**
	 * @return the key
	 */
	public final int getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public final void setKey(final int key) {
		this.key = key;
	}

	@Override public void debug(final ShapeRenderer sr) { }

	/**
	 * Used to set which sensor are occupied, not relevant for other than build mode
	 * This only works since one equipment can only hold to one shippart.
	 * @param thisData this parts sensor 
	 * @param otherData other parts sensor
	 */
	public final void setOccupiedSensors(final SensorData thisData, final SensorData otherData) {
		this.firstData = thisData;
		this.secondData = otherData;
		firstData.setOccupied(true);
		secondData.setOccupied(true);
	}

	/**
	 * @return the jointRelativePos
	 */
	public final Vector2 getJointRelativePos() {
		return jointRelativePos;
	}

	/**
	 * @return the revJoint
	 */
	public final RevoluteJoint getRevJoint() {
		return revJoint;
	}

	public String getSkillCode() {
		return skillCode;
	}

	public float getCooldownMax() {
		return cooldownMax;
	}

	public ProjectileCreator getProjectileCreator() {
		return projectileCreator;
	}

	public void setProjectileCreator(final ProjectileCreator projectileCreator) {
		this.projectileCreator = projectileCreator;
	}
}
