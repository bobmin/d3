package bob.d3.export;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class D3ConnectionDriverTest {

	@Test
	public void testCreateConnection() throws ClassNotFoundException, SQLException {
		Connection conn = D3ConnectionDriver.createConnection();
		Assert.assertNotNull(conn);
	}

}
