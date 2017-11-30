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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FinderController implements Initializable {

	private static Logger LOG = Logger.getLogger(FinderController.class.getName());

	@FXML
	private Button btnSearch;

	@FXML
	private TextField tfInput;

	@FXML
	private TextArea taOutput;

	@FXML
	private Label laPath;

	private File memoryPath = null;

	@FXML
	private CheckMenuItem cbIndexSearcher;

	@FXML
	private CheckMenuItem cbMemorySercher;

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
			searchInIndex(input);
		}

		if (cbMemorySercher.isSelected()) {
			searchInMemory(input);
		}

	}

	private void searchInIndex(String input) {
		Analyzer analyzer = new StandardAnalyzer();

		final Path path = FileSystems.getDefault().getPath(memoryPath.getAbsolutePath(), "memidx");

		Directory index = null;
		DirectoryReader ireader = null;
		IndexSearcher isearcher = null;

		try {
			index = FSDirectory.open(path);

			ireader = DirectoryReader.open(index);
			isearcher = new IndexSearcher(ireader);
			// Parse a simple query that searches for "text":
			QueryParser parser = new QueryParser("id", analyzer);
			Query query = parser.parse("P0000020");
			ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				taOutput.appendText(hitDoc.get("fieldname") + "\n");
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

	private void searchInMemory(final String input) {
		SqlQuery gen = new SqlQuery(input);
		String sql = gen.getCommand();
		taOutput.appendText(sql + "\n");

		MemoryReader memory = null;
		try {
			memory = new MemoryReader(memoryPath);
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
		laPath.setText(memoryPath.getAbsolutePath());
	}

}