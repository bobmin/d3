package bob.d3.gadget;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import bob.d3.ConsoleUtil;
import bob.d3.D3ExException.ResourceException;
import bob.d3.MemoryConnectionDriver;
import bob.d3.ResourceFile;
import bob.d3.export.D3ConnectionDriver;

public class TwoDatesGadget {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(TwoDatesGadget.class.getName());

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ResourceException {
		if (!(1 == args.length)) {
			System.out.println("usage: java -cp ... bob.d3.export.TwoDatesGadget <path>");
			System.exit(-1);
		}

		final String exportPath = args[0];
		ConsoleUtil.log("Program starts...\n\texport path = " + exportPath);

		Connection d3conn = null;
		PreparedStatement d3stmt = null;
		ResultSet d3rs = null;

		Connection memConn = null;
		Statement memSelectStmt = null;
		ResultSet memRs = null;

		PreparedStatement memUpdateStmt = null;

		try {
			MemoryConnectionDriver driver = MemoryConnectionDriver.create(new File(exportPath, "d3exdb"));
			memConn = driver.createConnection();

			d3conn = D3ConnectionDriver.createConnection();
			ResourceFile res = new ResourceFile("/query_two_dates.sql");
			d3stmt = d3conn.prepareStatement(res.getText());

			// memConn.createStatement()
			// .executeUpdate("ALTER TABLE document ADD COLUMN IF NOT EXISTS
			// doc_einbring_datum DATE");
			// log("column \"doc_einbring_datum\" created");

			// memConn.createStatement()
			// .executeUpdate("ALTER TABLE document ADD COLUMN IF NOT EXISTS
			// doc_sterbe_datum DATE");
			// log("column \"doc_sterbe_datum\" created");

			memSelectStmt = memConn.createStatement();
			memRs = memSelectStmt.executeQuery("SELECT doc_id FROM document WHERE doc_einbring_datum IS NULL");

			memUpdateStmt = memConn.prepareStatement(
					"UPDATE document SET doc_einbring_datum = ?, doc_sterbe_datum = ? WHERE doc_id = ?");

			int count = 0;

			while (memRs.next()) {
				final String id = memRs.getString("doc_id");
				d3stmt.setString(1, id);
				d3stmt.setString(2, id);
				d3rs = d3stmt.executeQuery();
				if (d3rs.next()) {
					String name = null;
					Date einbring = null;
					Date sterbe = null;
					// Werte holen
					do {
						name = d3rs.getString("name");
						final Date datum = d3rs.getDate("datum");
						if ("datum_einbring".equals(name)) {
							einbring = datum;
						} else if ("sterbe_datum".equals(name)) {
							sterbe = datum;
						}
					} while (d3rs.next());
					// Werte schreiben
					if (null != einbring || null != sterbe) {
						if (null != einbring) {
							memUpdateStmt.setDate(1, new java.sql.Date(einbring.getTime()));
						} else {
							memUpdateStmt.setNull(1, java.sql.Types.DATE);
						}
						if (null != sterbe) {
							memUpdateStmt.setDate(2, new java.sql.Date(sterbe.getTime()));
						} else {
							memUpdateStmt.setNull(2, java.sql.Types.DATE);
						}
						memUpdateStmt.setString(3, id);
						memUpdateStmt.executeUpdate();
						memUpdateStmt.clearParameters();
					}
				} // memRs.next()
				d3stmt.clearParameters();
				++count;
				if ((count % 500) == 0) {
					ConsoleUtil.log(String.format("(%d) %s...", count, id));
				}
			}

		} finally {
			close(memUpdateStmt);
			close(memRs);
			close(memSelectStmt);
			close(memConn);
			close(d3rs);
			close(d3stmt);
			close(d3conn);
		}

		ConsoleUtil.log("Program finished. Bye!");

	}

	private static void close(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException ex) {
				LOG.log(Level.WARNING, "cannot close", ex);
			}
		}
	}

	private static void close(Statement stmt) {
		if (null != stmt) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				LOG.log(Level.WARNING, "cannot close", ex);
			}
		}
	}

	private static void close(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException ex) {
				LOG.log(Level.WARNING, "cannot close", ex);
			}
		}
	}

}
