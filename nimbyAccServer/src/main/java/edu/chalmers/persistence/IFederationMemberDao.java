package edu.chalmers.persistence;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Path;

import edu.chalmers.model.Federation;
import edu.chalmers.model.FederationMember;

/**
 * Defines methods specific for FederationMember.
 * @author Mikael Stolpe
 *
 */
@Local
@Path("/federation")
public interface IFederationMemberDao extends Dao<String, FederationMember> {
	/**
	 * Looks for all members of a federation.
	 * @param federation Federation to get members from.
	 * @return All members in a federation.
	 */
	List<FederationMember> getAllMembers(Federation federation);
	
}
