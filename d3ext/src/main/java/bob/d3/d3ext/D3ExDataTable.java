package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import bob.d3.d3ext.D3ExException.DatabaseException;

public abstract class D3ExDataTable {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(D3ExDocArts.class.getName());

	public D3ExDataTable(final String tablename, final String sql) throws DatabaseException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = D3ExDatabase.createConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			StringBuffer logging = new StringBuffer();
			while (rs.next()) {
				if (0 == logging.length()) {
					logging.append("table \"").append(tablename).append("\" were loaded:\r\n");
				} else {
					logging.append("\r\n");
				}
				buildObject(rs, logging);
			}
			LOG.info(logging.toString());
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("table \"dokuart_langtexte\" not readable", ex);
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException ex) {
					LOG.warning("[rs] not closable: " + ex.getMessage());
				}
			}
			if (null != stmt) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					LOG.warning("[stmt] not closable: " + ex.getMessage());
				}
			}
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException ex) {
					LOG.warning("[conn] not closable: " + ex.getMessage());
				}
			}
		}
	}

	abstract void buildObject(ResultSet rs, StringBuffer logging) throws SQLException;

}
