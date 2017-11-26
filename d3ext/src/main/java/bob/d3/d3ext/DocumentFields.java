package bob.d3.d3ext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bob.d3.D3ExException.DatabaseException;

/**
 * Felder zu Repository:
 *
 * SELECT TOP 1000 [repository_id] ,[repository_text] ,[doc_field_nr_pref]
 * ,[data_type] ,[data_length] ,[min_value] ,[max_value] ,[predefined_values]
 * ,[predef_val_id] ,[predef_val_look_tab] ,[predef_val_look_col]
 * ,[hook_plausi_func] ,[hook_get_value_func] ,[regular_expression] ,[flag_feld]
 * FROM [D3P].[dbo].[field_repository] WHERE substring(repository_text, 1, 1) !=
 * '$' order by [repository_id], doc_field_nr_pref
 * 
 * @author maik@btmx.net
 *
 */
public class DocumentFields extends AbstractDataTable {

	// @formatter:off
	private static final String SQL = "SELECT "
			+ "kue_dokuart, dok_dat_feld_nr, sprache_fispe_titel, "
			+ "dok_dat_titel, werte_auswahl, daten_typ, laenge, repository_id "
			+ "FROM d3p..fispe_titel_dokuart "
			+ "WHERE substring(kue_dokuart, 1, 1) != '$' "
			+ "ORDER BY kue_dokuart, dok_dat_titel";
	// @formatter:on

	private static DocumentFields _self = null;

	private static Map<String, List<DocField>> data = null;

	private DocumentFields() throws DatabaseException {
		super("fispe_titel_dokuart", SQL);
	}

	public static DocumentFields getDefault() throws DatabaseException {
		if (null == _self) {
			_self = new DocumentFields();
		}
		return _self;
	}

	@Override
	void buildObject(ResultSet rs, StringBuffer logging) throws SQLException {
		if (null == data) {
			data = new LinkedHashMap<>();
		}

		String kue_dokuart = rs.getString("kue_dokuart").trim();

		if (!data.containsKey(kue_dokuart)) {
			data.put(kue_dokuart, new LinkedList<>());
		}

		int dok_dat_feld_nr = rs.getInt("dok_dat_feld_nr");
		String dok_dat_titel = rs.getString("dok_dat_titel").trim();
		boolean werte_auswahl = rs.getBoolean("werte_auswahl");
		String daten_typ = rs.getString("daten_typ");
		int laenge = rs.getInt("laenge");
		int repository_id = rs.getInt("repository_id");

		data.get(kue_dokuart).add(new DocField(kue_dokuart, dok_dat_feld_nr, dok_dat_titel, werte_auswahl, daten_typ,
				laenge, repository_id));

		logging.append("\t").append(kue_dokuart);

		String dokuartName = null;
		try {
			DocumentArts arts = DocumentArts.getDefault();
			dokuartName = arts.lookFor(kue_dokuart);
		} catch (DatabaseException ex) {
			throw new RuntimeException(ex);
		}
		logging.append(" [").append(dokuartName).append("]");

		logging.append(" + ").append(dok_dat_titel).append(" (").append(dok_dat_feld_nr).append(")");

	}

	public String lookFor(String dokuart, int fieldNumber) {
		String x = null;
		loop: if (data.containsKey(dokuart)) {
			for (DocField f : data.get(dokuart)) {
				if (fieldNumber == f.dok_dat_feld_nr) {
					x = f.dok_dat_titel;
					break loop;
				}
			}
		}
		return x;
	}

	public static class DocField {

		private final String kue_dokuart;

		private final int dok_dat_feld_nr;

		private final String dok_dat_titel;

		private final boolean werte_auswahl;

		private final String daten_typ;

		private final int laenge;

		private final int repository_id;

		public DocField(String kue_dokuart, int dok_dat_feld_nr, String dok_dat_titel, boolean werte_auswahl,
				String daten_typ, int laenge, int repository_id) {
			this.kue_dokuart = kue_dokuart;
			this.dok_dat_feld_nr = dok_dat_feld_nr;
			this.dok_dat_titel = dok_dat_titel;
			this.werte_auswahl = werte_auswahl;
			this.daten_typ = daten_typ;
			this.laenge = laenge;
			this.repository_id = repository_id;
		}

	}

}
