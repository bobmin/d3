package bob.d3.finder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interpretiert die Sucheingabe und erwartet von speziellen Implementierungen
 * die Ausprägung in entsprechende Abfragesprachen.
 * 
 * @author maik@btmx.net
 *
 */
public abstract class AbstractQuery {

	protected boolean empty = false;

	protected String direct = null;

	protected List<String> idValues = null;

	protected List<String> extValues = null;

	protected List<String> knrValues = null;

	protected List<String> lnrValues = null;

	protected List<String> artValues = null;

	protected int[] dateStarts = null;

	protected int[] dateEnds = null;

	public AbstractQuery(final String input) {
		if (null == input || 0 == input.trim().length()) {
			empty = true;
		} else if (input.startsWith("#direkt ")) {
			direct = input.substring(8);
		} else {
			if (-1 < input.indexOf("id = ")) {
				idValues = new LinkedList<>();
				idValues.addAll(lookForList(input, "id = ([A-Z0-9]+)"));
			}
			if (-1 < input.indexOf("ext = ")) {
				extValues = new LinkedList<>();
				extValues.addAll(lookForList(input, "ext = ([a-zA-Z]+)"));
			}
			if (-1 < input.indexOf("knr = ")) {
				knrValues = new LinkedList<>();
				knrValues.addAll(lookForList(input, "knr = ([0-9]+)"));
			}
			if (-1 < input.indexOf("lnr = ")) {
				lnrValues = new LinkedList<>();
				lnrValues.addAll(lookForList(input, "lnr = ([0-9]+)"));
			}
			if (-1 < input.indexOf("art = ")) {
				artValues = new LinkedList<>();
				artValues.addAll(lookForList(input, "art = ([a-zA-Z]+)"));
			}
			if (-1 < input.indexOf("datum > ") || -1 < input.indexOf("datum < ")) {
				dateStarts = lookForArray(input, "datum > ([0-9][0-9]).([0-9][0-9]).([0-9][0-9][0-9][0-9])");
				dateEnds = lookForArray(input, "datum < ([0-9][0-9]).([0-9][0-9]).([0-9][0-9][0-9][0-9])");
			}
		}
	}

	private List<String> lookForList(String input, String token) {
		List<String> x = new LinkedList<>();
		Pattern p = Pattern.compile(token);
		Matcher m = p.matcher(input);
		while (m.find()) {
			final String value = m.group(1);
			x.add(value);
		}
		return x;
	}

	private int[] lookForArray(String input, String token) {
		int[] x = null;
		Pattern p = Pattern.compile(token);
		Matcher m = p.matcher(input);
		while (m.find()) {
			try {
				int a = Integer.parseInt(m.group(1));
				int b = Integer.parseInt(m.group(2));
				int c = Integer.parseInt(m.group(3));
				x = new int[] { a, b, c };
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
		}
		return x;
	}

	abstract public String getCommand();

	@Override
	public String toString() {
		String data;
		if (null == direct) {
			// @formatter:off
			data = ", idValues=" + idValues 
					+ ", extValues=" + extValues 
					+ ", knrValues=" + knrValues
					+ ", lnrValues=" + lnrValues
					+ ", dateStarts=" + Arrays.toString(dateStarts)
					+ ", dateEnds=" + Arrays.toString(dateEnds);
			// @formatter:on
		} else {
			data = ", direct=" + direct;
		}
		return "AbstractQuery [emtpy=" + empty + data + "]";
	}

}
