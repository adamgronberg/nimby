package edu.chalmers.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Initiates the RESTServices for Bean injection.
 * @author Mikael
 *
 */
@ApplicationPath("")
public class RESTInit extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	
	/**
	 * Adds all resources to class set.
	 */
	public RESTInit() {
		getClasses().add(AccountResource.class);
		getClasses().add(ShipResource.class);
		getClasses().add(FederationResource.class);
		getClasses().add(ScoreboardResource.class);
		getClasses().add(PartResource.class);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}
