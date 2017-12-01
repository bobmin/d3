package bob.d3.finder;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

	protected List<String> idValues = null;

	protected List<String> extValues = null;

	protected String knrValue = null;

	protected String lnrValue = null;

	public AbstractQuery(final String input) {
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

	abstract public String getCommand();

	@Override
	public String toString() {
		return "AbstractQuery [idValues=" + idValues + ", extValues=" + extValues + ", knrValue=" + knrValue
				+ ", lnrValue=" + lnrValue + "]";
	}

}
