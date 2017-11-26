package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bob.d3.D3Config;
import bob.d3.D3ExException;
import bob.d3.D3ExException.DatabaseException;

public class D3ConnectionDriver {

	/** die lokale Benutzerkonfiguration */
	private static final D3Config CONFIG = D3Config.getDefault();

	// @formatter:off
	private static final String URL = "jdbc:sqlserver://172.16.1.9;database=D3P;"
			+ "username=" + CONFIG.getProperty(D3Config.DATABASE_USERNAME) + ";"
			+ "password=" + CONFIG.getProperty(D3Config.DATABASE_PASSWORD);
	// @formatter:on

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

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
