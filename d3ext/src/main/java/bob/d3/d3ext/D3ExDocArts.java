package bob.d3.d3ext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import bob.d3.d3ext.D3ExException.DatabaseException;

public class D3ExDocArts extends D3ExDataTable {

	// @formatter:off
	private static final String SQL = "SELECT "
			+ "kue_dokuart, sprache, langtext "
			+ "FROM d3p..dokuart_langtexte "
			+ "WHERE substring(kue_dokuart, 1, 1) != '$' "
			+ "ORDER BY kue_dokuart";
	// @formatter:on

	private static D3ExDocArts _self = null;

	private static Map<String, String> data = new LinkedHashMap<>();

	private D3ExDocArts() throws DatabaseException {
		super("dokuart_langtexte", SQL);
	}

	public static D3ExDocArts getDefault() throws DatabaseException {
		if (null == _self) {
			_self = new D3ExDocArts();
		}
		return _self;
	}

	@Override
	void buildObject(ResultSet rs, StringBuffer logging) throws SQLException {
		String kue_dokuart = rs.getString("kue_dokuart").trim();
		String langtext = rs.getString("langtext").trim();
		logging.append("\t").append(kue_dokuart).append(" = \"").append(langtext).append("\"");
		data.put(kue_dokuart, langtext);
	}

	/**
	 * Liefert den Langtext zur Dokumentenart oder <code>null</code>.
	 * 
	 * @param kue_dokuart
	 * @return eine Zeichenkette oder <code>null</code>
	 */
	public String lookFor(String kue_dokuart) {
		Objects.requireNonNull(kue_dokuart);
		return (data.containsKey(kue_dokuart) ? data.get(kue_dokuart) : null);
	}

}
