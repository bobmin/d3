package bob.d3.d3ext;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.DatabaseException;

public class D3ConnectionDriverTest {

	@Test
	public void testCreateConnection() throws DatabaseException {
		Connection conn = D3ConnectionDriver.createConnection();
		Assert.assertNotNull(conn);
	}

}
