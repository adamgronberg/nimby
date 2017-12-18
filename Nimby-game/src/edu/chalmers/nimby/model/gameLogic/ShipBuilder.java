package edu.chalmers.nimby.model.gameLogic;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.World;

import edu.chalmers.nimby.camera.CameraControllerFollowShip;
import edu.chalmers.nimby.controller.MatchScreen;
import edu.chalmers.nimby.inputProcessors.EquipmentInputProcessor;
import edu.chalmers.nimby.model.equipment.Equipment;
import edu.chalmers.nimby.model.factory.PartBuilder;
import edu.chalmers.nimby.util.Bundle;

/**
 * Builds incoming ships.
 * @author Adam Gr�nberg
 *
 */
public final class ShipBuilder extends EntityHandeler {

	private byte[] ship;
	private World world;
	
	/**
	 * Constructor. Builds and adds the ships as bundles.
	 * @param ships The ships to build.
	 * @param matchScreen the world to build in.
	 * @param world the world used by the screen.
	 */
	public ShipBuilder(final byte[] ship, final MatchScreen matchScreen, final World world) {
		super(matchScreen);
		this.ship = ship;
		this.world = world;
	}

	@Override
	public void command() {
		List<Bundle> bundles = new LinkedList<Bundle>();
		try {
			Part[] parts = PartBuilder.createSpaceShip(ship, world);
			boolean mine = parts[0].getMatchPartUniqueID().userID == IdentifiableEntity.getUserUniqueID();
				
			if (mine) {
				CameraControllerFollowShip camControll = (CameraControllerFollowShip) ((MatchScreen) screen).getCamControll();
				camControll.setShipToFollow(parts[0]);
			}
				
			for (Part part: parts) {
				if (part instanceof Equipment && mine) {
					Equipment eq = (Equipment) part;
					EquipmentInputProcessor eip = new EquipmentInputProcessor(eq);
						((MatchScreen) screen).addInputProcessor(eip);
					}
					Bundle bundle = new Bundle(part, part);
					bundles.add(bundle);
				}
		} catch (UnsupportedEncodingException | SQLException e) {
			e.printStackTrace();
		}
		
		for (Bundle bundle: bundles) {
			screen.addBundles(bundle);
		}
	}
}
