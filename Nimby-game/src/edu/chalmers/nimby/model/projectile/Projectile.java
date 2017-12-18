package edu.chalmers.nimby.model.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.assets.ImagesLoader;
import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.Position;
import edu.chalmers.nimby.model.gameLogic.IdentifiableEntity;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.view.Renderable;

/**'
 * A projectile spawned by Equipment. Explodes on collision.
 * @author Viktor Sjölind
 *
 */
public class Projectile extends IdentifiableEntity implements Renderable, Position {

	private static final float TIME_BEFORE_REMOVE = 1000;
	
	public final Body body;
	private final Fixture hitBoxFixture;
	private final Fixture aoeFixture;
	private final Sprite sprite;
	private final HitEffect hitEffect;
	private float width;
	private float halfHeight;
	private float halfWidth;
	private float height;
	private Fixture creator;
	private boolean contact;
	private float timeAlive;
	private boolean shouldBeRemoved;
	
	public Projectile(final World world, final Vector2 position, final float aoeRadius, final float width, final float height, final HitEffect hitEffect, final String texturePath) {
		
		if (texturePath != null && Part.getRender()) {
			sprite = new Sprite(ImagesLoader.getInstance().getTexture(texturePath));
			sprite.setOrigin(width / 2, height / 2);
		} else {
			sprite = null;
		}
		
		this.width = width;
		this.height = height;
		halfWidth = width / 2;
		halfHeight = height / 2;
		
		timeAlive = 0;
		shouldBeRemoved = false;
		
		// BODY
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 1;
		bodyDef.active = true;
		bodyDef.bullet = true;
		bodyDef.awake = true;
		
		if (position != null) {
			bodyDef.position.set(position.cpy().scl(BodyConstants.WORLD_TO_BOX));
		} else {
			bodyDef.position.set(0, 0);
		}
		
		body = world.createBody(bodyDef);

		// AOE FIXTURE
		FixtureDef aoeFixtureDef = new FixtureDef();
		aoeFixtureDef.isSensor = true;
		CircleShape aoeFixtureShape = new CircleShape();
		
		// Can not set radius less than the largest of width and height
		final float radius = Math.max(aoeRadius, Math.max(width, height));
		aoeFixtureShape.setRadius(radius * BodyConstants.WORLD_TO_BOX);
		aoeFixtureDef.shape = aoeFixtureShape;
		aoeFixture = body.createFixture(aoeFixtureDef);
		aoeFixture.setUserData(this);
		
		// HITBOX FIXTURE
		FixtureDef hitBoxFixtureDef = new FixtureDef();
		hitBoxFixtureDef.isSensor = true;
		PolygonShape hitBoxFixtureShape = new PolygonShape();
		
		hitBoxFixtureShape.setAsBox(width * BodyConstants.WORLD_TO_BOX, height * BodyConstants.WORLD_TO_BOX);
		hitBoxFixtureDef.shape = hitBoxFixtureShape;
		hitBoxFixture = body.createFixture(hitBoxFixtureDef);
		hitBoxFixture.setUserData(this);
		
		hitBoxFixtureShape.dispose();
		aoeFixtureShape.dispose();
		// WHAT HAPPENS ON HIT
		this.hitEffect = hitEffect;
		
		// APPLY INITAL FORCE
		
		
		contact = false;
		setContact(true);
	}
	
	@Override
	public void update(final float delta, final OrthographicCamera cam) {
		timeAlive += delta;
		if (timeAlive >= TIME_BEFORE_REMOVE && !shouldBeRemoved) {
			shouldBeRemoved = true;
		}
	}
	
	/**
	 * @return omg temp Is true if the projectile should be removed.
	 */
	public final boolean shouldBeRemoved() {
		return shouldBeRemoved;
	}

	@Override
	public void render(final SpriteBatch batch, final BitmapFont font) {
		Vector2 p = getPosition();
		sprite.setRotation(getAngle());
		sprite.setPosition(p.x - halfWidth, p.y - halfHeight);
		
		sprite.draw(batch);
	}

	@Override
	public void debug(final ShapeRenderer sr) {
		Vector2 p = getPosition();
		sr.setColor(Color.MAGENTA);

		sr.translate(p.x, p.y, 0);
		sr.rotate(0, 0, 1, getAngle());

//		sr.rect(-halfWidth, -halfHeight, width, height);

		sr.rotate(0, 0, 1, -getAngle());
		sr.translate(-p.x, -p.y, 0);
	}
	
	/**
	 * @param vector Sets the Velocity of the projectile.
	 */
	public final void setVelocity(final Vector2 vector) {
		body.setLinearVelocity(vector);
	}
	
	/**
	 * @param vector Sets the Velocity of the projectile.
	 * @return 
	 */
	public final Vector2 getVelocity() {
		return body.getLinearVelocity();
	}
	
	@Override
	public Vector2 getPosition() {
		return body.getPosition().cpy().scl(BodyConstants.BOX_TO_WORLD);
	}

	@Override
	public float getAngle() {
		return MathUtils.radiansToDegrees * (body.getAngle());
	}

	/**
	 * @return the hitBoxFixture
	 */
	public final Fixture getHitBoxFixture() {
		return hitBoxFixture;
	}

	/**
	 * @return the aoeFixture
	 */
	public final Fixture getAoeFixture() {
		return aoeFixture;
	}

	public boolean hasContact() {
		return contact;
	}
	
	public void setContact(final boolean contact) {
		this.contact = contact;
	}

	/**
	 * Resolve the result of the collision with the projectiles {@link HitEffect}.
	 * @param contact {@link Contact} containing information about the collision.
	 */
	public void resolveContact(final Contact contact) {
		hitEffect.resolveHit(contact);
	}

	/**
	 * Signals that the projectile has hit something, confirm with server.
	 * @param contact {@link Contact} containing information about the collision.
	 */
	public void hit(final Contact contact) {
//		hitEffect.resolveHit(contact, creator);
		setContact(true);
	}

	/**
	 * @return the creator
	 */
	public final Fixture getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public final void setCreator(final Fixture creator) {
		this.creator = creator;
	}

	/**
	 * Sets the projectile to active mode, which means it will explode upon contact with anything.
	 */
	public void arm() {
		hitBoxFixture.setSensor(false);
	}
	
	public void setPosition(final Vector2 position, final float angle) {
		body.setTransform(position, angle);
	}
}
