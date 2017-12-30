package bob.d3.finder;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bob.d3.AbstractDocument;

/**
 * Führt eine Suche aus.
 * 
 * @author maik@btmx.net
 *
 */
public abstract class AbstractSearcher {

	abstract public List<CacheItem> lookFor(String input);

	class CacheItem extends AbstractDocument {

		final Map<String, String> porps = new LinkedHashMap<>();

		public CacheItem(String id, Date einbring, String art, String erw) {
			super(id, einbring, art, erw);
		}

		public String format(String id) {
			return String.format("%1$s, %2$tY-%2$tm-%2$td, %3$s, %4$s", id, einbring, art, erw);
		}

	}

}
