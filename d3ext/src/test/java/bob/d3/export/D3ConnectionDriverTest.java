package bob.d3.export;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

import bob.d3.D3ExException.DatabaseException;
import bob.d3.export.D3ConnectionDriver;

public class D3ConnectionDriverTest {

	@Test
	public void testCreateConnection() throws DatabaseException {
		Connection conn = D3ConnectionDriver.createConnection();
		Assert.assertNotNull(conn);
	}

}
