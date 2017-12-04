package bob.d3.finder;

import java.util.List;

/**
 * Übersetzt eine Suchanfrage in eine passende SQL-Abfrage.
 * 
 * @author maik@btmx.net
 *
 */
public class SqlQuery extends AbstractQuery {

	static final String START = "SELECT * FROM document INNER JOIN property ON doc_id = prop_doc_id";

	static final String TAIL = "LIMIT 0, 100";

	public SqlQuery(final String input) {
		super(input);
	}

	@Override
	public String getCommand() {
		StringBuffer sb = new StringBuffer(START);
		sb.append(" ");

		int count = 0;
		count += appendList(sb, "doc_id", idValues, count);
		count += appendList(sb, "doc_ext", extValues, count);
		count += appendProp(sb, "Kunden-Nr.", knrValues, count);
		count += appendProp(sb, "Lieferanten-Nr.", lnrValues, count);

		sb.append(TAIL);
		return sb.toString();
	}

	private int appendList(StringBuffer sb, String name, List<String> list, int count) {
		if (null != list) {
			if (0 == count) {
				sb.append("WHERE ");
			} else {
				sb.append("AND ");
			}
			sb.append(name);
			sb.append(" IN (");
			for (int idx = 0; idx < list.size(); idx++) {
				final String ext = list.get(idx);
				if (0 < idx) {
					sb.append(", ");
				}
				sb.append("'").append(ext).append("'");
			}
			sb.append(") ");
		}
		return (null == list ? 0 : list.size());
	}

	private int appendProp(StringBuffer sb, String name, List<String> values, int count) {
		int match = count;
		if (null != values && 0 < values.size()) {
			for (String v : values) {
				if (0 == match) {
					sb.append("WHERE ");
				} else {
					sb.append("AND ");
				}
				sb.append("prop_longtext = '").append(name).append("'");
				sb.append(" AND ");
				sb.append("prop_value = '").append(v).append("'");
				sb.append(" ");
			}
			match++;
		}
		return (count - match);
	}

}
