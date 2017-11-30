package bob.d3.export;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

import bob.d3.Document;
import bob.d3.MemoryConnectionDriver;
import bob.d3.Property;
import bob.h2db.DatabaseManager;
import bob.h2db.DatabaseManager.ModifyCmd;

public class MemoryWriter {

	private static final Logger LOG = Logger.getLogger(MemoryWriter.class.getName());

	private static final String[] TABLES = new String[] { "document", "property" };

	// private final MemoryConnectionDriver mem;

	private DatabaseManager mgr;

	public MemoryWriter(final File folder) throws ClassNotFoundException, SQLException {
		boolean exists = folder.exists();
		if (!exists) {
			folder.mkdirs();
		}
		MemoryConnectionDriver mem = MemoryConnectionDriver.create(folder);
		this.mgr = new DatabaseManager(mem, TABLES);
		LOG.info(String.format("memory initialized, url=%s, created=%s", mem.getUrl(), !exists));
	}

	public void saveFile(String id, Date einbring, String art, int nr, String dir, String name, String ext, long bytes,
			Date sterbe)
			throws ClassNotFoundException, SQLException {
		ModifyCmd cmd = new ModifyCmd() {

			@Override
			public String getDml() {
				// @formatter:off
				return "INSERT INTO document (doc_id"
						+ ", doc_einbring_datum"
						+ ", doc_doku_art"
						+ ", doc_nummer"
						+ ", doc_folder"
						+ ", doc_name"
						+ ", doc_ext"
						+ ", doc_bytes"
						+ ", doc_sterbe_datum) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				// @formatter:on
			}

			@Override
			public void fill(PreparedStatement stmt) throws SQLException {
				int col = 0;
				stmt.setString(++col, id);
				if (null != einbring) {
					stmt.setDate(++col, new java.sql.Date(einbring.getTime()));
				} else {
					stmt.setNull(++col, java.sql.Types.DATE);
				}
				stmt.setString(++col, art);
				stmt.setInt(++col, nr);
				stmt.setString(++col, dir);
				if (null != name) {
					stmt.setString(++col, name);
				} else {
					stmt.setNull(++col, java.sql.Types.VARCHAR);
				}
				stmt.setString(++col, ext);
				stmt.setLong(++col, bytes);
				if (null != sterbe) {
					stmt.setDate(++col, new java.sql.Date(sterbe.getTime()));
				} else {
					stmt.setNull(++col, java.sql.Types.DATE);
				}
			}

			@Override
			public String getSuccessMessage(int code) {
				return String.format("file with id %s inserted", id);
			}

			@Override
			public String getErrorMessage() {
				return String.format("cannot insert file with id %s", id);
			}

		};
		mgr.runDml(cmd);
	}

	public void saveProp(String id, String column, String label, String value)
			throws ClassNotFoundException, SQLException {
		ModifyCmd cmd = new ModifyCmd() {

			@Override
			public String getDml() {
				// @formatter:off
				return "INSERT INTO property (prop_doc_id"
						+ ", prop_column"
						+ ", prop_longtext"
						+ ", prop_value) "
						+ "VALUES (?, ?, ?, ?)";
				// @formatter:on
			}

			@Override
			public void fill(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, id);
				stmt.setString(2, column);
				if (null != label) {
					stmt.setString(3, label);
				} else {
					stmt.setNull(3, java.sql.Types.VARCHAR);
				}
				stmt.setString(4, value);
			}

			@Override
			public String getSuccessMessage(int code) {
				return String.format("prop inserted, id=%s, value=%s, column=%s, label=%s", id, value, column, label);
			}

			@Override
			public String getErrorMessage() {
				return String.format("cannot insert prop %s with id %s", column, id);
			}

		};
		mgr.runDml(cmd);
	}

	public void pull(Document doc) throws ClassNotFoundException, SQLException {
		String id = doc.getId();

		File f = doc.getFile();
		String name = (null == f ? null : f.getName());
		saveFile(id, doc.getEinbring(), doc.getArt(), doc.getDokuNr(), doc.getFolder(), name, doc.getErw(),
				doc.getSize(), doc.getSterbe());

		for (Property p : doc.getProps()) {
			saveProp(id, p.getColumnName(), p.getLongtext(), p.getValue());
		}
	}

	public void close() {
		mgr.close();
	}


}
