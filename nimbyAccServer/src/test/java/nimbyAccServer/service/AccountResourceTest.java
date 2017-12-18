package nimbyAccServer.service;

import static edu.chalmers.service.AbstractResource.ACCOUNT_NOT_CREATED;
import static edu.chalmers.service.AbstractResource.ACCOUNT_NOT_DELETED;
import static edu.chalmers.service.AbstractResource.ACCOUNT_NOT_UPDATED;
import static edu.chalmers.service.AbstractResource.ACC_SUCCESSFULLY_DELETED;
import static edu.chalmers.service.AbstractResource.ACC_SUCCESSFULLY_DROPPED;
import static edu.chalmers.service.AbstractResource.ACC_SUCCESSFULLY_UPDATED;
import static edu.chalmers.service.AbstractResource.CREATED;
import static edu.chalmers.service.AbstractResource.DELETE_OK;
import static edu.chalmers.service.AbstractResource.DROP_FAIL;
import static edu.chalmers.service.AbstractResource.DROP_OK;
import static edu.chalmers.service.AbstractResource.FRIEND_ADDED;
import static edu.chalmers.service.AbstractResource.FRIEND_ALREADY;
import static edu.chalmers.service.AbstractResource.FRIEND_NOT_ADDED;
import static edu.chalmers.service.AbstractResource.FRIEND_NOT_REMOVED;
import static edu.chalmers.service.AbstractResource.FRIEND_N_REMOVED;
import static edu.chalmers.service.AbstractResource.FRIEND_REMOVED;
import static edu.chalmers.service.AbstractResource.GET_FAILED;
import static edu.chalmers.service.AbstractResource.LOGIN_FAILED;
import static edu.chalmers.service.AbstractResource.LOGIN_OK;
import static edu.chalmers.service.AbstractResource.MAIL_ALREADY_EXISTS;
import static edu.chalmers.service.AbstractResource.MISSING_CONTENT;
import static edu.chalmers.service.AbstractResource.NAME_ALREADY_EXISTS;
import static edu.chalmers.service.AbstractResource.NAME_DOESNT_EXIST;
import static edu.chalmers.service.AbstractResource.NOT_LOGGED_IN;
import static edu.chalmers.service.AbstractResource.NULL_NOT_ALLOWED;
import static edu.chalmers.service.AbstractResource.STATUS_OK;
import static edu.chalmers.service.AbstractResource.WRONG_PASSWORD;
import static edu.chalmers.service.AbstractResource.WRONG_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.chalmers.crypt.BCrypt;
import edu.chalmers.model.Account;
import edu.chalmers.persistence.IAccountDao;
import edu.chalmers.persistence.JpaAccountDao;
import edu.chalmers.service.AccountResource;

@RunWith(Arquillian.class)
public class AccountResourceTest {

	private IAccountDao mockedDao;
	private static Account accStranger;
	private static Account accOK;
	private static Account accNoPass;
	private static Account accNoEmail;
	private static Account accNoProfileName;
	private static Account accNoSeed;
	private static Account accNoToken;
	private static Account accFriend;

	private static final String ACC_PROFILE_NAME = "ACC_PROFILE_NAME";
	private static final String ACC_EMAIL = "accEmail@nimby.com";
	private static final String ACC_PASS = "superSecretPassword";
	private static final String ACC_FRIEND_PROFILE_NAME = "ACC_FRIEND_PROFILE_NAME";
	private static final String ACC_FRIEND_EMAIL = "accFriendEmail@nimby.com";
	private static final String ACC_FRIEND_PASS = "anotherSuperSecretPassword";
	private static final String ACC_STRANGER_PROFILE_NAME = "ACC_STRANGER_PROFILE_NAME";
	private static final String ACC_STRANGER_EMAIL = "accStrangerEmail@nimby.com";
	private static final String ACC_STRANGER_PASS = "thirdSuperSecretPassword";

