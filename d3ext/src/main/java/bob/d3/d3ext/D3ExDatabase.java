package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDatabase {

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/** der Benutzername für D3P auf SRVMSSQL03 */
	private static final String USERNAME = "burtool";

	/** das Kennwort für D3P auf SRVMSSQL03 */
	private static final char[] PASSWORD = new char[] { 'L', 't', 'T', 'D', 'h', 'e', 'A', 'Q', 'Y', 'K', 'V', 'K', 'o',
			'g', '3', 'x', 'n', 'E', 'B', 'm' };

	// @formatter:off
	private static final String URL = "jdbc:sqlserver://172.16.1.9;database=D3P;"
			+ "username=burtool;"
			+ "password=" + String.valueOf(PASSWORD);
	// @formatter:on

	private D3ExDatabase() {
	}

	public static Connection createConnection() throws DatabaseException {
		try {
			Class.forName(DRIVER);
			String connectionUrl = URL;
			final Connection conn = DriverManager.getConnection(connectionUrl);
			return conn;
		} catch (ClassNotFoundException ex) {
			throw new D3ExException.DatabaseException("driver not found: " + DRIVER, ex);
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("cannot open database", ex);
		}
	}

}
