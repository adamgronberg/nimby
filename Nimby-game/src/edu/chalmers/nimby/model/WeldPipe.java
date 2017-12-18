package edu.chalmers.nimby.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.OrthographicCamera;

import edu.chalmers.nimby.build.BuildConstants;
import edu.chalmers.nimby.build.BuildPart;
import edu.chalmers.nimby.controller.BuildScreen;
import edu.chalmers.nimby.model.gameLogic.Entity;
import edu.chalmers.nimby.util.PipeOut;

/**
 * 
 * @author Victor
 *
 */
public class WeldPipe implements Entity {
	
	private PipeOut<HashMap<String, List<BuildPart>>> pipe;
	private BuildScreen buildScreen;
	public WeldPipe(final PipeOut<HashMap<String, List<BuildPart>>> pipeOut, final BuildScreen bScreen) {
		this.pipe = pipeOut;
		this.buildScreen = bScreen;
	}
	@Override
	public void update(final float delta, final OrthographicCamera cam) {
		if (pipe.hasNext()) {
			HashMap<String, List<BuildPart>> newData = pipe.pull();
			if (newData.containsKey(BuildConstants.WELD)) {
				//System.out.println("Weldded");
				weld(newData.get(BuildConstants.WELD));
			} else if (newData.containsKey(BuildConstants.UNWELD)) {
				//System.out.println("unWeld");
				unWeld(newData.get(BuildConstants.UNWELD));
			}
		}
	}
	
	/**
	 * If two parts are not in the same set in the {@link BuildScreen} they are added to same set.
	 * @param list List with two {@link BuildPart}
	 */
	private void weld(final List<BuildPart> list) {
		if (list.size() != 2) {
			throw new IllegalArgumentException("Can't send more than two parts");
		}
		BuildPart p1 = list.get(0);
		BuildPart p2 = list.get(1);
		Set<BuildPart> set1 = buildScreen.getPartMapSet().get(p1);
		Set<BuildPart> set2 = buildScreen.getPartMapSet().get(p2);
		if (!set1.equals(set2)) {
			set1.addAll(set2);
			buildScreen.getPartListSet().remove(set2);
			for (BuildPart nP : set2) {
				buildScreen.getPartMapSet().put(nP, set1);
			}
		}
	}
	
	/**
	 * If two parts are in the same set in {@link BuildScreen} they are split into two sets.
	 * @param list List with two {@link BuildPart}
	 */
	private void unWeld(final List<BuildPart> list) {
		if (list.size() != 2) {
			throw new IllegalArgumentException("Can't send more than two parts");
		}
		BuildPart p1 = list.get(0);
		BuildPart p2 = list.get(1);
		Set<BuildPart> set1 = buildScreen.getPartMapSet().get(p1);
		Set<BuildPart> set2 = buildScreen.getPartMapSet().get(p2);
		if (set1.equals(set2)) {
			set1.remove(p2);
			Set<BuildPart> newP2Set = new HashSet<>();
			newP2Set.add(p2);
			buildScreen.getPartListSet().add(newP2Set);
		}
	}
}
