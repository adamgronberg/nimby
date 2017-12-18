package edu.chalmers.nimby.build;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.SensorData;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.util.KeyCodeToAscii;
import edu.chalmers.nimby.util.NimbyMath;
import edu.chalmers.nimby.util.PipeIn;

/**
 * Represent a build part for the game.
 * @author Mikael Stolpe
 *
 */
public class BuildPart extends Part {
	private static final float MOUSE_MARKER_RADIUS = 3;
	private final Part part;
	private boolean rotate;
	private Float newAngle;
	private boolean isHeld;
	private boolean positionable;
	private Float otherBodyAngle;
	private SensorData otherData;
	private SensorData thisData;
	private Vector2 otherPos;
	private OrthographicCamera ocam;
	private BuildPart otherPart;

	private Object trueUserData;
	private boolean revolute;
	private boolean unWedable;
	private boolean hovered;
	private List<Fixture> sensors;
	private HashMap<String, List<BuildPart>> put;
	private boolean welding;

	private PipeIn<HashMap<String, List<BuildPart>>> pipe;

	/**
	 * @param decoratedPart The part we which to decorate.
	 * @param pipeIn The input {@link PipeIn} used by the buildPart.
	 */
	public BuildPart(final Part decoratedPart, final PipeIn<HashMap<String, List<BuildPart>>> pipeIn) {
		this.part = decoratedPart;
		this.pipe = pipeIn;
		this.part.generateShipLocalUniqueID();
		this.put = null;
		// WHEN IMPLEMENTING SAVE THIS SHOULD BE SAVED AS SHIPPART
		this.setTrueUserData(part.body.getUserData());
		sensors = new LinkedList<>();
		part.body.setUserData(this);


		Filter filter;
		boolean equipment = decoratedPart instanceof Equipment;
		for (Fixture f : part.body.getFixtureList()) {
			filter = f.getFilterData();
			if (equipment) {
				filter.categoryBits = (short) BuildColliderFilter.PART_SHIP.getColDef();
				filter.maskBits = (short) (BuildColliderFilter.BOUNDARY.getColDef() | BuildColliderFilter.PART_SHIP.getColDef());
			} else {
				filter.categoryBits = (short) BuildColliderFilter.PART_EQUIPMENT.getColDef();
				filter.maskBits = (short) (BuildColliderFilter.BOUNDARY.getColDef() | BuildColliderFilter.PART_EQUIPMENT.getColDef());
			}

			f.setFilterData(filter);
		}
	}

	/**
	 * @deprecated
	 * Creates a ship sensor at 45 degree angle.
	 * @param relativePos pos relative to origo
	 * @param r radius of circle
	 */
	public final void createShipSensor(final Vector2 relativePos, final float r) {
		FixtureDef def2 = new FixtureDef();
		def2.isSensor = true;
		CircleShape circle = new CircleShape();
		circle.setRadius(r);
		def2.filter.categoryBits = (short) BuildColliderFilter.NODE_SHIP.getColDef();

		// should add another for equipment
		def2.filter.maskBits = (short) (BuildColliderFilter.NODE_SHIP.getColDef() | BuildColliderFilter.BOUNDARY.getColDef());
		def2.shape = circle;
		circle.setPosition(relativePos);

		Fixture f = part.body.createFixture(def2);

		float relativeAngle = MathUtils.atan2(relativePos.y, relativePos.x); 
		f.setUserData(new SensorData(relativeAngle + (MathUtils.PI / 2), relativePos, this));

	}

