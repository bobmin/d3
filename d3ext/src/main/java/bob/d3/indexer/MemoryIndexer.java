package bob.d3.indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MemoryIndexer {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(MemoryIndexer.class.getName());

	/** der Zeitpunkt vom Programmstart */
	private static final long startTimeMillis = System.currentTimeMillis();

	public static void main(String[] args) throws IOException {
		if (2 != args.length) {
			System.out.println("usage: java -cp ... bob.d3.indexer.MemoryIndexer <memory_path> <index_path>");
			System.exit(-1);
		}

		final String memoryPath = args[0];
		final String indexPath = args[1];

		final Path path = FileSystems.getDefault().getPath(indexPath, "memidx");

		log("Program starts...\n\tmemory path = " + memoryPath + "\n\tindex path = " + path.toAbsolutePath());

		final Directory index = FSDirectory.open(path);

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(index, config);

		Document document = new Document();
		document.add(new TextField("id", "P0000001", Field.Store.YES));
		document.add(new TextField("folder", "P000", Field.Store.YES));
		document.add(new TextField("knr", "11016", Field.Store.YES));

		log("document added: " + writer.addDocument(document));
		writer.close();


		log("Program finished. Bye!");

	}

	private static void log(final String msg) {
		long interval = System.currentTimeMillis() - startTimeMillis;
		final long hr = TimeUnit.MILLISECONDS.toHours(interval);
		final long min = TimeUnit.MILLISECONDS.toMinutes(interval) % 60;
		final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) % 60;
		LOG.info(String.format("%02d:%02d:%02d %s", hr, min, sec, msg));
	}

}
