/**
 * Sample Skeleton for 'HelloWorldScene.fxml' Controller Class
 */

package bob.d3.finder;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import bob.d3.Document;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

		SqlQuery gen = new SqlQuery(input);
		String sql = gen.getCommand();
		taOutput.appendText(sql + "\n");

		MemoryReader memory = null;
		try {
			memory = new MemoryReader(memoryPath);
			if (memory.open(sql)) {
				do {
					Document doc = memory.getDoc();
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