	@Inject
	private AccountResource accRes;

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class)
				.addClass(AccountResource.class)
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
		accRes = new AccountResource();
		mockedDao = mock(JpaAccountDao.class);
		accRes.setAccountDao(mockedDao);
		
		noFriends(accOK);
		noFriends(accNoEmail);
		noFriends(accNoPass);
		noFriends(accNoProfileName);
		noFriends(accNoSeed);
		noFriends(accNoToken);
		noFriends(accFriend);
		noFriends(accStranger);
		
		makeFriends(accOK, accFriend);
	}



	//	----------------------------------- CREATE ACCOUNT TESTS -------------------------------------
	@Test
	public void testCreateAccountNotExists() {
		when(mockedDao.findById(anyString())).thenAnswer(new Answer<Account>() {

			@Override
			public Account answer(final InvocationOnMock invocation) throws Throwable {
				when(mockedDao.findById(anyString())).thenReturn(accOK);
				return null;
			}
		}).thenReturn(null);

		Response response = accRes.createAccount(accOK);
		assertEquals(CREATED, response.getStatus());
	}

	@Test
	public void testCreateAccountMailAlreadyExists() {
		when(mockedDao.findById(anyString())).thenReturn(null);
		when(mockedDao.findByMail(anyString())).thenReturn(accOK);

		Response response = accRes.createAccount(accOK);
		assertEquals(ACCOUNT_NOT_CREATED, response.getStatus());
		assertEquals(MAIL_ALREADY_EXISTS, response.getEntity());
	}

	@Test
	public void testCreateAccountAlreadyExists() {
		when(mockedDao.findById(anyString())).thenReturn(accOK);

		Response response = accRes.createAccount(accOK);
		assertEquals(ACCOUNT_NOT_CREATED, response.getStatus());
		assertEquals(NAME_ALREADY_EXISTS, response.getEntity());
	}

	@Test
	public void testCreateAccountMissingParameters() {
		testCreateAccountMissingParameterHelper(accNoProfileName);
		testCreateAccountMissingParameterHelper(accNoEmail);
		testCreateAccountMissingParameterHelper(accNoPass);
		testCreateAccountMissingParameterHelper(null);
	}

	private void testCreateAccountMissingParameterHelper(final Account acc) {
		when(mockedDao.findById(anyString())).thenReturn(null);

		Response response = accRes.createAccount(acc);
		assertEquals(ACCOUNT_NOT_CREATED, response.getStatus());
		assertEquals(MISSING_CONTENT, response.getEntity());
	}

	//	----------------------------------- UPDATE ACCOUNT TESTS -------------------------------------
	@Test
	public void testUpdateAccountNotExists() {
		final Account acc = accOK;
		when(mockedDao.findById(anyString())).thenReturn(null);

		Response response = accRes.updateAccount(acc);
		assertEquals(ACCOUNT_NOT_UPDATED, response.getStatus());
		assertEquals(NAME_DOESNT_EXIST, response.getEntity());
	}

	@Test
	public void testUpdateAccountNoToken() {
		final Account acc = accOK;
		when(mockedDao.findById(anyString())).thenReturn(acc);
		when(mockedDao.findByToken(anyString())).thenReturn(null);

		Response response = accRes.updateAccount(acc);
		assertEquals(ACCOUNT_NOT_UPDATED, response.getStatus());
		assertEquals(NOT_LOGGED_IN, response.getEntity());
	}

	@Test
	public void testUpdateAccountNull() {
		final Account acc = null;
		when(mockedDao.findById(anyString())).thenReturn(acc);
		when(mockedDao.findByToken(anyString())).thenReturn(acc);

		Response response = accRes.updateAccount(acc);
		assertEquals(ACCOUNT_NOT_UPDATED, response.getStatus());
		assertEquals(NULL_NOT_ALLOWED, response.getEntity());
	}

	@Test
	public void testUpdateAccount() {
		final Account acc = accOK;
		when(mockedDao.findById(anyString())).thenReturn(acc);
		when(mockedDao.findByToken(anyString())).thenReturn(acc);

		Response response = accRes.updateAccount(acc);
		assertEquals(CREATED, response.getStatus());
		assertEquals(ACC_SUCCESSFULLY_UPDATED + ": " + acc.getProfileName(), response.getEntity());
	}
	//	----------------------------------- DELETE ACCOUNT TESTS -------------------------------------

	@Test
	public void testDeleteAccount() {
		final Account acc = accOK;
		when(mockedDao.findByToken(anyString())).thenReturn(acc);
		when(mockedDao.findById(anyString())).thenReturn(acc);

		Response response = accRes.deleteAcc(acc.getToken(), acc.getPassword());

		assertEquals(DELETE_OK, response.getStatus());
		assertEquals(ACC_SUCCESSFULLY_DELETED, response.getEntity());
	}

	@Test
	public void testDeleteAccountNoToken() {
		final Account acc = accNoToken;
		when(mockedDao.findByToken(anyString())).thenReturn(acc);
		when(mockedDao.findById(anyString())).thenReturn(acc);

		Response response = accRes.deleteAcc(acc.getToken(), acc.getPassword());

		assertEquals(ACCOUNT_NOT_DELETED, response.getStatus());
		assertEquals(WRONG_TOKEN, response.getEntity());
	}

	@Test
	public void testDeleteAccountWrongPass() {
		final Account acc = accNoPass;
		when(mockedDao.findByToken(anyString())).thenReturn(acc);
		when(mockedDao.findById(anyString())).thenReturn(acc);

		Response response = accRes.deleteAcc(acc.getToken(), acc.getPassword());

		assertEquals(ACCOUNT_NOT_DELETED, response.getStatus());
		assertEquals(WRONG_PASSWORD, response.getEntity());
	}
	//	----------------------------------- GET ACCOUNT TESTS -------------------------------------

	@Test
	public void testGetAccount() {
		final Account acc = accOK;
		when(mockedDao.findByToken(anyString())).thenReturn(acc);

		Response response = accRes.getAcc(acc.getToken());

		assertEquals(STATUS_OK, response.getStatus());
		assertEquals(acc, response.getEntity());
	}

	@Test
	public void testGetAccountWrongToken() {
		final Account acc = accNoToken;

		Response response = accRes.getAcc(acc.getToken());

		assertEquals(GET_FAILED, response.getStatus());
		assertEquals(WRONG_TOKEN, response.getEntity());
	}

	//	----------------------------------- LOGIN TESTS -------------------------------------

	@Test
	public void testLogin() {
		final Account acc = accOK;
		when(mockedDao.findById(acc.getProfileName())).thenReturn(acc);

		Response response = accRes.login(acc);

		assertEquals(LOGIN_OK, response.getStatus());
		assertTrue(response.getEntity() instanceof String);
	}

	@Test
	public void testLoginAccountNotExists() {
		final Account acc = accOK;
		when(mockedDao.findById(acc.getProfileName())).thenReturn(null);

		Response response = accRes.login(acc);

		assertEquals(LOGIN_FAILED, response.getStatus());
		assertEquals(NAME_DOESNT_EXIST, response.getEntity());
	}

	@Test
	public void testLoginAccountInvalidPass() {
		final Account acc = accNoPass;
		when(mockedDao.findById(acc.getProfileName())).thenReturn(accOK);

		Response response = accRes.login(acc);

		assertEquals(LOGIN_FAILED, response.getStatus());
		assertEquals(WRONG_PASSWORD, response.getEntity());
	}
