package edu.chalmers.service;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mockito.Mockito;

import edu.chalmers.model.Account;
import edu.chalmers.model.Federation;
import edu.chalmers.model.FederationMember;
import edu.chalmers.persistence.IAccountDao;
import edu.chalmers.persistence.IFederationDao;
import edu.chalmers.persistence.IFederationMemberDao;
import edu.chalmers.persistence.JpaAccountDao;
import edu.chalmers.persistence.JpaFederationDao;
import edu.chalmers.persistence.JpaFederationMemberDao;

/**
 * Handles the Federation part of the database and the connection to it. Also managed everything regarding members, such as promote and demote rank.
 * @author Mikael Stolpe
 *
 */
@Path("/federation")
@Stateless
public class FederationResource extends AbstractResource {

	@EJB
	private IFederationDao federationDao; 
	@EJB
	private IFederationMemberDao federationMemberDao;
	
	/**
	 * Checks that the dao is up and running.
	 * @return A hello message
	 */
	@GET()
	@Produces("text/plain")
	public String sayHello() {
		getLogger().log(Level.INFO, federationDao.toString());
		return "Federation RestEasy Service";
	}
	
	/**
	 * Creates a federation in the database.
	 * @param token Token of creator.
	 * @param federationToCreate JSON of federation.
	 * @return OK if successful, error message o/w.
	 */
	@POST()
	@Path("/create/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createFederation(@PathParam("token") final String token, final Federation federationToCreate) {
		if (federationToCreate == null) {
			return Response.status(FEDERATION_NOT_CREATED).entity(NULL_NOT_ALLOWED).build();
		}
		
		if (!isTokenValid(token)) {
			return Response.status(FEDERATION_NOT_CREATED).entity(WRONG_TOKEN).build();
		}
		Account db = getAccountDao().findByToken(token);
		FederationMember member = federationMemberDao.findById(db.getProfileName());
		Federation fed = federationDao.findById(federationToCreate.getFederationName());

		if (fed != null) {
			return Response.status(FEDERATION_NOT_CREATED).entity(FED_ALREADY_EXISTS).build();
		}

		if (member != null) {
			return Response.status(FEDERATION_NOT_CREATED).entity(FED_ALREADY_MEMBER).build();

		} else {
			federationDao.persist(federationToCreate);
			member = new FederationMember();
			member.setRank(TOP);
			member.setAccount(db.getProfileName());
			member.setFederationBean(federationToCreate);
			federationMemberDao.persist(member);
		}

		return Response.status(CREATED).entity(token + ": " + federationToCreate.getFederationName()).build();
	}

