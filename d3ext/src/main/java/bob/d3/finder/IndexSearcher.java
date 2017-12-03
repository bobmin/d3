package bob.d3.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexSearcher extends AbstractSearcher {

	private final Path path;

	public IndexSearcher(final File root) {
		this.path = FileSystems.getDefault().getPath(root.getAbsolutePath(), "memidx");
	}

	@Override
	public List<CacheItem> lookFor(String cmd) {

		List<CacheItem> x = new LinkedList<>();

		Analyzer analyzer = new StandardAnalyzer();

		Directory index = null;
		DirectoryReader ireader = null;
		org.apache.lucene.search.IndexSearcher isearcher = null;

		try {
			index = FSDirectory.open(path);

			ireader = DirectoryReader.open(index);
			isearcher = new org.apache.lucene.search.IndexSearcher(ireader);
			// https://stackoverflow.com/questions/2005084/how-to-specify-two-fields-in-lucene-queryparser
			QueryParser parser = new QueryParser("ID", analyzer);
			Query query = parser.parse(cmd);

			// QueryBuilder builder = new QueryBuilder(analyzer);
			// Query knr = builder.createBooleanQuery("Kunden-Nr.", "11016");
			ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				String einbring = hitDoc.get("EINBRING");
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.YEAR, Integer.parseInt(einbring.substring(0, 4)));
				cal.set(Calendar.MONTH, Integer.parseInt(einbring.substring(4, 6)) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(einbring.substring(6, 8)));
				CacheItem item = new CacheItem(hitDoc.get("ID"), cal.getTime(), hitDoc.get("FOLDER"),
						hitDoc.get("ERW"));
				MemoryProps memprops = MemoryProps.getDefault();
				int size = memprops.getSize();
				for (int idx = 0; idx < size; idx++) {
					String name = memprops.getName(idx);
					String value = hitDoc.get(name);
					if (null != value) {
						item.porps.put(name, value);
					}
				}
				x.add(item);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} finally {
			if (null != ireader) {
				try {
					ireader.close();
				} catch (IOException ex) {
					// ignored
				}
			}
			if (null != index) {
				try {
					index.close();
				} catch (IOException ex) {
					// ignored
				}
			}
		}
		return x;
	}

}