	/**
	 * Creates a ship sensor and saves all relevant data as an SensorData in UserData of said sensor. Sensor will be placed between v1 and v2.
	 * @param v1 First vector
	 * @param v2 Second vector
	 * @param r The radian of the sensor
	 * @param filter the {@link Filter} used by the sensor.
	 */
	public final void createShipSensor2(final Vector2 v1, final Vector2 v2, final float r, final Filter filter) {
		FixtureDef fdef = new FixtureDef();
		fdef.isSensor = true;
		CircleShape circle = new CircleShape();
		circle.setRadius(r);

		fdef.filter.categoryBits = filter.categoryBits;

		// should add another for equipment
		fdef.filter.maskBits = filter.maskBits;
		fdef.shape = circle;

		// create the middle vector
		Vector2 middleVector = v2.cpy().add(v1.cpy());
		circle.setPosition(middleVector.scl(BuildConstants.HALF_DIVIDER));
		Fixture f = part.body.createFixture(fdef);

		//calculate the relative angle of the middlevector from the bodies middle
		float relativeAngle = MathUtils.atan2(middleVector.y,  middleVector.x); 

		// Create the sensorData which will hold all necessary data
		SensorData sens = new SensorData(relativeAngle, middleVector, this);
		//calculate and set the linear disposition of the two vectors
		Vector2 line = v1.cpy().lerp(v2, 1);
		sens.setLinearPoleration(line);
		f.setUserData(sens);
		sensors.add(f);
	}

	@Override
	public final void render(final SpriteBatch batch, final BitmapFont font) {
		part.render(batch, font);
		if (this.getTrueUserData() instanceof Equipment) {
			Equipment e = (Equipment) this.getTrueUserData();
			Vector2 p = e.getPosition();
			font.draw(batch, "" + KeyCodeToAscii.keycodeNames.get(e.getKey()), p.x, p.y);
		}
	}

	@Override
	public final void debug(final ShapeRenderer shapeRenderer) {
		part.debug(shapeRenderer);
		final Vector2 p = getPosition();
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.translate(p.x, p.y, 0);
		shapeRenderer.rotate(0, 0, 1, getAngle());

		//		shapeRenderer.rect(-halfWidth, -halfHeight, width, height);

		for (Fixture fixture : sensors) {
			if (fixture.getUserData() instanceof SensorData) {
				SensorData sensorData = (SensorData) fixture.getUserData();
				//					shapeRenderer.line(new Vector2(0, 0), sensorData.getRelativePos());
				if (!sensorData.isOccupied()) {
					if (true) {
						if (fixture.getShape() instanceof CircleShape) {
							if (fixture.getFilterData().categoryBits == BuildColliderFilter.NODE_EQUIPMENT.getColDef()) {
								shapeRenderer.setColor(Color.ORANGE);

								//								CircleShape circleShape = (CircleShape) fixture.getShape();
								//								Vector2 renderPosition = circleShape.getPosition().cpy();
								//								renderPosition.scl(BodyConstants.BOX_TO_WORLD);
								//								shapeRenderer.circle(renderPosition.x, renderPosition.y, circleShape.getRadius() * BodyConstants.BOX_TO_WORLD);


							} else if (fixture.getFilterData().categoryBits == BuildColliderFilter.NODE_SHIP.getColDef()) {
								shapeRenderer.setColor(Color.CYAN);

							}

							// THE ACTUAL RENDERING OF THE SENSOR
							CircleShape circleShape = (CircleShape) fixture.getShape();
							Vector2 renderPosition = circleShape.getPosition().cpy();
							renderPosition.sub(part.relativePos);
							renderPosition.scl(BodyConstants.BOX_TO_WORLD);
							//							shapeRenderer.line(renderPosition, Vector2.Zero);
							shapeRenderer.circle(renderPosition.x, renderPosition.y, circleShape.getRadius() * BodyConstants.BOX_TO_WORLD);

							//							CircleShape circleShape = (CircleShape) fixture.getShape();
							//							Vector2 renderPosition = circleShape.getPosition().cpy();
							//							renderPosition.scl(BodyConstants.BOX_TO_WORLD);
							//							shapeRenderer.circle(renderPosition.x, renderPosition.y, circleShape.getRadius() * BodyConstants.BOX_TO_WORLD);
						}
					}
					shapeRenderer.setColor(Color.WHITE);
				}
			}
		}
		shapeRenderer.rotate(0, 0, 1, -getAngle());
		shapeRenderer.translate(-p.x, -p.y, 0);
		//		shapeRenderer.line(new Vector2(0, 0), part.getPosition());
		//		for (Fixture f : part.body.getFixtureList()) {
		//			shapeRenderer.setColor(Color.WHITE);
		//			if (f.getUserData() instanceof SensorData) {
		//				SensorData s = (SensorData) f.getUserData();
		//								Vector2 dest = NimbyMath.createWorldVector(part.body.getAngle(), s.getAngleRad(), s.getRelativePos().len());
		//								shapeRenderer.line(Vector2.Zero, dest);

		//			}
		//		}
		shapeRenderer.setColor(Color.PINK);
		Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		if (ocam != null) {
			ocam.unproject(v);
		}
		shapeRenderer.circle(v.x, v.y, MOUSE_MARKER_RADIUS);
	}