	/**
	 * Returns all federations matching a specific name.
	 * @param federationName name to look for.
	 * @return JSON-list with all federations, error message o/w.
	 */
	@GET()
	@Path("/find/byName/{federationName}")
	public Response getByName(@PathParam("federationName") final String federationName) {
		if (!isStringValid(federationName)) {
			return Response.status(GET_FAILED).entity(INVALID_STRING).build();
		}

		List<Federation> matches = federationDao.findByName(federationName);

		GenericEntity<List<Federation>> ge = new GenericEntity<List<Federation>>(matches) { }; 

		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Gets all members of a federation where [token] is a member
	 * @param token token of the member
	 * @return JSON-list with all the names of members of a federation
	 */
	@GET()
	@Path("/find/coMembers/{token}")
	public Response getCoMembers(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		
		Account db = getAccountDao().findByToken(token);
		if (db == null) {
			Response.status(GET_FAILED).entity(MISSING_CONTENT).build();
		}
		
		FederationMember dbFedMem = federationMemberDao.findById(db.getProfileName());
		
		if (dbFedMem == null) {
			Response.status(GET_FAILED).entity(FED_NOT_MEMBER).build();
		}
		
		List<FederationMember> matches = federationMemberDao.getAllMembers(dbFedMem.getFederationBean());
		List<String> memberNames = new LinkedList<>();
		for (FederationMember fMem : matches) {
			memberNames.add(fMem.getAccount());
		}
		
		GenericEntity<List<String>> ge = new GenericEntity<List<String>>(memberNames) { }; 

		return Response.status(STATUS_OK).entity(ge).build();
	}
	
	/**
	 * Gets all members of a federation as JSON.
	 * @param federationName Name of federation.
	 * @return JSON-list with members, error message o/w.
	 */
	@GET()
	@Path("/find/members/{federationName}")
	public Response getMembers(@PathParam("federationName") final String federationName) {
		if (!isStringValid(federationName)) {
			return Response.status(GET_FAILED).entity(INVALID_STRING).build();
		}
		
		Federation fed = federationDao.findById(federationName);
		if (fed == null) {
			return Response.status(GET_FAILED).entity(NAME_DOESNT_EXIST).build();
		}
		 
		List<FederationMember> matches = federationMemberDao.getAllMembers(fed);
		
		GenericEntity<List<FederationMember>> ge = new GenericEntity<List<FederationMember>>(matches) { }; 

		return Response.status(STATUS_OK).entity(ge).build();		
	}
	
	/**
	 * Updates the logo of a federation.
	 * @param token Acc of valid updater.
	 * @param update Federation with new values.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/update/{token}")
	public Response updateName(@PathParam("token") final String token, final Federation update) {
		if (!isTokenValid(token) || update == null) {
			return Response.status(FEDERATION_NOT_UPDATED).entity(MISSING_CONTENT).build();
		}

		Account dbAcc = getAccountDao().findByToken(token);
		FederationMember fedMem = federationMemberDao.findById(dbAcc.getProfileName());

		if (fedMem == null || update.getFederationName() == null || !fedMem.getFederationBean().getFederationName().equals(update.getFederationName()) || update.getLogo() == null) {
			return Response.status(FEDERATION_NOT_UPDATED).entity(MISSING_CONTENT).build();
		}

		federationDao.update(update);

		return Response.status(CREATED).entity(FED_SUCCESSFULLY_UPDATED).build();

	}

	/**
	 * Deletes a federation.
	 * @param token Token of deleter.
	 * @return OK if successful, error message o/w.
	 */
	@DELETE()
	@Path("/delete/{token}")
	public Response delete(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(FEDERATION_NOT_DELETED).entity(MISSING_CONTENT).build();
		}

		Account dbAcc = getAccountDao().findByToken(token);
		FederationMember fedMem = federationMemberDao.findById(dbAcc.getProfileName());

		if (fedMem == null || !fedMem.getRank().equals(TOP)) {
			return Response.status(FEDERATION_NOT_DELETED).entity(FED_INVALID_RANK).build();
		}

		Federation toDelete = federationDao.findById(fedMem.getFederationBean().getFederationName());
		federationDao.remove(toDelete);

		return Response.status(STATUS_OK).entity(FED_SUCCESSFULLY_DELETED).build();		
	}

