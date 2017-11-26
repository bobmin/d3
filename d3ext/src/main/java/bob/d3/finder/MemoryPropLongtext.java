package bob.d3.finder;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import bob.d3.D3ExException.ResourceException;
import bob.d3.ResourceFile;

public class MemoryPropLongtext {

	private static final Logger LOG = Logger.getLogger(MemoryPropLongtext.class.getName());

	private final List<String> data = new LinkedList<>();

	private static MemoryPropLongtext _self = null;

	public static MemoryPropLongtext getDefault() {
		if (null == _self) {
			final List<String> x = new LinkedList<>();
			try {
				ResourceFile res = new ResourceFile("/memory_prop_longtext.txt");
				final String[] text = res.getText().split("\\n");
				for (String line : text) {
					x.add(line.split("\\t")[0]);
				}
			} catch (ResourceException ex) {
				LOG.log(Level.SEVERE, "data file corrupt", ex);
			}
			LOG.info("data loaded: " + x);
			_self = new MemoryPropLongtext(x);
		}
		return _self;
	}

	private MemoryPropLongtext(List<String> values) {
		this.data.addAll(values);
	}

	public int getSize() {
		return data.size();
	}

}