	@Override
	public void update(final float delta, final OrthographicCamera cam) {
		part.update(delta, cam);
		if (ocam == null) {
			ocam = cam;
		}
		// We should unweld the part
		if (unWedable) {
			if (this.trueUserData instanceof ShipPart) {
				((ShipPart) this.trueUserData).unWeld(part.getWorld());
			} else if (this.trueUserData instanceof Equipment) {
				((Equipment) this.trueUserData).destroyRevoluteJoint();
				thisData.setOccupied(false);
				otherData.setOccupied(false);
			}
			// Inform an unweld has happened
			put = new HashMap<>();
			List<BuildPart> unWeld = new LinkedList<>();
			unWeld.add(this);
			unWeld.add(otherPart);
			put.put(BuildConstants.UNWELD, unWeld);
			pipe.put(put);
			unWedable = false;
		}


		// rotates the part
		if (rotate) {
			float angle = getAngle() + BuildConstants.ANGLE_STEP;
			// Overshot, compensate
			if (angle < -BuildConstants.HALF_CIRCLE_ANGLE) {
				angle += BuildConstants.FULL_CIRCLE_ANGLE;
			}
			if (angle >  BuildConstants.HALF_CIRCLE_ANGLE) {
				angle -= BuildConstants.FULL_CIRCLE_ANGLE;
			}
			// Make the body rotatable and rotate, then set it back
			if (part instanceof Equipment) {
				Equipment equip = (Equipment) this.getTrueUserData();
				equip.body.setFixedRotation(false);
				equip.body.setTransform(equip.body.getPosition(), angle * MathUtils.degreesToRadians);
				equip.body.setFixedRotation(true);
			}
			rotate = false;
		}
		if (getNewAngle() != null) {
			//			part.body.setFixedRotation(false);
			//			part.body.setTransform(part.body.getPosition(), getNewAngle() * MathUtils.degreesToRadians);
			//			part.body.setFixedRotation(true);
			setNewAngle(null);
		}


		// The part can positioned and should be welded
		if (positionable) {
			Vector2 newPos = calculatePositon();
			part.body.setTransform(newPos, getAngle() * MathUtils.degreesToRadians);
			if (!thisData.isOccupied() && !otherData.isOccupied()) {
				if (revolute) {
					ShipPart ship = (ShipPart) otherPart.getTrueUserData();
					Equipment equip = (Equipment) this.getTrueUserData();
					equip.createRevoluteJoint(ship, otherData.getRelativePos().cpy().scl(BodyConstants.WORLD_TO_BOX));
					equip.setOccupiedSensors(thisData, otherData);

				} else {
					ShipPart ship1 = (ShipPart) this.getTrueUserData();
					ShipPart ship2 = (ShipPart) otherPart.getTrueUserData();
					ship1.weld(part.getWorld(), ship2);
					thisData.setOccupied(true);
					otherData.setOccupied(true);
				}

				// Inform a weld has happened
				put = new HashMap<>();
				List<BuildPart> weld = new LinkedList<>();
				weld.add(this);
				weld.add(otherPart);
				put.put(BuildConstants.WELD, weld);
				pipe.put(put);

				otherData = null;
				thisData = null;
			}

			setPositionable(false);
		}	
	}

