package bob.d3.export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bob.d3.D3Config;

public class D3ConnectionDriver {

	/** die lokale Benutzerkonfiguration */
	private static final D3Config CONFIG = D3Config.getDefault();

	// @formatter:off
	private static final String URL = "jdbc:sqlserver://172.16.1.9;database=D3P;"
			+ "username=" + CONFIG.getProperty(D3Config.DATABASE_USERNAME) + ";"
			+ "password=" + CONFIG.getProperty(D3Config.DATABASE_PASSWORD);
	// @formatter:on

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static Connection createConnection() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		String connectionUrl = URL;
		final Connection conn = DriverManager.getConnection(connectionUrl);
		return conn;
	}

}
