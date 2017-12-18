package edu.chalmers.nimby.model.gameLogic;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;

import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.model.SensorData;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.util.PartsUtil;

/**
 * Describes the hull of a ship. The ShipPart can be attached to other parts
 * such as the Equipment it is containing, and other ShipParts that is connected
 * to its sides.
 * 
 * @author Viktor Sjölind, Adam Gr�nberg
 * 
 */
public class ShipPart extends Part {

//	private static final int ATTACHMENTNODE_DEBUG_RADIUS = 10;

	private static final float START_ANGLE = 0;

	private boolean root;
	private Part parent;
	private final List<Part> children;

	private List<RevoluteJoint> joints;

	/**
	 * Creates a ShipPart with a collision {@link Body} the size of the provided
	 * {@link Texture}, and with the provided attachmentNodes.
	 * 
	 * @param world The world to create the body in.
	 * @param relativePos The position to set the position to, if null (0, 0) will be used.
	 * @param width width of the part.
	 * @param height height of the part.
	 * @param mass mass of the part.
	 * @param partName the name of the part.
	 * @param texturePath The internal path of the texture.
	 */
	public ShipPart(final World world, final Vector2 relativePos, final float width, 
			final float height, final float mass, final String partName , final String texturePath) {
		super(world, relativePos, START_ANGLE, width, height, mass, partName, texturePath);
		children = new LinkedList<>();
		initShipPart();
	}

	/**
	 * Sets root parent and joints.
	 */
	private void initShipPart() {
		setRoot(true);
		setParent(null);
		joints = new LinkedList<>();
	}

	/**
	 * Welds this ShipPart together with a Part, for example another ShipPart or
	 * a piece of {@link Equipment}. After the weld the two parts keep the same
	 * distance and relative rotation as they did prior to the weld.
	 * 
	 * @param world The world in which the ship exist.
	 * @param otherPart The {@link ShipPart} to weld together with this ShipPart.
	 */
	public  final void weld(final World world, final ShipPart otherPart) {
		boolean weld = true;

		ShipPart thisRoot = PartsUtil.getRoot(this);
		ShipPart otherRoot = PartsUtil.getRoot(otherPart);
		if (thisRoot.equals(otherRoot)) {
			weld = false;
			setParent(otherPart);
		}

		if (weld) {
			Body oldBody = otherPart.body;
			for (Fixture otherFixture : oldBody.getFixtureList()) {
				if (getFixture().getUserData() instanceof SensorData) {
					SensorData sensorData = (SensorData) getFixture().getUserData();
					BuildPart bPart = (BuildPart) sensorData.getBuildPart();
					bPart.getSensors().clear();
				}
				PartsUtil.copyFixtureToBody(otherFixture, this.body);
			}
			world.destroyBody(oldBody);
			
			if (this.root) {
				this.root = false;
				setParent(otherPart);
			} else {
				ShipPart root = PartsUtil.getRoot(this);
				root.root = false;
				// just find a root, if not it will cause exception
				PartsUtil.getRoot(otherPart);
				root.setParent(otherPart);
			}
		}

		// if we are welding to another part we will never we root and the otherpart will always be our parent
		// when needing to find the root we simply move through the parent til we find our root
	}

	@Override public final void update(final float delta, final OrthographicCamera cam) { }

	@Override
	public final void render(final SpriteBatch batch, final BitmapFont font) {
		Vector2 p = getPosition();

		sprite.setRotation(getAngle());
		sprite.setPosition(p.x - halfWidth, p.y - halfHeight);

		sprite.draw(batch);

		// DEBUGGING
//		font.draw(batch, getAngle() + " " + internalAngle, p.x + 10, p.y);
//		font.draw(batch, getShipLocalUniqueID() + " ", p.x, p.y + 100);
//		font.draw(batch, "Hp: " + getHp(), p.x, p.y + 130);
		
	}

	@Override
	public final void debug(final ShapeRenderer sr) {
		Vector2 p = getPosition();
		sr.setColor(Color.RED);

		sr.translate(p.x, p.y, 0);
		sr.rotate(0, 0, 1, getAngle());

//		sr.circle(0, 0, ATTACHMENTNODE_DEBUG_RADIUS);
//		sr.rect(-halfWidth, -halfHeight, width, height);

		sr.rotate(0, 0, 1, -getAngle());
		sr.translate(-p.x, -p.y, 0);

//		sr.line(body.getPosition().cpy().scl(BodyConstants.BOX_TO_WORLD), getRelativePos().cpy().add(body.getPosition()).scl(BodyConstants.BOX_TO_WORLD));
	}


	/**
	 * @return the root
	 */
	public final boolean isRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public final void setRoot(final boolean root) {
		this.root = root;
	}

	/**
	 * @return the parent
	 */
	public final Part getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public final void setParent(final ShipPart parent) {
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	/**
	 * @return the children
	 */
	public final List<Part> getChildren() {
		return children;
	}

	/**
	 * 
	 * @return List of revoluteJoints.
	 */
	public final List<RevoluteJoint> getJoints() {
		return joints;
	}
}