	/**
	 * @return the rotate
	 */
	public boolean isRotate() {
		return rotate;
	}

	/**
	 * @param rotate the rotate to set
	 */
	public void setRotate(final boolean rotate) {
		this.rotate = rotate;
	}

	/**
	 * @return the newAngle
	 */
	public Float getNewAngle() {
		return newAngle;
	}

	/**
	 * @param newAngle the newAngle to set
	 */
	public void setNewAngle(final Float newAngle) {
		this.newAngle = newAngle;
	}

	/**
	 * @return the isHeld
	 */
	public boolean isHeld() {
		return isHeld;
	}

	/**
	 * @param isHeld the isHeld to set
	 */
	public void setHeld(final boolean isHeld) {
		this.isHeld = isHeld;
	}

	public Part getPart() {
		return part;
	}

	@Override
	public Vector2 getPosition() {
		return part.getPosition();
	}

	@Override
	public float getAngle() {
		return part.getAngle();
	}

	/**
	 * @return the changePos
	 */
	public boolean isPositionable() {
		return positionable;
	}

	/**
	 * @param changePos the changePos to set
	 */
	public void setPositionable(final boolean changePos) {
		this.positionable = changePos;
	}

	/**
	 * Calculates the position this body should have in relation to the other body.
	 * @return The new position in world
	 */
	private Vector2 calculatePositon() {
		Vector2 newPos = new Vector2();
		Vector2 v1 = NimbyMath.createWorldVector(otherBodyAngle, otherData.getAngleRad(), otherData.getRelativePos().len() * BodyConstants.WORLD_TO_BOX);
		Vector2 v2 = NimbyMath.createWorldVector(part.body.getAngle(), thisData.getAngleRad(), thisData.getRelativePos().len() * BodyConstants.WORLD_TO_BOX);
		newPos = otherPos.cpy();
		newPos.add(v1);
		newPos.sub(v2);
		newPos.sub(this.part.getRelativePos());

		otherBodyAngle = null;
		otherPos = null;
		return newPos;
	}

	/**
	 * Set required variable for a change position operation.
	 * 
	 * @param otherPosition The position the body not held
	 * @param angle The angle of the body not held
	 */
	public final void setPositionData(final Vector2 otherPosition , final float angle) {
		otherBodyAngle = angle;
		otherPos = otherPosition;
		setPositionable(true);
	}

	/**
	 * @param heldData The data of sensor held
	 * @param notHeldData The data of sensor not held
	 */
	public final void setCurrentSensors(final SensorData heldData, final SensorData notHeldData) {
		otherData = notHeldData;
		thisData = heldData;
	}

	/**
	 * Setter the part this part has collided with.
	 * @param notHeldPart 
	 */
	public final void setOtherPart(final BuildPart notHeldPart) {
		this.otherPart = notHeldPart;

	}

	/**
	 * Getter for the true user data.
	 * @return the trueUserData
	 */
	public final Object getTrueUserData() {
		return trueUserData;
	}

	/**
	 * Setter for the true user data.
	 * @param trueUserData the trueUserData to set
	 */
	public final void setTrueUserData(final Object trueUserData) {
		this.trueUserData = trueUserData;
	}

	public void setRevolute(final boolean revo) {
		this.revolute = revo;
	}

	public void unWeldable(final boolean unweld) {
		this.unWedable = unweld;

	}

	/**
	 * @return the hovered
	 */
	public boolean isHovered() {
		return hovered;
	}

	/**
	 * @param hovered the hovered to set
	 */
	public void setHovered(final boolean hovered) {
		this.hovered = hovered;
	}

	/**
	 * @return the sensors
	 */
	public final List<Fixture> getSensors() {
		return sensors;
	}

	/**
	 * @return the welding
	 */
	public final boolean isWelding() {
		return welding;
	}

	/**
	 * @param welding the welding to set
	 */
	public final void setWelding(final boolean welding) {
		this.welding = welding;
	}
}
