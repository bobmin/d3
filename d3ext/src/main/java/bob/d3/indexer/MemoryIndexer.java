package bob.d3.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			final String sql = "SELECT TOP 100 * FROM document";
			if (reader.open(sql)) {
				do {
					bob.d3.Document doc = reader.getDoc();

					Document document = new Document();

					// Datei
					document.add(new TextField("ID", doc.getId(), Field.Store.YES));
					document.add(new StringField("FOLDER", doc.getFolder(), Field.Store.YES));
					document.add(new StringField("ERW", doc.getErw().trim().toLowerCase(), Field.Store.YES));

					// Art
					String artShort = doc.getArt();
					document.add(new StringField("ART", artShort, Field.Store.YES));

					// Datum
					Date einbring = doc.getEinbring();
					// document.add(new LongPoint("EINBRING_YYYYMMDD",
					// Long.parseLong(sdf.format(einbring))));
					document.add(new TextField("EINBRING", sdf.format(einbring), Field.Store.YES));
					
					// Eigenschaften
					List<Property> props = doc.getProps();
					for (Property p : props) {
						String value = p.getValue();
						if (null != value && 0 < value.trim().length()) {
							if (value.endsWith(" 00:00:00.0")) {
								value = value.substring(0, value.length() - 11);
							}
							String label = p.getLongtext();
							if (null != label) {
								document.add(new TextField(label, value, Field.Store.YES));
							} else {
								document.add(new TextField(p.getColumnName(), value, Field.Store.NO));
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
