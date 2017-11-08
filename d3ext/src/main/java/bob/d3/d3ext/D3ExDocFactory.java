package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bob.d3.d3ext.D3ExException.DatabaseException;
import bob.d3.d3ext.D3ExException.ResourceException;

/**
 * Die Logik innerhalb der D3-Datenbank.
 *  
 * @author m.boettcher@btmx.net
 *
 */
public class D3ExDocFactory {

	private final String loopSql;
	
	private Connection conn = null;

	private Statement loopStmt = null;

	private ResultSet loopRs = null;

	private D3ExDocFactory(final String sql) {
		this.loopSql = sql;
	}

	public static D3ExDocFactory create() throws ResourceException {
		D3ExResource res = new D3ExResource("/query_docs_all.sql");
		D3ExDocFactory db = new D3ExDocFactory(res.getText());
		return db;
	}

	public boolean open() throws DatabaseException {
		try {
			conn = D3ExDatabase.createConnection();
			loopStmt = conn.createStatement();
			loopRs = loopStmt.executeQuery(loopSql);
			return loopRs.next();
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("cannot open database", ex);
		}
	}
	
	public D3ExDoc getDoc() throws DatabaseException {
		try {
			D3ExDoc doc = null;

			int columnCount = 0;
			List<String> columnNames = readColumnNames(loopRs);

			String id = null, art = null, dir = null, erw = null;
			int nr = -1;
			long size = -1;

			if (columnNames.contains("doku_id")) {
				id = loopRs.getString("doku_id").trim();
				columnCount++;
			}
			if (columnNames.contains("dokuart")) {
				art = loopRs.getString("dokuart");
				columnCount++;
			}
			if (columnNames.contains("size_in_byte")) {
				size = loopRs.getLong("size_in_byte");
				columnCount++;
			}
			if (columnNames.contains("logi_verzeichnis")) {
				dir = loopRs.getString("logi_verzeichnis").trim();
				columnCount++;
			}
			if (columnNames.contains("datei_erw")) {
				erw = loopRs.getString("datei_erw").trim();
				columnCount++;
			}
			if (columnNames.contains("doku_nr")) {
				nr = loopRs.getInt("doku_nr");
				columnCount++;
			}

			if (6 == columnCount) {
				doc = new D3ExDoc(id, art, size, dir, erw, nr);
				putDokDatField(doc, columnNames, 1, 60);
				putDokDatField(doc, columnNames, 70, 90);
			}

			return doc;
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("document is corrupt", ex);
		}
	}

	private List<String> readColumnNames(ResultSet loopRs2) throws SQLException {
		List<String> x = new LinkedList<>();
		ResultSetMetaData md = loopRs.getMetaData();
		int count = md.getColumnCount();
		for (int i = 1; i <= count; i++) {
			x.add(md.getColumnName(i));
		}
		return x;
	}

	private void putDokDatField(D3ExDoc doc, List<String> columnNames, int von, int bis) throws SQLException {
		for (int i = von; i < bis; i++) {
			String fieldName = String.format("dok_dat_feld_%d", i);
			if (columnNames.contains(fieldName)) {
				String value = loopRs.getString(fieldName);
				if (null != value && 0 < value.trim().length()) {
					doc.put(fieldName, value);
				}
			}
		}
	}

	public boolean next() throws DatabaseException {
		try {
			return loopRs.next();
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("database not readable", ex);
		}
	}

	public void close() {
		if (null != loopRs) {
			try {
				loopRs.close();
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
		if (null != conn) {
			try {
				conn.close();
			} catch (SQLException ex) {
				// ignored
			}
		}
	}

	public static class D3ExDoc {

		private final String doku_id;

		private final String dokuart;

		private Map<String, String> props = null;

		private final long size_in_byte;

		private final String logi_verzeichnis;

		private final String datei_erw;

		private final int doku_nr;

		public D3ExDoc(String id, String art, long size, String dir, String erw, int nr) {
			this.doku_id = id;
			this.dokuart = art;
			this.size_in_byte = size;
			this.logi_verzeichnis = dir;
			this.datei_erw = erw;
			this.doku_nr = nr;
		}

		public String getId() {
			return doku_id;
		}

		private void put(final String columnName, final String value) {
			if (null == props) {
				props = new LinkedHashMap<>();
			}
			props.put(columnName, value);
		}

		@Override
		public String toString() {
			int propsSize = (null == props ? 0 : props.size());
			// @formatter:off
			return String.format("D3ExDoc [doku_id=%s, art=%s"
					+ ", props=%d, nr=%d, bytes=%d, "
					+ "dir=%s, erw=%s]"
					, doku_id, dokuart
					, propsSize
					, doku_nr, size_in_byte
					, logi_verzeichnis, datei_erw);
			// @formatter:on
		}

	}

}
