package nimbyAccServer.service;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;
import static edu.chalmers.service.AbstractResource.*;
import edu.chalmers.crypt.BCrypt;
import edu.chalmers.model.Account;
import edu.chalmers.model.Federation;
import edu.chalmers.model.FederationMember;
import edu.chalmers.persistence.IAccountDao;
import edu.chalmers.persistence.IFederationDao;
import edu.chalmers.persistence.IFederationMemberDao;
import edu.chalmers.persistence.JpaAccountDao;
import edu.chalmers.persistence.JpaFederationDao;
import edu.chalmers.persistence.JpaFederationMemberDao;
import edu.chalmers.service.AccountResource;
import edu.chalmers.service.FederationResource;

@RunWith(Arquillian.class)
public class FederationResourceTest {

	private IAccountDao mockedAccountDao;
	private IFederationDao mockedFederationDao;
	private IFederationMemberDao mockedFederationMemberDao;
	
	private static Account accStranger;
	private static Account accOK;
	private static Account accNoPass;
	private static Account accNoEmail;
	private static Account accNoProfileName;
	private static Account accNoSeed;
	private static Account accNoToken;
	private static Account accFriend;
	
	private static Federation fedOKEmpty;
	private static Federation fedOKAccOK;
	private static Federation fedNoName;
	private static Federation fedNoLogo;
	
	private static final String ACC_PROFILE_NAME = "ACC_PROFILE_NAME";
	private static final String ACC_EMAIL = "accEmail@nimby.com";
	private static final String ACC_PASS = "superSecretPassword";
	private static final String ACC_FRIEND_PROFILE_NAME = "ACC_FRIEND_PROFILE_NAME";
	private static final String ACC_FRIEND_EMAIL = "accFriendEmail@nimby.com";
	private static final String ACC_FRIEND_PASS = "anotherSuperSecretPassword";
	private static final String ACC_STRANGER_PROFILE_NAME = "ACC_STRANGER_PROFILE_NAME";
	private static final String ACC_STRANGER_EMAIL = "accStrangerEmail@nimby.com";
	private static final String ACC_STRANGER_PASS = "thirdSuperSecretPassword";
	private static final String FED_NAME = "FED_NAME";
	private static final byte[] FED_LOGO = "FED_LOGO".getBytes();

