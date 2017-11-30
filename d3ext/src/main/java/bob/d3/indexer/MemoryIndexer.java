package bob.d3.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import bob.d3.ConsoleUtil;
import bob.d3.Property;
import bob.d3.finder.MemoryReader;

public class MemoryIndexer {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(MemoryIndexer.class.getName());

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		if (1 != args.length) {
			System.out.println("usage: java -cp ... bob.d3.indexer.MemoryIndexer <folder>");
			System.exit(-1);
		}

		final String folder = args[0];
		ConsoleUtil.log("%s starts: %s", MemoryIndexer.class.getSimpleName(), folder);

		// Quelle
		final MemoryReader reader = new MemoryReader(new File(folder, "memdb"));

		// Ziel
		final Path path = FileSystems.getDefault().getPath(folder, "memidx");
		final Directory index = FSDirectory.open(path);

		StandardAnalyzer analyzer = new StandardAnalyzer();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		IndexWriter writer = new IndexWriter(index, config);

		try {
			final String sql = "SELECT TOP 100 * FROM document";
			if (reader.open(sql)) {
				do {
					bob.d3.Document doc = reader.getDoc();

					Document document = new Document();

					document.add(new TextField("ID", doc.getId(), Field.Store.YES));
					String artShort = doc.getArt();
					String artLong = doc.getArtLong();
					if (null != artLong) {
						document.add(new TextField("ART", String.format("%s [%s]", artLong, artShort), Field.Store.NO));
					} else {
						document.add(new TextField("ART", artShort, Field.Store.NO));
					}

					List<Property> props = doc.getProps();
					for (Property p : props) {
						String value = p.getValue();
						if (null != value && 0 < value.trim().length()) {
							document.add(new TextField(p.getColumnName(), value, Field.Store.NO));
							String label = p.getLongtext();
							if (null != label) {
								document.add(new TextField(label, value, Field.Store.YES));
							}
						}
					}

					ConsoleUtil.log("document added: " + writer.addDocument(document));

				} while (reader.next());
			}
		} finally {
			writer.close();
			reader.close();
		}

		ConsoleUtil.log("%s finished. Bye!", MemoryIndexer.class.getSimpleName());

	}

}
