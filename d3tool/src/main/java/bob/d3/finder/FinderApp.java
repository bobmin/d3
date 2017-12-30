package bob.d3.finder;
	
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Die Anwendung zum Suchen.
 * 
 * @author maik@btmx.net
 *
 */
public class FinderApp extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		URL url = getClass().getResource("/FinderPane.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		Parent pane = loader.load();

		final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();
		final String memoryPath = parameters.isEmpty() ? null : parameters.get(0);
		final String filesPath = parameters.isEmpty() ? null : parameters.get(1);

		FinderController ctrl = loader.getController();

		ctrl.setMemoryPath(new File(memoryPath));
		ctrl.setFilesPath(new File(filesPath));
		ctrl.setHostService(getHostServices());

		Scene scene = new Scene(pane);
		scene.getStylesheets().add("/FinderPane.css");

		stage.setScene(scene);
		stage.setTitle("Document Finder");
		stage.show();

	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
