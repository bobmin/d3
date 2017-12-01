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

	public String getCommand() {
		StringBuffer sb = new StringBuffer(START);
		sb.append(" ");

		int count = 0;
		count += append(sb, "doc_id", idValues, count);
		count += append(sb, "doc_ext", extValues, count);
		count += append(sb, "Kunden-Nr.", knrValue, count);
		count += append(sb, "Lieferanten-Nr.", lnrValue, count);

		sb.append(TAIL);
		return sb.toString();
	}

	private int append(StringBuffer sb, String name, List<String> list, int count) {
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

	private int append(StringBuffer sb, String name, String value, int count) {
		if (null != value) {
			if (0 == count) {
				sb.append("WHERE ");
			} else {
				sb.append("AND ");
			}
			sb.append("prop_longtext = '").append(name).append("'");
			sb.append(" AND ");
			sb.append("prop_value = '").append(value).append("'");
			sb.append(" ");
		}
		return (null == value ? 0 : 1);
	}

}