	/**
	 * Adds a member to the federation with rank cadet.
	 * @param token Token of adder.
	 * @param add Name of person to add.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/member/add/{token}/{add}")
	public Response addMember(@PathParam("token") final String token, @PathParam("add") final String add) {
		if (!isTokenValid(token) || !isStringValid(add)) {
			return Response.status(MEMBER_NOT_ADDED).entity(MISSING_CONTENT).build();
		}

		Account dbAcc = getAccountDao().findByToken(token);
		FederationMember fedMem = federationMemberDao.findById(dbAcc.getProfileName());
		if (fedMem == null) {
			return Response.status(MEMBER_NOT_ADDED).entity(FED_NOT_MEMBER).build();
		}

		String rank = fedMem.getRank();
		if (rank.equals(BOTTOM)) {
			return Response.status(MEMBER_NOT_ADDED).entity(FED_INVALID_RANK).build();
		}

		Account dbFriend = getAccountDao().findById(add);

		if (dbFriend == null || federationMemberDao.findById(dbFriend.getProfileName()) != null) {
			return Response.status(MEMBER_NOT_ADDED).entity(FED_ALREADY_MEMBER).build();
		}

		FederationMember member = new FederationMember();
		member.setRank(BOTTOM);
		member.setAccount(dbFriend.getProfileName());
		member.setFederationBean(fedMem.getFederationBean());
		federationMemberDao.persist(member);

		return Response.status(STATUS_OK).entity(FED_MEMBER_ADDED).build();
	}

	/**
	 * Removes a member from the federation.
	 * @param token Token of remover.
	 * @param remove Name of the member to remove
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/member/remove/{token}/{remove}")
	public Response removeMember(@PathParam("token") final String token, @PathParam("remove") final String remove) {
		if (!rankHelper(token, remove, new String[]{MIDDLE, BOTTOM})) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		FederationMember removeMem = federationMemberDao.findById(remove);
		
		String rank = federationMemberDao.findById(getAccountDao().findByToken(token).getProfileName()).getRank();
		
		String remRank = removeMem.getRank();
		if (rank.equals(TOP) && remRank.equals(TOP) || (rank.equals(MIDDLE) && (remRank.equals(MIDDLE) || remRank.equals(TOP)))) {
			return Response.status(MEMBER_NOT_REMOVED).entity(FED_INVALID_RANK).build();
		}

		federationMemberDao.remove(removeMem);
		return Response.status(STATUS_OK).entity(FED_REMOVED).build();

	}

	/**
	 * Used to leave a federation.
	 * @param token Token of the person which wants to leave.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/leave/{token}")
	public Response leaveFederation(@PathParam("token") final String token) {
		if (!isTokenValid(token)) {
			return Response.status(FEDERATION_CANT_LEAVE).entity(MISSING_CONTENT).build();
		}

		Account dbAcc = getAccountDao().findByToken(token);
		FederationMember fedMem = federationMemberDao.findById(dbAcc.getProfileName());
		if (fedMem == null) {
			return Response.status(FEDERATION_CANT_LEAVE).entity(FED_NOT_MEMBER).build();
		}

		if (fedMem.getRank().equals(TOP)) {
			return Response.status(FEDERATION_CANT_LEAVE).entity(FED_INVALID_RANK).build();
		}

		federationMemberDao.remove(fedMem);

		return Response.status(STATUS_OK).entity(FED_REMOVED).build();
	}
	
	/**
	 * Promotes a member one rank.
	 * @param token Token of promoter.
	 * @param promote Name of member to promote.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/member/promote/{token}/{promote}")
	public Response promote(@PathParam("token") final String token, @PathParam("promote") final String promote) {
		if (!rankHelper(token, promote, new String[]{BOTTOM})) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		FederationMember promoteMem = federationMemberDao.findById(promote);
		String promRank = promoteMem.getRank();
		if (promRank.equals(MIDDLE) || promRank.equals(TOP)) {
			return Response.status(FEDERATION_PROMOTE_FAIL).entity(FED_INVALID_RANK).build();
		}

		promoteMem.setRank(MIDDLE);
		federationMemberDao.update(promoteMem);

		return Response.status(STATUS_OK).entity(FED_SUCCESSFULLY_PROMOTED).build();
	}

	/**
	 * Demotes a member one rank.
	 * @param token Token of demoter.
	 * @param demote Name of member to demote.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/member/demote/{token}/{demote}")
	public Response demote(@PathParam("token") final String token, @PathParam("demote") final String demote) {
		if (!rankHelper(token, demote, new String[]{BOTTOM})) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		FederationMember demoteMem = federationMemberDao.findById(demote);
		String promRank = demoteMem.getRank();
		if (promRank.equals(TOP) || promRank.equals(BOTTOM)) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(FED_INVALID_RANK).build();
		}

		demoteMem.setRank(BOTTOM);
		federationMemberDao.update(demoteMem);

		return Response.status(STATUS_OK).entity(FED_SUCCESSFULLY_DEMOTED).build();
	}
	
	/**
	 * Swiches leadership of the federation.
	 * @param token Token of current leader.
	 * @param newLeader Name of new leader.
	 * @return OK if successful, error message o/w.
	 */
	@PUT()
	@Path("/switchLeader/{token}/{newLeader}")
	public Response switchLeader(@PathParam("token") final String token, @PathParam("newLeader") final String newLeader) {
		if (!rankHelper(token, newLeader, new String[]{MIDDLE, BOTTOM})) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(MISSING_CONTENT).build();
		}
		
