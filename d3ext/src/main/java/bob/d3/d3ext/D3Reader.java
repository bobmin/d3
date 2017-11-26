package bob.d3.d3ext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import bob.d3.D3ExException;
import bob.d3.Document;
import bob.d3.Property;
import bob.d3.ResourceFile;
import bob.d3.D3ExException.DatabaseException;
import bob.d3.D3ExException.ResourceException;

public class D3Reader {

	/** die Spaltennamen */
	private Set<String> columnNames = null;

	/** die Dokumentenarten */
	private DocumentArts arts = null;

	/** die Eigenschaften pro Dokumentenart */
	private DocumentFields fields = null;

	/** die Abfrage zu den Dokumenten */
	private final String loopSql;

	/** die Datenbankverbindung */
	private Connection conn = null;

	private Statement loopStmt = null;

	/** das Ergebnis zur Abfrage */
	private ResultSet loopRs = null;

	/**
	 * Instanziiert die Quelle für die Dokumenten.
	 * 
	 * @param queryResourcePath
	 *            der Pfad zur SQL-Ressource (-Abfrage)
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws DatabaseException
	 * @throws ResourceException
	 *             wenn Abfrage nicht gelesen werden kann
	 */
	public D3Reader(final String path) throws DatabaseException, ResourceException {
		ResourceFile res = new ResourceFile(path);
		this.loopSql = res.getText();
		arts = DocumentArts.getDefault();
		fields = DocumentFields.getDefault();
	}

	public boolean open() throws DatabaseException {
		try {
			conn = D3ConnectionDriver.createConnection();
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
	public Document getDoc() throws DatabaseException {
		Document doc = null;
		try {
			String id = loopRs.getString("doku_id").trim();
			String artShort = loopRs.getString("dokuart").trim();
			long size = loopRs.getLong("size_in_byte");
			String dir = loopRs.getString("logi_verzeichnis").trim();
			String erw = loopRs.getString("datei_erw").trim();
			int nr = loopRs.getInt("doku_nr");

			doc = new Document(id, artShort, size, dir, erw, nr);

			setupDocument(doc, loopRs);

		} catch (SQLException ex) {
			throw new D3ExException.DatabaseException("document is corrupt", ex);
		}
		return doc;
	}

	protected void setupDocument(final Document doc, final ResultSet rs) throws SQLException {
		String artLong = arts.lookFor(doc.getArt());
		doc.setArtLong(artLong);

		putDokDatField(doc, rs, 1, 60);
		putDokDatField(doc, rs, 70, 90);

	}

	/**
	 * Liefert die Spaltennamen aus den Metadaten der Dokumentenabfrage.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws SQLException
	 *             wenn Probleme mit Metadaten
	 */
	private Set<String> readColumnNames(final ResultSet rs) throws SQLException {
		Set<String> x = new LinkedHashSet<>();
		ResultSetMetaData md = rs.getMetaData();
		int count = md.getColumnCount();
		for (int i = 1; i <= count; i++) {
			x.add(md.getColumnName(i));
		}
		return x;
	}

	private void putDokDatField(Document doc, final ResultSet rs, int von, int bis) throws SQLException {
		if (null == columnNames) {
			columnNames = readColumnNames(rs);
		}
		final String dokuart = doc.getArt();
		for (int i = von; i < bis; i++) {
			String fieldName = String.format("dok_dat_feld_%d", i);
			if (columnNames.contains(fieldName)) {
				String value = rs.getString(fieldName);
				if (null != value && 0 < value.trim().length()) {
					final Property prop = new Property(fieldName, value.trim());
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

}
