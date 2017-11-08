package bob.d3.d3ext;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDatabaseTest {

	@Test
	public void testCreateConnection() throws DatabaseException {
		Connection conn = D3ExDatabase.createConnection();
		Assert.assertNotNull(conn);
	}

}
