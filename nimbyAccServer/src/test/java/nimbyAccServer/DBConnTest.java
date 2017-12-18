package nimbyAccServer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

public class DBConnTest extends TestCase {

    private EntityManagerFactory entityManagerFactory;

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testBasicUsage() {
		assertTrue(true);
	}
}
