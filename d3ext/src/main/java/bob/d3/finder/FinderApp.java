package bob.d3.finder;
	
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Die Anwendung zum Suchen in der Datenbank.
 * 
 * @author maik@btmx.net
 *
 */
public class FinderApp extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		URL url = getClass().getResource("FinderPane.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		AnchorPane pane = loader.load();

		final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();
		final String exportPath = !parameters.isEmpty() ? parameters.get(0) : null;
		FinderController ctrl = loader.getController();
		ctrl.setMemoryPath(new File(exportPath));

		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("D3 Export Finder");
		primaryStage.show();

	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
