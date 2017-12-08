package bob.d3.finder;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import bob.d3.finder.AbstractSearcher.CacheItem;
import javafx.concurrent.Task;

/**
 * Führt die Suchen aus. Kann als nebenläufige Aufgabe gestartet werden.
 * 
 * @author maik@btmx.net
 *
 */
public class SearchWorker extends Task<List<CacheItem>> {

	private final File root;

	private final String indexCmd;

	private final String memoryCmd;

	/**
	 * Instaziiert die Aufgabe. Wird <code>null</code> als Abfrage übergeben,
	 * wird keine Abfrage gestartet.
	 * 
	 * @param root
	 *            das Wurzelverzeichnis von Index und Datenbank
	 * @param indexCmd
	 *            die Abfrage zum Index
	 * @param memoryCmd
	 *            die Abfrage zur Datenbank
	 */
	public SearchWorker(final File root, final String indexCmd, final String memoryCmd) {
		this.root = root;
		this.indexCmd = indexCmd;
		this.memoryCmd = memoryCmd;
	}

	@Override
	protected List<CacheItem> call() throws Exception {
		List<CacheItem> x = new LinkedList<>();

		if (null != indexCmd) {
			AbstractSearcher searcher = new IndexSearcher(root);
			x.addAll(searcher.lookFor(indexCmd));
		}

		if (null != memoryCmd) {
			AbstractSearcher searcher = new MemorySearcher(root);
			x.addAll(searcher.lookFor(memoryCmd));
		}

		return x;
	}

}