		FederationMember promoteMem = federationMemberDao.findById(newLeader);
		FederationMember fedMem = federationMemberDao.findById(getAccountDao().findByToken(token).getProfileName());
		String promRank = promoteMem.getRank();
		if (!promRank.equals(MIDDLE)) {
			return Response.status(FEDERATION_DEMOTE_FAIL).entity(FED_INVALID_RANK).build();
		}

		promoteMem.setRank(TOP);
		fedMem.setRank(MIDDLE);
		
		federationMemberDao.update(fedMem);
		federationMemberDao.update(promoteMem);
		
		return Response.status(STATUS_OK).entity(FED_SUCCESSFULLY_DEMOTED).build();
	}
	
	/**
	 * Checks the status of two accounts in regards to rank and federation.
	 * @param token the caller
	 * @param other member to handle
	 * @param rank which ranks not allowed
	 * @return true if it's a valid promote / demote / switchLeader operation
	 */
	private boolean rankHelper(final String token, final String other, final String[] rank) {
		getLogger().log(Level.INFO, "Entered rankHelper");
		
		if (!isTokenValid(token) || !isStringValid(other)) {
			return false;
		}
		getLogger().log(Level.INFO, "token and strings ok");
		
		
		Account dbAcc = getAccountDao().findByToken(token);
		FederationMember fedMem = federationMemberDao.findById(dbAcc.getProfileName());
		if (fedMem == null) {
			return false;
		}
		getLogger().log(Level.INFO, "caller not present");
		
		String fedRank = fedMem.getRank();
		for (String r : rank) {
			if (fedRank.equals(r)) {
				return false;
			}
		}
		getLogger().log(Level.INFO, "ranks ok");
		
		Account dbFriend = getAccountDao().findById(other);

		if (dbAcc.equals(dbFriend)) {
			return false;
		}
		getLogger().log(Level.INFO, "caller and other not equal ok");
		
		if (dbFriend == null) {
			return false;
		}
		getLogger().log(Level.INFO, "dbFriend ok");
		
		FederationMember promoteMem = federationMemberDao.findById(dbFriend.getProfileName());
		if (promoteMem  == null || !promoteMem.getFederationBean().equals(fedMem.getFederationBean())) {
			return false;
		}
		getLogger().log(Level.INFO, "Exiting with true");
		
		return true;
	}
	
	/**
	 * Sets the account dao to Mockito version.
	 * @return Mocked account dao.
	 */
	public IAccountDao mockAccountDao() {
		setAccountDao(Mockito.mock(JpaAccountDao.class));
		return getAccountDao();
	}
	
	/**
	 * Sets the account dao to Mockito version.
	 * @return Mocked account dao.
	 */
	public IFederationDao mockFederationDao() {
		federationDao = Mockito.mock(JpaFederationDao.class);
		return federationDao;
	}
	
	/**
	 * Sets the account dao to Mockito version.
	 * @return Mocked account dao.
	 */
	public IFederationMemberDao mockFederationMemberDao() {
		federationMemberDao = Mockito.mock(JpaFederationMemberDao.class);
		return federationMemberDao;
	}
}