//	//	----------------------------------- LOGOUT TESTS -------------------------------------
//
//	@Test
//	public void testLogout() {
//		final Account acc = accOK;
//		when(mockedDao.findById(acc.getProfileName())).thenReturn(acc);
//
//		Response response = accRes.logout(acc);
//
//		assertEquals(LOGOUT_OK, response.getStatus());
//		assertTrue(response.getEntity() instanceof String);
//	}
//
//	@Test
//	public void testLogoutAccountNotExists() {
//		final Account acc = accOK;
//		when(mockedDao.findById(acc.getProfileName())).thenReturn(null);
//
//		Response response = accRes.logout(acc);
//
//		assertEquals(LOGOUT_FAILED, response.getStatus());
//		assertEquals(WRONG_TOKEN, response.getEntity());
//	}

	//	----------------------------------- DROP TOKEN / LOGUT TESTS -------------------------------------
	@Test
	public void testDropLogout() {
		final Account acc = accOK;
		when(mockedDao.findByToken(acc.getToken())).thenReturn(acc);

		Response response = accRes.logout(acc.getToken());

		assertEquals(DROP_OK, response.getStatus());
		assertEquals(ACC_SUCCESSFULLY_DROPPED, response.getEntity());
	}

	@Test
	public void testDropLogoutFail() {
		final Account acc = accOK;
		when(mockedDao.findByToken(acc.getToken())).thenReturn(null);

		Response response = accRes.logout(acc.getToken());

		assertEquals(DROP_FAIL, response.getStatus());
		assertEquals(WRONG_TOKEN, response.getEntity());
	}

	//	----------------------------------- ADD FRIEND TESTS -------------------------------------

	@Test
	public void testAddFriend() {
		final Account me = accOK;
		final Account stranger = accStranger;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(stranger.getProfileName())).thenReturn(stranger);

		// CAN ADD
		Response response1 = accRes.addFriend(me.getToken(), stranger.getProfileName());

		assertEquals(STATUS_OK, response1.getStatus());
		assertEquals(FRIEND_ADDED, response1.getEntity());
		
		// CAN NOT ADD AGAIN
		Response response2 = accRes.addFriend(me.getToken(), stranger.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response2.getStatus());
		assertEquals(FRIEND_ALREADY, response2.getEntity());
	}
	
	@Test
	public void testAddFriendCannotAddSelf() {
		final Account me = accOK;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);

		// CAN NOT ADD SELF
		Response response = accRes.addFriend(me.getToken(), me.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response.getStatus());
		assertEquals(MISSING_CONTENT, response.getEntity());
	}

	@Test
	public void testAddFriendInvalidToken() {
		final Account me = accNoToken;
		final Account stranger = accStranger;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);
		
		when(mockedDao.findByToken(stranger.getToken())).thenReturn(stranger);
		when(mockedDao.findById(stranger.getProfileName())).thenReturn(stranger);
		
		Response response1 = accRes.addFriend(me.getToken(), stranger.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response1.getStatus());
		assertEquals(MISSING_CONTENT, response1.getEntity());
		
		Response response2 = accRes.addFriend(me.getToken(), stranger.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response2.getStatus());
		assertEquals(MISSING_CONTENT, response2.getEntity());
	}

	@Test
	public void testAddFriendNotExist() {
		final Account me = accOK;
		final Account stranger = accStranger;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(stranger.getProfileName())).thenReturn(null);

		Response response = accRes.addFriend(me.getToken(), stranger.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response.getStatus());
		assertEquals(MISSING_CONTENT, response.getEntity());
	}

	@Test
	public void testAddFriendAlreadyFriends() {
		final Account me = accOK;
		final Account friend = accFriend;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);

		when(mockedDao.findByToken(friend.getToken())).thenReturn(friend);
		when(mockedDao.findById(friend.getProfileName())).thenReturn(friend);


		Response response1 = accRes.addFriend(me.getToken(), friend.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response1.getStatus());
		assertEquals(FRIEND_ALREADY, response1.getEntity());

		Response response2 = accRes.addFriend(friend.getToken(), me.getProfileName());

		assertEquals(FRIEND_NOT_ADDED, response2.getStatus());
		assertEquals(FRIEND_ALREADY, response2.getEntity());
	}
	
