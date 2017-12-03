package bob.d3.finder;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Führt eine Suche aus.
 * 
 * @author maik@btmx.net
 *
 */
public abstract class AbstractSearcher {

	abstract public List<CacheItem> lookFor(String input);

	class CacheItem {

		final String id;
		final Date einbring;
		final String folder;
		final String erw;
		final Map<String, String> porps = new LinkedHashMap<>();

		public CacheItem(String id, Date einbring, String folder, String erw) {
			this.id = id;
			this.einbring = einbring;
			this.folder = folder;
			this.erw = erw;
		}

		public String format(String id) {
			return String.format("%1$s, %2$tY-%2$tm-%2$td, %3$s", id, einbring, erw);
		}

	}

}
