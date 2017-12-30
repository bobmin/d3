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

import bob.d3.CopyUtil;
import bob.d3.D3ExException.SourceException;
import bob.d3.export.DocumentFolder;
import bob.d3.finder.AbstractSearcher.CacheItem;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FinderController implements Initializable {

	private static Logger LOG = Logger.getLogger(FinderController.class.getName());

	private ObjectProperty<File> memoryPathProperty = new SimpleObjectProperty<>();

	private ObjectProperty<File> filesPathProperty = new SimpleObjectProperty<>();

	private HostServices hostServices = null;

	/** die Ergebnisse der Suche pro Dokument-ID */
	private Map<String, AbstractSearcher.CacheItem> cache = new HashMap<>();

	/** diese Dateiablage wird ggf. nach Dokumenten durchsucht */
	private DocumentFolder src = null;

	/** Anzahl der Treffer (im Zwischenspeicher) */
	private IntegerProperty countProperty = new SimpleIntegerProperty(0);

	@FXML
	private Parent rootPane;

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
		laMemoryPath.textProperty().bind(memoryPathProperty.asString());
		laMemoryPath.setTooltip(new Tooltip("memdb + memidx"));
		laMatch.textProperty().bind(Bindings.concat(countProperty).concat(" Treffer"));
		laFilesPath.textProperty().bind(filesPathProperty.asString());
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

		cache.clear();

		String input = tfInput.getText();
		File root = memoryPathProperty.get();

		String indexCmd = analyzeCommand(cbIndexSearcher.isSelected(), "INDEX", new IndexQuery(input));
		String memoryCmd = analyzeCommand(cbMemorySercher.isSelected(), "MEMORY", new SqlQuery(input));

		if (null != indexCmd || null != memoryCmd) {
			SearchWorker task = new SearchWorker(root, indexCmd, memoryCmd);

			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					final List<CacheItem> result = task.getValue();
					if (0 < result.size()) {
						for (CacheItem item : result) {
							publishItem(item.getId(), item);
						}
					}
					countProperty.set(cache.size());
					setDisable(false);
				}
			});

			setDisable(true);
			new Thread(task).start();

		}
	}

	private String analyzeCommand(boolean onoff, String name, AbstractQuery query) {
		String x = null;
		if (onoff) {
			publishLine("Suche über " + name + "...");
			x = query.getCommand();
			if (null != x) {
				publishQuery(x);
			} else {
				publishLine("Abfrage unklar. Schreibweise korrekt?");
			}
		}
		return x;
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
		memoryPathProperty.set(path);
		// laMemoryPath.setText(memoryPath.getAbsolutePath());
		// laMemoryPath.setTooltip(new Tooltip("memdb + memidx"));
	}

	/**
	 * Setzt den Pfad zum Wurzelverzeichnis der Dokumentenablage.
	 * 
	 * @param path
	 *            der Dateipfad
	 */
	public void setFilesPath(File path) {
		filesPathProperty.set(path);
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
					src = DocumentFolder.create(filesPathProperty.get());
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

	@FXML
	void copyFiles(ActionEvent event) {
		if (0 < cache.size()) {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Zielordner wählen");
			chooser.setInitialDirectory(memoryPathProperty.get());
			Stage stage = (Stage) rootPane.getScene().getWindow();
			final File targetFolder = chooser.showDialog(stage);
			if (null != targetFolder) {
				setDisable(true);
				new Thread(new FileCopier(targetFolder)).start();
			}
		} else {
			publishLine("Keine Treffer zum Kopieren. Suche erfolgreich beendet?");
		}
	}

	/**
	 * @see https://stackoverflow.com/questions/27832347/javafx-swingworker-equivalent#27835238
	 */
	private class FileCopier extends Task<String> {

		private final CopyUtil util = new CopyUtil();

		private final File targetFolder;

		public FileCopier(final File targetFolder) {
			this.targetFolder = targetFolder;
		}

		@Override
		protected String call() throws Exception {
			try {
				if (null == src) {
					taOutput.appendText("Erster Zugriff ins Dateisystem. Bitte warten!\n");
					src = DocumentFolder.create(filesPathProperty.get());
				}

				for (String id : cache.keySet()) {
					CacheItem item = cache.get(id);

					final File f = src.lookFor(id, item.getFolder(), item.getErw());

					if (null != f) {
						final String art = item.getArt();
						final String erw = item.getErw();
						final String dstName;
						if (null != art && null != erw) {
							dstName = id + "_" + item.getArt().toUpperCase() + "." + item.getErw().toUpperCase();
						} else {
							dstName = id + "_" + f.getName();
						}
						final File dst = new File(targetFolder, dstName);
						if (!dst.exists()) {
							util.copy(f, dst);
							publishLine("Datei " + f.getAbsolutePath() + " kopiert.");
						} else {
							publishLine("Datei " + dst.getName() + " nicht kopiert. Existiert schon?");
						}
					} else {
						publishLine("Keine Datei mit ID gefunden.");
					}

				}

				setDisable(false);
			} catch (SourceException ex) {
				ex.printStackTrace();
			}
			return null;
		}

	}

	@FXML
	void exitApp(ActionEvent event) {
		Stage stage = (Stage) rootPane.getScene().getWindow();
		stage.close();
	}

	@FXML
	void copyToClipboard(ActionEvent event) {
		String text = tfInput.getSelectedText();
		if (0 == text.trim().length()) {
			text = taOutput.getSelectedText();
		}
		if (0 < text.trim().length()) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();
			content.putString(text);
			clipboard.setContent(content);
		}
	}

	@FXML
	void pasteFromClipboard(ActionEvent event) {
		if (!tfInput.isFocused()) {
			return;
		}

		Clipboard clipboard = Clipboard.getSystemClipboard();

		if (!clipboard.hasContent(DataFormat.PLAIN_TEXT)) {
			return;
		}

		IndexRange range = tfInput.getSelection();

		String origText = tfInput.getText();

		int endPos = 0;
		String updatedText = "";
		String firstPart = origText.substring(0, range.getStart());
		String lastPart = origText.substring(range.getEnd());

		String clipboardText = clipboard.getString();

		updatedText = firstPart + clipboardText + lastPart;

		if (range.getStart() == range.getEnd()) {
			endPos = range.getEnd() + clipboardText.length();
		} else {
			endPos = range.getStart() + clipboardText.length();
		}

		tfInput.setText(updatedText);
		tfInput.positionCaret(endPos);

	}

	@FXML
	void changeTheme(ActionEvent event) {
		String name = ((RadioMenuItem) event.getSource()).getText();
		String url;
		if ("Caspian".equals(name)) {
			url = Application.STYLESHEET_CASPIAN;
		} else if ("Modena".equals(name)) {
			url = Application.STYLESHEET_MODENA;
		} else {
			throw new IllegalArgumentException("[name] unknown: " + name);
		}
		Application.setUserAgentStylesheet(url);
	}

}