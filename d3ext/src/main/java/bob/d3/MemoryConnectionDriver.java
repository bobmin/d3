package bob.d3;

import java.io.File;
import java.sql.SQLException;

import bob.h2db.H2ConnectionDriver;

/**
 * Beschreibt die lokale Sicherung von relevanten Eigenschaften der eingelesenen
 * Dokumente.
 * 
 * @author maik@btmx.net
 *
 */
public class MemoryConnectionDriver extends H2ConnectionDriver {

	private static MemoryConnectionDriver _self = null;

	public static MemoryConnectionDriver getDefault(final File folder) throws ClassNotFoundException, SQLException {
		if (null == _self) {
			// Datenbankpfad
			File path = new File(folder, "d3exdb");
			// Benutzerdaten
			D3Config cfg = D3Config.getDefault();
			final String user = cfg.getProperty("D3ExLocalDatabase.username");
			final String pass = cfg.getProperty("D3ExLocalDatabase.password");
			// Driver
			_self = new MemoryConnectionDriver(path, user, pass);
		}
		return _self;
	}

	private MemoryConnectionDriver(final File path, String user, String pass)
			throws ClassNotFoundException, SQLException {
		super(path, user, pass);
	}

}
