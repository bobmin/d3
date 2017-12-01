/**
 * Sample Skeleton for 'HelloWorldScene.fxml' Controller Class
 */

package bob.d3.finder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import bob.d3.D3ExException.SourceException;
import bob.d3.export.DocumentFolder;
import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FinderController implements Initializable {

	private static Logger LOG = Logger.getLogger(FinderController.class.getName());

	private File memoryPath = null;

	private File filesPath = null;

	private HostServices hostServices = null;

	private Map<String, CacheItem> cache = new HashMap<>();

	private DocumentFolder src = null;

	@FXML
	private MenuBar menuBar;

	@FXML
	private Button btnSearch;

	@FXML
	private TextField tfInput;

	@FXML
	private TextArea taOutput;

	@FXML
	private Label laMemoryPath;

	@FXML
	private Label laFilesPath;

	@FXML
	private CheckMenuItem cbIndexSearcher;

	@FXML
	private CheckMenuItem cbMemorySercher;

	@FXML
	private CheckMenuItem cbShowQuery;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	void keyPressed(KeyEvent evt) {
		if (evt.getCode().equals(KeyCode.ENTER)) {
			startSearch(null);
		}
	}

	@FXML
	void startSearch(ActionEvent event) {
		String input = tfInput.getText();

		if (cbIndexSearcher.isSelected()) {
			taOutput.appendText("Suche über INDEX...\n");
			searchInIndex(input);
		}

		if (cbMemorySercher.isSelected()) {
			taOutput.appendText("Suche über MEMORY...\n");
			searchInMemory(input);
		}

	}

	private void searchInIndex(String input) {
		IndexQuery indexQuery = new IndexQuery(input);
		String cmd = indexQuery.getCommand();
		publishQuery(cmd);

		Analyzer analyzer = new StandardAnalyzer();

		final Path path = FileSystems.getDefault().getPath(memoryPath.getAbsolutePath(), "memidx");

		Directory index = null;
		DirectoryReader ireader = null;
		IndexSearcher isearcher = null;

		try {
			index = FSDirectory.open(path);

			ireader = DirectoryReader.open(index);
			isearcher = new IndexSearcher(ireader);
			// https://stackoverflow.com/questions/2005084/how-to-specify-two-fields-in-lucene-queryparser
			QueryParser parser = new QueryParser("ID", analyzer);
			Query query = parser.parse(cmd);

			// QueryBuilder builder = new QueryBuilder(analyzer);
			// Query knr = builder.createBooleanQuery("Kunden-Nr.", "11016");
			ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				publishItem(hitDoc.get("ID"), hitDoc.get("FOLDER"), hitDoc.get("ERW"));
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
	}

	/**
	 * Bringt eine Textzeile zur Anzeige.
	 * 
	 * @param msg
	 *            die Nachricht
	 */
	private void publishLine(String msg) {
		taOutput.appendText(msg);
		taOutput.appendText("\n");
	}

	/**
	 * Veröffentlicht die Abfrage, wenn der Menüeintrag aktiviert ist.
	 * 
	 * @param query
	 *            die Abfrage
	 */
	private void publishQuery(String query) {
		if (cbShowQuery.isSelected()) {
			publishLine(query);
		}
	}

	/**
	 * Bringt eine Fundstelle zur Anzeige und speichert diese im Cache.
	 * 
	 * @param id
	 *            die ID vom Dokument
	 * @param folder
	 *            der Ordner vom Dokument
	 * @param erw
	 *            die Dateierweiterung vom Dokument
	 */
	private void publishItem(String id, String folder, String erw) {
		CacheItem item = new CacheItem();
		item.folder = folder;
		item.erw = erw;
		// Anzeige
		publishLine(item.format(id));
		// Cache
		if (!cache.containsKey(id)) {
			cache.put(id, item);
		}
	}

	private void searchInMemory(final String input) {
		SqlQuery gen = new SqlQuery(input);
		String sql = gen.getCommand();
		publishQuery(sql);

		MemoryReader memory = null;
		try {
			final File memdb = new File(memoryPath, "memdb");
			memory = new MemoryReader(memdb);
			if (memory.open(sql)) {
				do {
					bob.d3.Document doc = memory.getDoc();
					taOutput.appendText(doc + "\n");
				} while (memory.next());
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != memory) {
				memory.close();
			}
		}
	}

	public void setMemoryPath(File path) {
		this.memoryPath = path;
		laMemoryPath.setText(memoryPath.getAbsolutePath());
		laMemoryPath.setTooltip(new Tooltip("memdb + memidx"));
	}

	public void setFilesPath(File path) {
		this.filesPath = path;
		laFilesPath.setText(filesPath.getAbsolutePath());
	}

	@FXML
	void lookForFile(ActionEvent event) {
		String id = taOutput.getSelectedText().trim();
		if (id.matches("P[\\d]+")) {
			CacheItem item = cache.get(id);
			if (null != item) {
				setDisable(true);
				taOutput.appendText(id + "...\n");
				new Thread(new FileSearcher(id, item)).start();
			} else {
				taOutput.appendText("ID nicht aus letztem Suchergebnis. Suche erneut ausführen.");
			}
		} else {
			taOutput.appendText("keine ID in Auswahl erkannt :(\n");
		}
	}

	private void setDisable(boolean b) {
		menuBar.setDisable(b);
		tfInput.setDisable(b);
		btnSearch.setDisable(b);
		taOutput.setDisable(b);

	}

	private class CacheItem {

		String folder;
		String erw;

		public String format(String id) {
			return String.format("%s, %s, %s", id, folder, erw);
		}

	}

	/**
	 * @see https://stackoverflow.com/questions/27832347/javafx-swingworker-equivalent#27835238
	 */
	private class FileSearcher extends Task<String> {

		private final String id;
		private final CacheItem item;

		public FileSearcher(String id, CacheItem item) {
			this.id = id;
			this.item = item;
		}

		@Override
		protected String call() throws Exception {
			try {
				if (null == src) {
					taOutput.appendText("Erster Zugriff ins Dateisystem. Bitte warten!\n");
					src = DocumentFolder.create();
				}
				final File f = src.lookFor(id, item.folder, item.erw);
				if (null != f) {
					taOutput.appendText(f.getAbsolutePath() + "\n");
					hostServices.showDocument(f.getAbsolutePath());
				} else {
					taOutput.appendText("Keine Datei mit ID gefunden. Ist das Verzeichnis korrekt?");
				}
				setDisable(false);
			} catch (SourceException ex) {
				ex.printStackTrace();
			}
			return null;
		}

	}

	public void setHostService(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@FXML
	void showWebsite(ActionEvent event) {
		hostServices.showDocument("https://github.com/bobmin/d3");
	}

	@FXML
	void printCacheItems(ActionEvent event) {
		for (String id : cache.keySet()) {
			CacheItem item = cache.get(id);
			publishLine(item.format(id));
		}
	}

	@FXML
	void clearOutput(ActionEvent event) {
		taOutput.clear();
	}

}