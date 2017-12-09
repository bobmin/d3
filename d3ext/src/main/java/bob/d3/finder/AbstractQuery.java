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

	/** <code>true</code> wenn Eingabe leer war */
	protected boolean empty = false;

	/** gefüllt wenn Direktsuche über #direkt angezeigt wurde */
	protected String direct = null;

	/** die ID's sollen gesucht werden */
	protected List<String> idValues = null;

	/** die Dateierweiterungen sollen gesucht werden */
	protected List<String> extValues = null;

	/** die Kundennummern sollen gesucht werden */
	protected List<String> knrValues = null;

	/** die Lieferantennummern sollen gesucht werden */
	protected List<String> lnrValues = null;

	/** die Dokumentenarten sollen gesucht werden */
	protected List<String> artValues = null;

	/** ab diesem Datum soll gesucht werden */
	protected int[] dateStarts = null;

	/** bis zu diesem Datum soll gesucht werden */
	protected int[] dateEnds = null;

	/**
	 * Instanziiert eine Abfrage für die Eingabe und versucht die verschiedenen
	 * Muster innerhalb der Eingabe zu finden.
	 * 
	 * @param input
	 *            die Benutzereingabe
	 */
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

	/**
	 * Sucht das Muster innerhalb der Eingabe und liefert alle Werte aus allen
	 * Fundstellen zurück. Der Wert wird im Muster als erstes Klammernpaar
	 * erwartet.
	 * 
	 * @param input
	 *            die Eingabe
	 * @param token
	 *            das Muster
	 * @return ein Objekt, niemals <code>null</code>
	 */
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

	/**
	 * Sucht das Muster innerhalb der Eingabe und liefert die Werte zurück. Als
	 * Werte werden die ersten 3 Klammernpaare interpretiert.
	 * 
	 * @param input
	 *            die Eingabe
	 * @param token
	 *            das Muster
	 * @return ein Objekt, niemals <code>null</code>
	 */
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
