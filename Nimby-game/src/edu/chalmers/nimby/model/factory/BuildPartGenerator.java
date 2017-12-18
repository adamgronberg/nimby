package edu.chalmers.nimby.model.factory;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.assets.ShipData;
import edu.chalmers.nimby.build.BuildColliderFilter;
import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.model.BodyConstants;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.gameLogic.ShipPart;
import edu.chalmers.nimby.util.PipeIn;

/**
 * Generates different parts to be used in the {@link BuildScreen}.
 * @author Mikael Stolpe
 *
 */
public final class BuildPartGenerator {
	private static final float SENSOR_OFFSET_MULTPIPLIER = 1.5f;
	private BuildPartGenerator() { }
	
	/**
	 * Returns a {@link Filter} for a ship sensor.
	 * @return Ship sensor filter
	 */
	private static Filter getShipSensFilter() {
		Filter shipSensFilter = new Filter();
		shipSensFilter.categoryBits =  (short) BuildColliderFilter.NODE_SHIP.getColDef();
		shipSensFilter.maskBits = (short) (BuildColliderFilter.BOUNDARY.getColDef() | BuildColliderFilter.NODE_SHIP.getColDef());
		return shipSensFilter;
	}
	
	/**
	 * Returns a {@link Filter} for an equipment sensor.
	 * @return Equipment sensor filter
	 */
	private static Filter getEquipSensFilter() {
		Filter equipSensFilter = new Filter();
		equipSensFilter.categoryBits = (short) BuildColliderFilter.NODE_EQUIPMENT.getColDef();
		equipSensFilter.maskBits = (short) (BuildColliderFilter.BOUNDARY.getColDef() | BuildColliderFilter.NODE_EQUIPMENT.getColDef());
		return equipSensFilter;
	}
	
	/**
	 * Returns a {@link BuildPart} Part for a {@link ShipPart}.
	 * @param world The word object
	 * @param pos The position to place the BuildPart
	 * @return The created BuildPart
	 */
	public static BuildPart getShipBuildPart(final World world, final Vector2 pos, final PipeIn<HashMap<String, List<BuildPart>>> pipe) {
		BuildPart buildPart = new BuildPart(new ShipPart(world, pos, ShipData.TIE_WHOLE_WIDTH, 
				ShipData.TIE_SIDE_HEIGHT, ShipData.PART_MASS, ShipData.TIE_WHOLE_NAME, ShipData.TIE_WHOLE), pipe);
		buildPart.getPart().body.setFixedRotation(true);
		buildPart = createShipPartSensors(buildPart);
		return buildPart;
		
	}
	
	/**
	 * Returns a {@link BuildPart} Part for a {@link ShipPart}.
	 * @param world The word object
	 * @param pos The position to place the BuildPart
	 * @return The created BuildPart
	 */
	public static BuildPart getShipBuildPartHigh(final World world, final Vector2 pos, final PipeIn<HashMap<String, List<BuildPart>>> pipe) {
		BuildPart buildPart = new BuildPart(new ShipPart(world, pos, ShipData.TIE_SIDE_HEIGHT, ShipData.TIE_WHOLE_WIDTH, ShipData.PART_MASS, ShipData.TIE_WHOLE_NAME, ShipData.TIE_WHOLE_UP), pipe);
		buildPart.getPart().body.setFixedRotation(true);
		buildPart = createShipPartSensors(buildPart);
		return buildPart;
		
	}
	
	/**
	 * General sensor type creation platform.
	 * @param buildPart buildpart to fix sensors to
	 * @return sensorized buildpart
	 */
	private static BuildPart createShipPartSensors(final BuildPart buildPart) {
		Vector2 v1 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		Vector2 v2 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.SHIP_SENSOR_RADIUS, getShipSensFilter());
		
		v1 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		
		v1 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, 0);
		v2 = new Vector2(0,  -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		
		v1 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, 0);
		v2 = new Vector2(0, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		
		v1 = new Vector2(0, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX,  0);
		buildPart.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		
		v1 = new Vector2(0, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, 0);
		buildPart.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		
		v1 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.SHIP_SENSOR_RADIUS, getShipSensFilter());

		v1 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.SHIP_SENSOR_RADIUS, getShipSensFilter());

		v1 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		v2 = new Vector2(-buildPart.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, buildPart.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		buildPart.createShipSensor2(v1, v2, BuildConstants.SHIP_SENSOR_RADIUS, getShipSensFilter());
		return buildPart;
	}

	/**
	 * Returns a {@link BuildPart} Part for a {@link Equipment}.
	 * @param world The word object
	 * @param pos The position to place the BuildPart
	 * @return The created BuildPart
	 */
	public static BuildPart getEngineBuildPart(final World world, final Vector2 pos, final float angle, final PipeIn<HashMap<String, List<BuildPart>>> pipe) {
		StringBuilder sb = new StringBuilder();
		sb.append("{KEY_DOWN}");
		sb.append("{Boost;500}");
		sb.append("{END}");
		final float engineCD = 0.1f;
		BuildPart engine = new BuildPart(new Equipment(world, pos, angle, ShipData.TIE_ENGINE_SIZE, ShipData.TIE_ENGINE_SIZE, ShipData.PART_MASS, ShipData.TIE_ENGINE_NAME, ShipData.TIE_ENGINE_PART, engineCD, sb.toString() , Keys.W), pipe);
		Vector2 v1 = new Vector2(-engine.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -engine.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		Vector2 v2 = new Vector2(engine.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, engine.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		engine.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		engine.getPart().body.setFixedRotation(true);
		return engine;
	}
	
	/**
	 * Returns a {@link BuildPart} Part for an equipment i.e weapon.
	 * @param world The word object
	 * @param pos The position to place the BuildPart
	 * @return The created BuildPart
	 */
	public static BuildPart getWeaponBuildPart(final World world, final Vector2 pos, final float angle, final PipeIn<HashMap<String, List<BuildPart>>> pipe) {
		StringBuilder sb = new StringBuilder();
		sb.append("{KEY_DOWN}");
		sb.append("{SpawnProjectile;500}");
		sb.append("{END}");
		final float weaponCD = 0.1f;
		BuildPart equipment = new BuildPart(new Equipment(world, pos, angle, ShipData.TIE_WEAPON_SIZE_WIDTH, ShipData.TIE_WEAPON_SIZE_HEIGHT, ShipData.WEAPON_MASS, ShipData.TIE_WEAPON_NAME, ShipData.TIE_WEAPON, weaponCD, sb.toString(), Keys.SPACE), pipe);
		Vector2 v1 = new Vector2(-equipment.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, -equipment.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		Vector2 v2 = new Vector2(equipment.getPart().halfWidth * BodyConstants.WORLD_TO_BOX, equipment.getPart().halfHeight * BodyConstants.WORLD_TO_BOX);
		equipment.createShipSensor2(v1, v2, BuildConstants.EQUIPMENT_SENSOR_RADIUS, getEquipSensFilter());
		equipment.getPart().body.setFixedRotation(true);
		return equipment;
	}
}
