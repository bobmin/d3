/**
 * Sample Skeleton for 'HelloWorldScene.fxml' Controller Class
 */

package bob.d3.finder;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import bob.d3.D3ExException.SourceException;
import bob.d3.export.DocumentFolder;
import bob.d3.finder.AbstractSearcher.CacheItem;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

	private Map<String, AbstractSearcher.CacheItem> cache = new HashMap<>();

	private DocumentFolder src = null;

	/** Anzahl der Treffer (im Zwischenspeicher) */
	private IntegerProperty matches = new SimpleIntegerProperty(0);

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
	private Label laMatch;

	@FXML
	private Label laFilesPath;

	@FXML
	private CheckMenuItem cbIndexSearcher;

	@FXML
	private CheckMenuItem cbMemorySercher;

	@FXML
	private CheckMenuItem cbShowQuery;

	@FXML
	private CheckMenuItem cbClearBefore;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		laMatch.textProperty().bind(Bindings.concat(matches).concat(" Treffer"));
	}

	@FXML
	void keyPressed(KeyEvent evt) {
		if (evt.getCode().equals(KeyCode.ENTER)) {
			startSearch(null);
		}
	}

	@FXML
	void startSearch(ActionEvent event) {
		if (cbClearBefore.isSelected()) {
			taOutput.clear();
		}

		String input = tfInput.getText();

		if (cbIndexSearcher.isSelected()) {
			taOutput.appendText("Suche über INDEX...\n");
			searchIn(new IndexSearcher(memoryPath), new IndexQuery(input));
		}

		if (cbMemorySercher.isSelected()) {
			taOutput.appendText("Suche über MEMORY...\n");
			searchIn(new MemorySearcher(memoryPath), new SqlQuery(input));
		}

	}

	private void searchIn(final AbstractSearcher searcher, final AbstractQuery query) {
		cache.clear();
		// Abfrage aufbauen
		final String cmd = query.getCommand();
		if (null == cmd) {
			publishLine("Abfrage unklar. Schreibweise korrekt?");
		} else {
			publishQuery(cmd);
			// Suche ausführen
			List<CacheItem> result = searcher.lookFor(cmd);
			for (CacheItem x : result) {
				publishItem(x.getId(), x);
			}
		}
		matches.set(cache.size());
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
	private CacheItem publishItem(String id, CacheItem item) {
		// Anzeige
		StringBuffer sb = new StringBuffer(item.format(id));
		for (String key : item.porps.keySet()) {
			sb.append(", ").append(key).append(":\"").append(item.porps.get(key)).append("\"");
		}
		publishLine(sb.toString());
		// Cache
		if (!cache.containsKey(id)) {
			cache.put(id, item);
		}
		return item;
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
				final File f = src.lookFor(id, item.getFolder(), item.getErw());
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

	@FXML
	void startDirectSearch(ActionEvent event) {
		String selectedText = taOutput.getSelectedText().trim();
		tfInput.setText("#direkt " + selectedText);
		btnSearch.fire();
	}

}