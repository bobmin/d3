package bob.d3.finder;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import bob.d3.Property;

public class MemorySearcher extends AbstractSearcher {

	private final File memdb;

	public MemorySearcher(final File root) {
		this.memdb = new File(root, "memdb");
	}

	@Override
	public List<CacheItem> lookFor(String cmd) {
		List<CacheItem> x = new LinkedList<>();
		MemoryReader memory = null;
		try {

			memory = new MemoryReader(memdb);
			if (memory.open(cmd)) {
				do {
					bob.d3.Document doc = memory.getDoc();

					CacheItem item = new CacheItem(doc.getId(), doc.getEinbring(), doc.getFolder(), doc.getErw());
					List<Property> memprops = doc.getProps();
					for (Property p : memprops) {
						String value = p.getValue();
						if (null != value) {
							String name = p.getLongtext();
							item.porps.put((null == name ? p.getColumnName() : name), value);
						}
					}
					x.add(item);

				} while (memory.next());
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != memory) {
				memory.close();
			}
		}
		return x;
	}

}
