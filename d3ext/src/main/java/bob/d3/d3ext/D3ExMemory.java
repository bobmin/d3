package bob.d3.d3ext;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bob.d3.d3ext.D3ExDocFactory.D3ExDoc;
import bob.d3.d3ext.D3ExDocFactory.D3ExProp;
import bob.d3.d3ext.D3ExException.LocalDatabaseException;
import bob.h2db.Database;
import bob.h2db.Database.ModifyCmd;

public class D3ExMemory {

	private final Database db;

	private D3ExMemory(Database db) {
		this.db = db;
	}

	public static D3ExMemory getPath(final String root) throws LocalDatabaseException {
		// Access
		D3ExConfig cfg = D3ExConfig.getDefault();
		String user = cfg.getProperty("D3ExLocalDatabase.username");
		String pass = cfg.getProperty("D3ExLocalDatabase.password");
		// Path
		File path = new File(root, "d3exdb");
		// Tables
		String[] tables = new String[] { "document", "property" };
		try {
			Database db = new Database(user, pass, path, "d3exdb", tables);
			D3ExMemory loc = new D3ExMemory(db);
			return loc;
		} catch (ClassNotFoundException | SQLException ex) {
			throw new D3ExException.LocalDatabaseException("local database corrupt", ex);
		}
	}

	public void saveFile(String id, String art, int nr, String dir, String name, String ext, long bytes)
			throws LocalDatabaseException {
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
		run(cmd);
	}

	public void saveProp(String id, String column, String label, String value) throws LocalDatabaseException {
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
		run(cmd);
	}

	private void run(ModifyCmd cmd) throws LocalDatabaseException {
		try {
			db.runDml(cmd);
		} catch (ClassNotFoundException | SQLException ex) {
			throw new LocalDatabaseException(cmd.getErrorMessage(), ex);
		}
	}

	public void pull(D3ExDoc doc) throws LocalDatabaseException {
		String id = doc.getId();

		File f = doc.getFile();
		String name = (null == f ? null : f.getName());
		saveFile(id, doc.getArt(), doc.getDokuNr(), doc.getFolder(), name, doc.getErw(), doc.getSize());

		for (D3ExProp p : doc.getProps()) {
			saveProp(id, p.getColumnName(), p.getLongtext(), p.getValue());
		}
	}

}
