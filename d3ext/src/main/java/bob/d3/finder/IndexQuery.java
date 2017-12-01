package bob.d3.finder;

import java.util.List;

/**
 * Übersetzt eine Suchanfrage in eine passende Index-Abfrage.
 * 
 * @author maikboettcher
 *
 * @see https://lucene.apache.org/core/2_9_4/queryparsersyntax.html
 *
 */
public class IndexQuery extends AbstractQuery {

	public IndexQuery(final String input) {
		super(input);
	}

	@Override
	public String getCommand() {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		count += append(sb, "Kunden-Nr.", knrValue, count);
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
			if (0 != count) {
				sb.append(" ");
			}
			sb.append(name).append(":").append(value);
		}
		return (null == value ? 0 : 1);
	}

}
