package bob.d3.finder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Übersetzt eine Suchanfrage in eine passende SQL-Abfrage.
 * 
 * @author maik@btmx.net
 *
 */
public class SqlGenerator {

	static final String START = "SELECT * FROM document INNER JOIN property ON doc_id = prop_doc_id";

	static final String TAIL = "LIMIT 0, 100";

	private List<String> idValues = null;

	private List<String> extValues = null;

	private String knrValue = null;

	private String lnrValue = null;

	public SqlGenerator(final String input) {
		Objects.requireNonNull(input);
		if (-1 < input.indexOf("id = ")) {
			idValues = new LinkedList<>();
			idValues.addAll(lookFor(input, "id = ([A-Z0-9]+)"));
		}
		if (-1 < input.indexOf("ext = ")) {
			extValues = new LinkedList<>();
			extValues.addAll(lookFor(input, "ext = ([A-Z]+)"));
		}
		if (-1 < input.indexOf("knr = ")) {
			final List<String> values = lookFor(input, "knr = ([0-9]+)");
			knrValue = (0 == values.size() ? null : values.get(0));
		}
		if (-1 < input.indexOf("lnr = ")) {
			final List<String> values = lookFor(input, "lnr = ([0-9]+)");
			lnrValue = (0 == values.size() ? null : values.get(0));
		}
	}

	private List<String> lookFor(String input, String token) {
		List<String> x = new LinkedList<>();
		Pattern p = Pattern.compile(token);
		Matcher m = p.matcher(input);
		while (m.find()) {
			final String value = m.group(1);
			x.add(value);
		}
		return x;
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

	@Override
	public String toString() {
		return "SqlGenerator [idValues=" + idValues + ", extValues=" + extValues + ", knrValue=" + knrValue
				+ ", lnrValue=" + lnrValue + "]";
	}

}