//	----------------------------------- REMOVE FRIEND TESTS -------------------------------------

	@Test
	public void testRemoveFriend1() {
		final Account me = accOK;
		final Account friend = accFriend;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);

		when(mockedDao.findByToken(friend.getToken())).thenReturn(friend);
		when(mockedDao.findById(friend.getProfileName())).thenReturn(friend);

		// CAN REMOVE
		Response response1 = accRes.removeFriend(me.getToken(), friend.getProfileName());

		assertEquals(FRIEND_REMOVED, response1.getEntity());
		assertEquals(STATUS_OK, response1.getStatus());
		
		// CAN NOT REMOVE AGAIN
		Response response2 = accRes.removeFriend(me.getToken(), friend.getProfileName());

		assertEquals(FRIEND_N_REMOVED, response2.getEntity());
		assertEquals(FRIEND_NOT_REMOVED, response2.getStatus());
	}
	
	@Test
	public void testRemoveFriend2() {
		final Account me = accOK;
		final Account friend = accFriend;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);

		when(mockedDao.findByToken(friend.getToken())).thenReturn(friend);
		when(mockedDao.findById(friend.getProfileName())).thenReturn(friend);

		// CAN REMOVE
		Response response1 = accRes.removeFriend(friend.getToken(), me.getProfileName());

		assertEquals(FRIEND_REMOVED, response1.getEntity());
		assertEquals(STATUS_OK, response1.getStatus());
		
		// CAN NOT REMOVE AGAIN
		Response response2 = accRes.removeFriend(friend.getToken(), me.getProfileName());

		assertEquals(FRIEND_N_REMOVED, response2.getEntity());
		assertEquals(FRIEND_NOT_REMOVED, response2.getStatus());
	}
	
	@Test
	public void testRemoveFriendNotFriends() {
		final Account me = accOK;
		final Account stranger = accStranger;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);
		when(mockedDao.findById(me.getProfileName())).thenReturn(me);

		when(mockedDao.findByToken(stranger.getToken())).thenReturn(stranger);
		when(mockedDao.findById(stranger.getProfileName())).thenReturn(stranger);


		Response response1 = accRes.removeFriend(me.getToken(), stranger.getProfileName());

		assertEquals(FRIEND_NOT_REMOVED, response1.getStatus());
		assertEquals(FRIEND_N_REMOVED, response1.getEntity());

		Response response2 = accRes.removeFriend(stranger.getToken(), me.getProfileName());

		assertEquals(FRIEND_NOT_REMOVED, response2.getStatus());
		assertEquals(FRIEND_N_REMOVED, response2.getEntity());
	}
	
