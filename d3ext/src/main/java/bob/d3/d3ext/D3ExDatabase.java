package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDatabase {

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/** die lokale Benutzerkonfiguration */
	private static final D3ExConfig CONFIG = D3ExConfig.getDefault();

	// @formatter:off
	private static final String URL = "jdbc:sqlserver://172.16.1.9;database=D3P;"
			+ "username=" + CONFIG.getProperty(D3ExConfig.DATABASE_USERNAME) + ";"
			+ "password=" + CONFIG.getProperty(D3ExConfig.DATABASE_PASSWORD);
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
