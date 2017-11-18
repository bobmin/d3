package bob.d3.d3ext;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bob.d3.d3ext.D3ExException.DatabaseException;
import bob.d3.d3ext.D3ExException.ResourceException;

/**
 * Die Logik innerhalb der D3-Datenbank.
 *  
 * @author m.boettcher@btmx.net
 *
 */
public class D3ExDocFactory {

	/** die Abfrage zu den Dokumenten */
	private final String loopSql;
	
	/** die Datenbankverbindung */
	private Connection conn = null;

	private Statement loopStmt = null;

	/** das Ergebnis zur Abfrage */
	private ResultSet loopRs = null;

	/** die Spaltennamen */
	private Set<String> columnNames = null;

	/** die Dokumentenarten */
	private D3ExDocArts arts = null;

	/** die Eigenschaften pro Dokumentenart */
	private D3ExDocFields fields = null;

	/**
	 * Liefert die Quelle zu den Dokumenten.
	 * 
	 * @param queryResourcePath
	 *            der Pfad zur SQL-Ressource (-Abfrage)
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws ResourceException
	 *             wenn Abfrage nicht gelesen werden kann
	 */
	public static D3ExDocFactory create(final String queryResourcePath) throws ResourceException {
		D3ExResource res = new D3ExResource(queryResourcePath);
		D3ExDocFactory db = new D3ExDocFactory(res.getText());
		return db;
	}

	/**
	 * Geschützter Konstruktor instanziiert die Quelle. Übergeben wird die
	 * Abfrage für die Schleife. Jeder Aufruf innerhalb der Schleife kann als
	 * Dokument interpretiert werden.
	 * 
	 * @param sql
	 *            die Abfrage
	 */
	private D3ExDocFactory(final String sql) {
		this.loopSql = sql;
	}

	public boolean open() throws DatabaseException {
		try {
			arts = D3ExDocArts.getDefault();
			fields = D3ExDocFields.getDefault();

			conn = D3ExDatabase.createConnection();
			loopStmt = conn.createStatement();
			loopRs = loopStmt.executeQuery(loopSql);
			return loopRs.next();
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("cannot open database", ex);
		}
	}
	
	/**
	 * Liefert das Dokument zur aktuellen Position des Zeigers innerhalb der
	 * Schleife (aller Dokumente). Liefert die aktuelle Zeigerposition kein
	 * gültiges Dokument, wird <code>null</code> zurückgegeben.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws DatabaseException
	 *             wenn Dokument nicht erstellt werden kann
	 */
	public D3ExDoc getDoc() throws DatabaseException {
		D3ExDoc doc = null;
		try {
			if (null == columnNames) {
				columnNames = readColumnNames();
			}

			String id = loopRs.getString("doku_id").trim();
			String artShort = loopRs.getString("dokuart").trim();
			long size = loopRs.getLong("size_in_byte");
			String dir = loopRs.getString("logi_verzeichnis").trim();
			String erw = loopRs.getString("datei_erw").trim();
			int nr = loopRs.getInt("doku_nr");

			String artLong = arts.lookFor(artShort);

			doc = new D3ExDoc(id, artShort, artLong, size, dir, erw, nr);

			putDokDatField(doc, 1, 60);
			putDokDatField(doc, 70, 90);

		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("document is corrupt", ex);
		}
		return doc;
	}

	/**
	 * Liefert die Spaltennamen aus den Metadaten der Dokumentenabfrage.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws SQLException
	 *             wenn Probleme mit Metadaten
	 */
	private Set<String> readColumnNames() throws SQLException {
		Set<String> x = new LinkedHashSet<>();
		ResultSetMetaData md = loopRs.getMetaData();
		int count = md.getColumnCount();
		for (int i = 1; i <= count; i++) {
			x.add(md.getColumnName(i));
		}
		return x;
	}

	private void putDokDatField(D3ExDoc doc, int von, int bis) throws SQLException {
		final String dokuart = doc.getArt();
		for (int i = von; i < bis; i++) {
			String fieldName = String.format("dok_dat_feld_%d", i);
			if (columnNames.contains(fieldName)) {
				String value = loopRs.getString(fieldName);
				if (null != value && 0 < value.trim().length()) {
					final D3ExProp prop = new D3ExProp(fieldName, value.trim());
					prop.setLongtext(fields.lookFor(dokuart, i));
					doc.add(prop);
				}
			}
		}
	}

