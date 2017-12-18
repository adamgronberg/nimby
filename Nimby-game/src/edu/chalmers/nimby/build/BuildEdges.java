package edu.chalmers.nimby.build;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.view.Renderable;

/**
 * Handles the boundaries for the build mode.
 * @author Mikael Stolpe
 *
 */
public class BuildEdges implements Entity, Renderable {
	private World world;
	private Body edge;
	private float boundaryWidth;
	private float boundaryHeight;

	/**
	 * Default, takes world.
	 * @param w world
	 */
	public BuildEdges(final World w) {
		this.world = w;
		boundaryWidth = BuildConstants.BOUNDARY_WIDTH;
		boundaryHeight = BuildConstants.BOUNDARY_HEIGHT;
		defineBoundaries();
	}
	
	
	public BuildEdges(World w, float boundaryW,
			float boundaryH) {
		this.world = w;
		this.boundaryWidth = boundaryW;
		this.boundaryHeight = boundaryH;
		defineBoundaries();
	}


	/**
	 * Creates the boundaries.
	 */
	private void defineBoundaries() {
		BodyDef def = new BodyDef(); 
		def.type = BodyType.StaticBody;
		Vector2 boundaryPos = new Vector2(0, 0);
		def.position.set(boundaryPos);
		edge = world.createBody(def);
		
		//shape definition
		PolygonShape polygonShape = new PolygonShape();
		//fixture definition
		FixtureDef fDef = new FixtureDef(); 
		fDef.filter.categoryBits = (short) BuildColliderFilter.BOUNDARY.getColDef();
		fDef.filter.maskBits = (short) (BuildColliderFilter.NODE_SHIP.getColDef() | BuildColliderFilter.NODE_EQUIPMENT.getColDef() 
				| BuildColliderFilter.PART_EQUIPMENT.getColDef() | BuildColliderFilter.PART_SHIP.getColDef());
		//add four walls to the static body
		float thickness = BuildConstants.BOUNDARY_THICKNESS;
		polygonShape.setAsBox(boundaryWidth , thickness, new Vector2(boundaryWidth, 0), 0);
		fDef.shape = polygonShape;
		edge.createFixture(fDef);
		polygonShape.setAsBox(boundaryWidth, thickness, new Vector2(boundaryWidth, boundaryHeight * 2), 0);
		fDef.shape = polygonShape;
		edge.createFixture(fDef);
		polygonShape.setAsBox(thickness, boundaryHeight, new Vector2(0, boundaryHeight), 0);
		fDef.shape = polygonShape;
		edge.createFixture(fDef);
		polygonShape.setAsBox(thickness, boundaryHeight, new Vector2(boundaryWidth * 2, boundaryHeight), 0);
		fDef.shape = polygonShape;
		edge.createFixture(fDef);
		
	}

	@Override
	public void render(final SpriteBatch batch, final BitmapFont font) {
	}

	@Override
	public void debug(final ShapeRenderer sr) {
		if (edge != null) {

			Vector2 p = edge.getPosition();
			p.scl(BodyConstants.BOX_TO_WORLD);
			sr.setColor(Color.GREEN);

			sr.translate(p.x, p.y, 0);
			sr.rotate(0, 0, 1, edge.getAngle() * MathUtils.radiansToDegrees);
			for (Fixture f : edge.getFixtureList()) {
				if (f.getShape() instanceof PolygonShape) {
					PolygonShape pol = (PolygonShape) f.getShape();
					for (int i = 0; i < pol.getVertexCount(); i++) {
						Vector2 vertex1 = new Vector2();
						Vector2 vertex2 = new Vector2();
						pol.getVertex(i, vertex1);
						pol.getVertex((i + 1) % pol.getVertexCount(), vertex2);
						vertex1.scl(BodyConstants.BOX_TO_WORLD);
						vertex2.scl(BodyConstants.BOX_TO_WORLD);
						sr.line(vertex1.x, vertex1.y, vertex2.x, vertex2.y);
					}
				}
			}
			sr.rotate(0, 0, 1, edge.getAngle() * MathUtils.radiansToDegrees);
			sr.translate(-p.x, -p.y, 0);
		}
	}

	@Override
	public void update(final float delta, final OrthographicCamera cam) {
	}

}
