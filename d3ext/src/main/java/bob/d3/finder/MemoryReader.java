package bob.d3.finder;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Logger;

import bob.d3.Document;
import bob.d3.MemoryConnectionDriver;
import bob.d3.Property;
import bob.h2db.ConnectionDriver;

public class MemoryReader {

	private static final Logger LOG = Logger.getLogger(MemoryReader.class.getName());

	private static final MemoryArtsCache ARTS = MemoryArtsCache.getDefault();

	private final ConnectionDriver driver;

	/** die Datenbankverbindung */
	private Connection conn = null;

	private Statement loopStmt = null;

	private PreparedStatement propStmt = null;

	/** das Ergebnis zur Abfrage */
	private ResultSet rs = null;

	public MemoryReader(final File folder) throws ClassNotFoundException, SQLException {
		driver = MemoryConnectionDriver.create(folder);
		LOG.info(String.format("memory initialized, url=%s", driver.getUrl()));
	}

	public boolean open(final String sql) throws SQLException, ClassNotFoundException {
		conn = driver.createConnection();
		loopStmt = conn.createStatement();
		propStmt = conn.prepareStatement("SELECT * FROM property WHERE prop_doc_id = ?");
		rs = loopStmt.executeQuery(sql);
		return rs.next();
	}

	public Document getDoc() throws SQLException {
		final String id = rs.getString("doc_id");
		final String artShort = rs.getString("doc_doku_art");
		final long size = rs.getLong("doc_bytes");
		final String dir = rs.getString("doc_folder");
		final String name = rs.getString("doc_name");
		final String erw = rs.getString("doc_ext");
		final int nr = rs.getInt("doc_nummer");
		final Date einbring = rs.getDate("doc_einbring_datum");
		final Date sterbe = rs.getDate("doc_sterbe_datum");
		Document doc = new Document(id, einbring, artShort, size, dir, erw, nr, sterbe);
		// +File
		if (null != name) {
			doc.setFile(new File(name));
		}
		// +Art
		doc.setArtLong(ARTS.lookFor(artShort));
		// +Properties
		propStmt.setString(1, id);
		final ResultSet propRs = propStmt.executeQuery();
		while (propRs.next()) {
			final String columnName = propRs.getString("prop_column");
			final String value = propRs.getString("prop_value");
			Property p = new Property(columnName, value);
			p.setLongtext(propRs.getString("prop_longtext"));
			doc.add(p);
		}
		propStmt.clearParameters();
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
		if (null != loopStmt) {
			try {
				loopStmt.close();
			} catch (SQLException ex) {
				// ignored
			}
		}
		if (null != propStmt) {
			try {
				propStmt.close();
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
