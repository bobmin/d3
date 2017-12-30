package bob.d3.finder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bob.d3.D3ExException.ResourceException;
import bob.d3.ResourceFile;

public class MemoryArts {

	private static final Logger LOG = Logger.getLogger(MemoryArts.class.getName());

	private final Map<String, String> data = new LinkedHashMap<>();

	private static MemoryArts _self = null;

	public static MemoryArts getDefault() {
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
			_self = new MemoryArts(x);
		}
		return _self;
	}

	private MemoryArts(Map<String, String> values) {
		this.data.putAll(values);
	}

	public int getSize() {
		return data.size();
	}

	public String lookFor(String artShort) {
		return data.get(artShort);
	}

}
