package bob.d3.export;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
		if (!folder.exists()) {
			folder.mkdirs();
			LOG.fine(String.format("path created: %s", folder.getAbsolutePath()));
		}
		MemoryConnectionDriver mem = MemoryConnectionDriver.getDefault(folder);
		this.mgr = new DatabaseManager(mem, TABLES);
	}

	public void saveFile(String id, String art, int nr, String dir, String name, String ext, long bytes)
			throws ClassNotFoundException, SQLException {
		ModifyCmd cmd = new ModifyCmd() {

			@Override
			public String getDml() {
				// @formatter:off
				return "INSERT INTO document (doc_id"
						+ ", doc_doku_art"
						+ ", doc_nummer"
						+ ", doc_folder"
						+ ", doc_name"
						+ ", doc_ext"
						+ ", doc_bytes) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
				// @formatter:on
			}

			@Override
			public void fill(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, id);
				stmt.setString(2, art);
				stmt.setInt(3, nr);
				stmt.setString(4, dir);
				if (null != name) {
					stmt.setString(5, name);
				} else {
					stmt.setNull(5, java.sql.Types.VARCHAR);
				}
				stmt.setString(6, ext);
				stmt.setLong(7, bytes);
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
				return String.format("prop %s for id %s inserted", column, id);
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
		saveFile(id, doc.getArt(), doc.getDokuNr(), doc.getFolder(), name, doc.getErw(), doc.getSize());

		for (Property p : doc.getProps()) {
			saveProp(id, p.getColumnName(), p.getLongtext(), p.getValue());
		}
	}

	public void close() {
		mgr.close();
	}


}