//	----------------------------------- REMOVE FRIEND TESTS -------------------------------------

	@Test
	public void testListFriends() {
		final Account me = accOK;

		when(mockedDao.findByToken(me.getToken())).thenReturn(me);

		Response response = accRes.listFriends(me.getToken());

		assertEquals(STATUS_OK, response.getStatus());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<Account>> ge = (GenericEntity<List<Account>>) response.getEntity();
		List<Account> list = ge.getEntity();
		assertEquals(accFriend, list.get(0));
		assertEquals(accOK.getAccounts1().size(), list.size());
	}
	
	@Test
	public void testListFriendsNoFriends() {
		final Account stranger = accStranger;

		when(mockedDao.findByToken(stranger.getToken())).thenReturn(stranger);

		Response response = accRes.listFriends(stranger.getToken());

		assertEquals(STATUS_OK, response.getStatus());
		assertTrue(response.getEntity() instanceof GenericEntity);
		GenericEntity<List<Account>> ge = (GenericEntity<List<Account>>) response.getEntity();
		List<Account> list = ge.getEntity();
		assertEquals(0, list.size());
	}
	
	@Test
	public void testListFriendsIllegalToken() {
		final Account me = accNoToken;

		Response response = accRes.listFriends(me.getToken());

		assertEquals(GET_FAILED, response.getStatus());
		assertEquals(MISSING_CONTENT, response.getEntity());
	}
}
