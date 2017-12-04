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
			count += append(sb, "ID", idValues, false, count);
			count += append(sb, "ERW", extValues, true, count);
			count += append(sb, "Kunden-Nr.", knrValues, false, count);
			count += append(sb, "Lieferanten-Nr.", lnrValues, false, count);
			count += append(sb, "ART", artValues, true, count);
			count += append(sb, dateStarts, dateEnds, count);
		}
		return (0 == sb.length() ? null : sb.toString());
	}

	private int append(StringBuffer sb, String name, List<String> list, boolean lowerCases, int count) {
		if (null != list) {
			if (0 != count) {
				sb.append(" AND ");
			}
			sb.append(name);
			sb.append(":(");
			for (int idx = 0; idx < list.size(); idx++) {
				final String x = list.get(idx);
				if (0 < idx) {
					sb.append("+");
				}
				if (lowerCases) {
					sb.append(x.toLowerCase());
				} else {
					sb.append(x);
				}
			}
			sb.append(")");
		}
		return (null == list ? 0 : list.size());
	}

	// EINBRING:(20171010 TO 20171111)
	private int append(StringBuffer sb, int[] start, int[] end, int count) {
		if (null != start || null != end) {
			if (0 != count) {
				sb.append(" AND ");
			}
			sb.append("EINBRING:[");
			if (null == start) {
				sb.append("19990101");
			} else {
				sb.append(String.format("%02d", start[2]));
				sb.append(String.format("%02d", start[1]));
				sb.append(String.format("%02d", start[0]));
			}
			sb.append(" TO ");
			if (null == end) {
				sb.append("20991231");
			} else {
				sb.append(String.format("%02d", end[2]));
				sb.append(String.format("%02d", end[1]));
				sb.append(String.format("%02d", end[0]));
			}
			sb.append("]");
			return 1;
		}
		return 0;
	}

}