	/**
	 * Versucht den Zeiger auf das nächste Dokument zu setzen. Bei Erfolg wird
	 * <code>true</code> geliefert.
	 * 
	 * @return <code>true</code> wenn Zeiger auf nächstem Dokument steht
	 * @throws DatabaseException
	 *             wenn Probleme mit Dokumentenabfrage
	 */
	public boolean next() throws DatabaseException {
		try {
			return loopRs.next();
		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("database not readable", ex);
		}
	}

	/**
	 * Schließt die Verbindung zu den Dokumenten.
	 */
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

		// --- Datenbankspalten --------------

		private final String doku_id;
		private final String dokuart;
		private final long size_in_byte;
		private final String logi_verzeichnis;
		private final String datei_erw;
		private final int doku_nr;

		// --- ENDE --------------------------

		private final String artLong;

		private final String folder;

		/** die Eigenschaften */
		private List<D3ExProp> props = new LinkedList<>();

		/** die Datei im Filesystem */
		private File file = null;

		private D3ExDoc(String id, String artShort, String artLong, long size, String dir, String erw, int nr)
				throws DatabaseException {
			this.doku_id = id;
			this.dokuart = artShort;
			this.artLong = artLong;
			this.size_in_byte = size;
			this.logi_verzeichnis = dir;
			this.datei_erw = erw;
			this.doku_nr = nr;
			this.folder = computeFolder(id);
		}

		private String computeFolder(String id) throws DatabaseException {
			String x;
			if (8 == id.length()) {
				x = id.substring(0, 4);
			} else if (10 == id.length()) {
				x = id.substring(0, 6);
			} else {
				throw new D3ExException.DatabaseException("format from [id] is unknown: " + id, null);
			}
			return x;
		}

		public String getId() {
			return doku_id;
		}

		public String getFolder() {
			return folder;
		}

		public String getErw() {
			return datei_erw;
		}

		/**
		 * Liefert das Kürzel von der Dokumentenart.
		 * 
		 * @return eine Zeichenkette, niemals <code>null</code>
		 */
		public String getArt() {
			return dokuart;
		}

		/**
		 * Liefert die Dokumentenart ausgeschrieben.
		 * 
		 * @return eine Zeichenkette oder <code>null</code>
		 */
		public String getArtLong() {
			return artLong;
		}

		public long getSize() {
			return size_in_byte;
		}

		public int getDokuNr() {
			return doku_nr;
		}

		private void add(final D3ExProp prop) {
			props.add(prop);
		}

		/**
		 * Liefert die Eigenschaften vom Dokument. Sind keine Eigenschaften
		 * bekannt, wird eine leere Liste geliefert.
		 * 
		 * @return ein Objekt, niemals <code>null</code>
		 */
		public List<D3ExProp> getProps() {
			return props;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public boolean hasAttachment() {
			return (null != file);
		}

		public File getFile() {
			return file;
		}

		@Override
		public String toString() {
			int propsSize = (null == props ? 0 : props.size());
			// @formatter:off
			return String.format("D3ExDoc [doku_id=%s"
					+ ", artShort=%s, artLong=%s"
					+ ", props=%d, nr=%d, bytes=%d, "
					+ "dir=%s, erw=%s, path=%s]"
					, doku_id, dokuart, artLong
					, propsSize
					, doku_nr, size_in_byte
					, logi_verzeichnis, datei_erw
					, (null == file ? "null" : file.getAbsolutePath()));
			// @formatter:on
		}

	}

	/**
	 * Beschreibt eine optionale Dokumenteneigenschaft.
	 */
	public static class D3ExProp {

		/** der Spaltenname */
		private final String columnName;

		/** der Wert */
		private final String value;

		/** die optionale Bezeichnung (abhängig von der Dokumentenart) */
		private String longtext = null;

		public D3ExProp(String columnName, String value) {
			this.columnName = columnName;
			this.value = value;
		}

		/**
		 * Setzt einen Bezeichner.
		 * 
		 * @param longtext
		 *            der neue Langtext
		 */
		public void setLongtext(final String longtext) {
			this.longtext = longtext;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getValue() {
			return value;
		}

		public String getLongtext() {
			return longtext;
		}

		public boolean hasLongtext() {
			return (null != longtext);
		}

		@Override
		public String toString() {
			// @formatter:off
			return String.format("D3ExProp [columnName=\"%s\", value=\"%s\""
					+ ", longtext=\"%s\"]"
					, columnName, value, longtext);
			// @formatter:on
		}

	}

}
