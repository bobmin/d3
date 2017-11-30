package bob.d3.finder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bob.d3.D3ExException.ResourceException;
import bob.d3.ResourceFile;

public class MemoryArtsCache {

	private static final Logger LOG = Logger.getLogger(MemoryArtsCache.class.getName());

	private final Map<String, String> data = new LinkedHashMap<>();

	private static MemoryArtsCache _self = null;

	public static MemoryArtsCache getDefault() {
		if (null == _self) {
			final Map<String, String> x = new LinkedHashMap<>();
			try {
				ResourceFile res = new ResourceFile("/memory_doc_doku_art.txt");
				final String[] text = res.getText().split("\\n");
				for (String line : text) {
					String[] cols = line.split("\\t");
					x.put(cols[0], cols[1]);
				}
			} catch (ResourceException ex) {
				LOG.log(Level.SEVERE, "data file corrupt", ex);
			}
			LOG.info("data loaded: " + x);
			_self = new MemoryArtsCache(x);
		}
		return _self;
	}

	private MemoryArtsCache(Map<String, String> values) {
		this.data.putAll(values);
	}

	public int getSize() {
		return data.size();
	}

	public String lookFor(String artShort) {
		return data.get(artShort);
	}

}
