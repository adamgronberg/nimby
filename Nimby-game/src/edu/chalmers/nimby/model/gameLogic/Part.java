package edu.chalmers.nimby.model.gameLogic;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.assets.ImagesLoader;
import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.Position;
import edu.chalmers.nimby.util.PartsUtil;
import edu.chalmers.nimby.view.Renderable;

/**
 * A part of a ship, either a part of the hull or a piece of equipment.
 * @author Viktor Sjölind
 *
 */
public abstract class Part extends IdentifiableEntity implements Renderable, Position {

	private static final int DEFAULT_HP = 100;

	private static boolean render = true;
	
	public Body body;
	public float width;
	public float height;
	public float halfWidth;
	public float halfHeight;
	public final String texturePath;
	private World savedWorld;
	public Sprite sprite;
	public final Vector2 relativePos;
	private Fixture fixture; 
	public final String partName;
	public float internalAngle;

	private int hp;

	/**
	 * Constructs the base of all Parts.
	 * @param world the World in which the Part shall exist.
	 * @param position The in the world (pixels).
	 * @param width width of the part.
	 * @param height height of the part.
	 * @param mass mass of the part.
	 * @param mass2 
	 * @param partName the name of the part.
	 * @param texturePath The internal path to the texture used. !!! If on match server use null here!!!
	 */
	public Part(final World world, final Vector2 position, final float angle, final float width, final float height, final float mass, final String partName, final String texturePath) {
		super();
		this.internalAngle = 0;
		this.partName = partName;
		this.relativePos = new Vector2(0, 0);
		this.savedWorld = world;
		this.texturePath = texturePath;
		
		// SPRITE
		if (texturePath != null && render) {
			sprite = new Sprite(ImagesLoader.getInstance().getTexture(texturePath), (int) width, (int) height);
			sprite.setOrigin(width / 2, height / 2);
		}

		// WIDTH / HEIGHT
		this.width = width;
		this.height = height;
		halfWidth = width / 2;
		halfHeight = height / 2;

		// BODY
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.gravityScale = 1;
		def.awake = true;
		def.bullet = true;
		def.active = true;
		def.angle = angle;
		
		if (position != null) {
			def.position.set(position.cpy().scl(BodyConstants.WORLD_TO_BOX));
		} else {
			def.position.set(0, 0);
		}

		body = world.createBody(def);
		MassData m = new MassData();
		m.mass = mass;
		body.setMassData(m);

		// FIXTURE
		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(
				halfWidth * BodyConstants.WORLD_TO_BOX, 
				halfHeight * BodyConstants.WORLD_TO_BOX
				);

		fixture = body.createFixture(polygon, 1);
		fixture.setUserData(this);
		body.setUserData(this);
		polygon.dispose();
		
		// HP
		hp = DEFAULT_HP;
	}


	/**
	 * Unwelds this ShipPart from its parents.
	 * @param world The World in which this ShipPart resides
	 */
	public final void unWeld(final World world) {
		
		if (this instanceof ShipPart) {
			// CREATE A BODY DEF TO BE ABLE TO COPY BODY
			BodyDef def = new BodyDef();
			def.active = body.isActive();
			def.allowSleep = body.isSleepingAllowed();
			def.angle = body.getAngle();
			def.angularDamping = body.getAngularDamping();
			def.angularVelocity = body.getAngularVelocity();
			def.awake = body.isAwake();
			def.bullet = body.isBullet();
			def.fixedRotation = body.isFixedRotation();
			def.gravityScale = body.getGravityScale();
			def.linearDamping = body.getLinearDamping();
			def.linearVelocity.set(body.getLinearVelocity());
			def.position.set(getPosition());

			Body newBody = world.createBody(def);
			// COPY FIXTURES TO NEW BODY
			PartsUtil.copyFixtureToBody(fixture, newBody);
			body.destroyFixture(fixture);
//			if (this instanceof ShipPart) {
//				ShipPart sp = (ShipPart) this;
//				for (Part child : sp.getWeldPartsChildren()) {
//					copyFixtureToBody(child.fixture, newBody);
//					body.destroyFixture(child.fixture);
//				}
//			}
		}
	}

	/**
	 * 
	 */
	protected Part() {
		texturePath = "";
		partName = "";
		this.relativePos = new Vector2(0, 0);
	}

	@Override
	public Vector2 getPosition() {
		Vector2 position;

		position = new Vector2(body.getPosition());
		Vector2 additivePos = getRelativePos().cpy();
		
		additivePos.setAngle(getRelativePos().angle());
		additivePos.rotate(getAngle());
		position.add(additivePos);

		return position.scl(BodyConstants.BOX_TO_WORLD);
	}

	@Override
	public float getAngle() {
		return MathUtils.radiansToDegrees * (body.getAngle() + internalAngle);
	}

	/**
	 * @return the world
	 */
	public final World getWorld() {
		return savedWorld;
	}

	/**
	 * @param world the world to set
	 */
	public final void setWorld(final World world) {
		this.savedWorld = world;
	}

	
	/**
	 * @param render If set true the objects will be rendered. True by default
	 */
	public static void setShouldRender(final boolean render) {
		Part.render = render;
	}
	
	/**
	 * @return true if entities should render.
	 */
	public static boolean getRender() {
		return render;
	}

	/**
	 * @return the fixture
	 */
	public final Fixture getFixture() {
		return fixture;
	}
	
	/**
	 * @param fixture the fixture to set
	 */
	public final void setFixture(final Fixture fixture) {
		this.fixture = fixture;
	}

	/**
	 * @return the relativePos
	 */
	public Vector2 getRelativePos() {
		return relativePos;
	}
	
	public void hurt(final int damage) {
		hp -= damage;
	}
	
	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}
}
