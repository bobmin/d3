package bob.d3.finder;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import bob.d3.Document;
import bob.d3.MemoryConnectionDriver;
import bob.h2db.ConnectionDriver;

public class MemoryReader {

	private static final Logger LOG = Logger.getLogger(MemoryReader.class.getName());

	private final ConnectionDriver driver;

	/** die Datenbankverbindung */
	private Connection conn = null;

	private Statement stmt = null;

	/** das Ergebnis zur Abfrage */
	private ResultSet rs = null;

	public MemoryReader(final File folder) throws ClassNotFoundException, SQLException {
		driver = MemoryConnectionDriver.getDefault(folder);
	}

	public boolean open(final String sql) throws SQLException, ClassNotFoundException {
		conn = driver.createConnection();
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		return rs.next();
	}

	public Document getDoc() throws SQLException {
		final String id = rs.getString("doc_id");
		final String artShort = rs.getString("doc_doku_art");
		final long size = rs.getLong("doc_bytes");
		final String dir = rs.getString("doc_folder");
		final String erw = rs.getString("doc_ext");
		final int nr = rs.getInt("doc_nummer");
		final Document doc = new Document(id, artShort, size, dir, erw, nr);
		return doc;
	}

	public boolean next() throws SQLException {
		return rs.next();
	}

	public void close() {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException ex) {
				// ignored
			}
		}
		if (null != stmt) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				// ignored
			}
		}
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException ex) {
				// ignored
			}
		}
	}

}
