package bob.d3.finder;

import java.util.List;

/**
 * Übersetzt eine Suchanfrage in eine passende Index-Abfrage.
 * 
 * @author maikboettcher
 *
 * @see https://lucene.apache.org/core/7_1_0//queryparser/org/apache/lucene/queryparser/classic/package-summary.html
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
		if (empty) {
			sb.append("P*");
		} else if (null != direct) {
			sb.append(direct);
		} else {
			int count = 0;
			count += append(sb, "ID", idValues, count);
			count += append(sb, "Kunden-Nr.", knrValue, count);
			// mod_date:[20020101 TO 20030101]
		}
		return (0 == sb.length() ? null : sb.toString());
	}

	private int append(StringBuffer sb, String name, List<String> list, int count) {
		if (null != list) {
			if (0 != count) {
				sb.append(" ");
			}
			sb.append(name);
			sb.append(":(");
			for (int idx = 0; idx < list.size(); idx++) {
				final String x = list.get(idx);
				if (0 < idx) {
					sb.append(",");
				}
				sb.append(x);
			}
			sb.append(")");
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