	@Inject
	private FederationResource fedRes;

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class)
				.addClass(FederationResource.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@BeforeClass
	public static void setup() {
		AccountResource tempRes = new AccountResource();
		long seed = System.nanoTime();
		String salt = tempRes.saltShaker(seed);
		String token = tempRes.tokenGenerator(seed);
		accStranger = mockAccount(ACC_STRANGER_PROFILE_NAME, ACC_STRANGER_EMAIL, ACC_STRANGER_PASS, salt , token);
		seed += 1000;
		salt = tempRes.saltShaker(seed);
		token = tempRes.tokenGenerator(seed);
		accFriend = mockAccount(ACC_FRIEND_PROFILE_NAME, ACC_FRIEND_EMAIL, ACC_FRIEND_PASS, salt , token);

		seed += 1000;
		salt = tempRes.saltShaker(seed);
		token = tempRes.tokenGenerator(seed);
		accOK = mockAccount(ACC_PROFILE_NAME, ACC_EMAIL, ACC_PASS, salt , token);
		accNoProfileName = mockAccount(null, ACC_EMAIL, ACC_PASS, salt, token);
		accNoEmail = mockAccount(ACC_PROFILE_NAME, null, ACC_PASS, salt, token);
		accNoPass = mockAccount(ACC_PROFILE_NAME, ACC_EMAIL, null, salt, token);
		accNoSeed = mockAccount(ACC_PROFILE_NAME, ACC_EMAIL, ACC_PASS, null, token);
		accNoToken = mockAccount(ACC_PROFILE_NAME, ACC_EMAIL, ACC_PASS, salt, null);

		fedOKEmpty = mockFederation(FED_NAME, FED_LOGO);
		fedOKAccOK = mockFederation(FED_NAME, FED_LOGO);
		fedNoLogo = mockFederation(FED_NAME, null);
		fedNoName = mockFederation(null, FED_LOGO);
	}

	private static FederationMember mockFederationMember(Account acc, String rank, Federation fed) {
		FederationMember member = mock(FederationMember.class);
		final String account = acc.getProfileName();
		when(member.getAccount()).thenReturn(account);
		when(member.getRank()).thenReturn(rank);
		when(member.getFederationBean()).thenReturn(fed);
		when(member.getAccountBean()).thenReturn(acc);
		return member;
	}
	
	private static Federation mockFederation(String name, byte[] logo) {
		Federation fed = mock(Federation.class);
		 List<FederationMember> members = new LinkedList<>();
		 
		when(fed.getFederationName()).thenReturn(name);
		when(fed.getFederationmembers()).thenReturn(members);
		when(fed.getLogo()).thenReturn(logo);
		
		return fed;
	}

	private static void makeFriends(Account acc1, Account acc2) {
		List<Account> acc1List = getList(acc1.getAccounts1());
		List<Account> acc2List = getList(acc2.getAccounts1());

		acc1List.add(acc2);
		acc2List.add(acc1);

		when(acc1.getAccounts1()).thenReturn(acc1List);
		when(acc2.getAccounts1()).thenReturn(acc2List);
	}

	private static List<Account> getList(List<Account> list) {
		List<Account> accList = list;
		if (accList == null) {
			accList = new LinkedList<Account>();
		}
		return accList;
	}

	private static Account mockAccount(String profileName, String email, String password, String salt, String token) {
		Account acc = mock(Account.class);
		when(acc.getProfileName()).thenReturn(profileName);
		when(acc.getEmail()).thenReturn(email);
		when(acc.getPassword()).thenReturn(password);
		when(acc.getSalt()).thenReturn(salt);
		when(acc.getToken()).thenReturn(token); 
		noFriends(acc);

		if (password != null && salt != null) {
			when(acc.getHash()).thenReturn(BCrypt.hashpw(password, salt));
		}
		return acc;
	}
	
	private static void noFriends(Account acc) {
		when(acc.getAccounts1()).thenReturn(new LinkedList<Account>());
		when(acc.getAccounts2()).thenReturn(new LinkedList<Account>());
	}
	
	@Before
	public void startup() {
	
		fedRes = new FederationResource();
		mockedAccountDao = fedRes.mockAccountDao();
		mockedFederationDao = fedRes.mockFederationDao();
		mockedFederationMemberDao = fedRes.mockFederationMemberDao();
		
		noFriends(accOK);
		noFriends(accNoEmail);
		noFriends(accNoPass);
		noFriends(accNoProfileName);
		noFriends(accNoSeed);
		noFriends(accNoToken);
		noFriends(accFriend);
		noFriends(accStranger);
		
		makeFriends(accOK, accFriend);
		
		clearFederation(fedOKAccOK);
		clearFederation(fedOKEmpty);
		clearFederation(fedNoLogo);
		clearFederation(fedNoName);
		List<FederationMember> fedMembers = new LinkedList<>();
		fedMembers.add(mockFederationMember(accOK,TOP ,fedOKAccOK));
		when(fedOKAccOK.getFederationmembers()).thenReturn(fedMembers);
		
	}

	private void clearFederation(final Federation fed) {
		when(fed.getFederationmembers()).thenReturn(new LinkedList<FederationMember>());
	}

	//	----------------------------------- CREATE FEDERATION TESTS -------------------------------------
	@Test
	public void testCreateFederation() {
		final Account me = accOK;
		final Federation fed = fedOKEmpty;

		when(mockedAccountDao.findByToken(me.getToken())).thenReturn(me);
		
		Response response = fedRes.createFederation(me.getToken(), fed);
		assertEquals(me.getToken() + ": " + fed.getFederationName(), response.getEntity());
		assertEquals(CREATED, response.getStatus());
	}
	
	@Test
	public void testCreateFederationNoToken() {
		final Account me = accNoToken;
		final Federation fed = fedOKEmpty;

		when(mockedAccountDao.findByToken(me.getToken())).thenReturn(me);
		
		Response response = fedRes.createFederation(me.getToken(), fed);
		assertEquals(WRONG_TOKEN, response.getEntity());
		assertEquals(FEDERATION_NOT_CREATED, response.getStatus());
	}
	
	@Test
	public void testCreateFederationNullFederation() {
		final Account me = accOK;

		when(mockedAccountDao.findByToken(me.getToken())).thenReturn(me);
		
		Response response = fedRes.createFederation(me.getToken(), null);
		assertEquals(NULL_NOT_ALLOWED, response.getEntity());
		assertEquals(FEDERATION_NOT_CREATED, response.getStatus());
	}
	
	@Test
	public void testCreateFederationAlreadyExists() {
		final Account me = accOK;
		final Federation fed = fedOKAccOK;
		
		when(mockedAccountDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedFederationDao.findById(fed.getFederationName())).thenReturn(fed);
		
		Response response = fedRes.createFederation(me.getToken(), fed);
		assertEquals(FED_ALREADY_EXISTS, response.getEntity());
		assertEquals(FEDERATION_NOT_CREATED, response.getStatus());
	}
	
	@Test
	public void testCreateFederationAlreadyMember() {
		final Account me = accOK;
		final Federation fed = fedOKEmpty;
		final FederationMember member = fedOKAccOK.getFederationmembers().get(0);
		when(mockedAccountDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedFederationMemberDao.findById(me.getProfileName())).thenReturn(member);
		
		Response response = fedRes.createFederation(me.getToken(), fed);
		assertEquals(FED_ALREADY_MEMBER, response.getEntity());
		assertEquals(FEDERATION_NOT_CREATED, response.getStatus());
	}
	//	----------------------------------- GET FEDERATIONS BY NAME TESTS -------------------------------------
	
	@Test
	public void testFederationsByName() {
		final Federation fed = fedOKAccOK;
		
		LinkedList<Federation> mockFederationList = new LinkedList<>();
		mockFederationList.add(fedOKAccOK);
		when(mockedFederationDao.findByName(fed.getFederationName())).thenReturn(mockFederationList);
		
		Response response = fedRes.getByName(fed.getFederationName());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<Federation>> ge = (GenericEntity<List<Federation>>) response.getEntity();
		List<Federation> federationList = ge.getEntity();
		
		assertEquals(fedOKAccOK, federationList.get(0));
		assertEquals(mockFederationList.size(), federationList.size());
		assertEquals(STATUS_OK, response.getStatus());
	}
	
	@Test
	public void testFederationsByNameEmpty() {
		final Federation fed = fedOKAccOK;
		
		LinkedList<Federation> mockFederationList = new LinkedList<>();
		when(mockedFederationDao.findByName(fed.getFederationName())).thenReturn(mockFederationList);
		
		Response response = fedRes.getByName(fed.getFederationName());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<Federation>> ge = (GenericEntity<List<Federation>>) response.getEntity();
		List<Federation> federationList = ge.getEntity();
		
		assertEquals(mockFederationList.size(), federationList.size());
		assertEquals(STATUS_OK, response.getStatus());
	}
	
	@Test
	public void testFederationsByNameNull() {
		Response response = fedRes.getByName(null);
		
		assertEquals(INVALID_STRING, response.getEntity());
		assertEquals(GET_FAILED, response.getStatus());
	}
	//	----------------------------------- GET MEMBERS BY FEDERATION -------------------------------------
	
	@Test
	public void testFederationMembersByFederationName() {
		final Federation fed = fedOKAccOK;
		
		when(mockedFederationDao.findById(fed.getFederationName())).thenReturn(fed);
		List<FederationMember> mockedFederationMemberList = fed.getFederationmembers();
		when(mockedFederationMemberDao.getAllMembers(fed)).thenReturn(mockedFederationMemberList);
		
		Response response = fedRes.getMembers(fed.getFederationName());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<FederationMember>> ge = (GenericEntity<List<FederationMember>>) response.getEntity();
		List<FederationMember> federationList = ge.getEntity();

		assertEquals(STATUS_OK, response.getStatus());
		assertEquals(mockedFederationMemberList.size(), federationList.size());
		for (int i = 0; i < federationList.size(); i++) {
			assertEquals(mockedFederationMemberList.get(i), federationList.get(i));
		}
	}
	
	@Test
	public void testFederationMembersByFederationNameEmptyList() {
		final Federation fed = fedOKAccOK;
		
		when(mockedFederationDao.findById(fed.getFederationName())).thenReturn(fed);
		List<FederationMember> mockedFederationMemberList = new LinkedList<>();
		when(mockedFederationMemberDao.getAllMembers(fed)).thenReturn(mockedFederationMemberList);
		
		Response response = fedRes.getMembers(fed.getFederationName());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<FederationMember>> ge = (GenericEntity<List<FederationMember>>) response.getEntity();
		List<FederationMember> federationList = ge.getEntity();

		assertEquals(STATUS_OK, response.getStatus());
		assertEquals(mockedFederationMemberList.size(), federationList.size());
//		for (int i = 0; i < federationList.size(); i++) {
//			assertEquals(mockedFederationMemberList.get(i), federationList.get(i));
//		}
	}
	
	@Test
	public void testFederationMembersByFederationNameNull() {
		Response response = fedRes.getMembers(null);
		
		assertEquals(INVALID_STRING, response.getEntity());
		assertEquals(GET_FAILED, response.getStatus());
	}
	
	@Test
	public void testFederationMembersByFederationNameNotFound() {
		final Federation fed = fedOKAccOK;
		when(mockedFederationDao.findById(fed.getFederationName())).thenReturn(null);
		
		Response response = fedRes.getMembers(fed.getFederationName());
		assertEquals(NAME_DOESNT_EXIST, response.getEntity());
		assertEquals(GET_FAILED, response.getStatus());
	}
}
