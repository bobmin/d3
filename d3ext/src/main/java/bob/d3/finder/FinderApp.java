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
		URL url = getClass().getResource("/FinderPane.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		AnchorPane pane = loader.load();

		final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();
		final String memoryPath = parameters.isEmpty() ? null : parameters.get(0);
		final String filesPath = parameters.isEmpty() ? null : parameters.get(1);
		FinderController ctrl = loader.getController();
		ctrl.setMemoryPath(new File(memoryPath));
		ctrl.setFilesPath(new File(filesPath));
		ctrl.setHostService(getHostServices());
		ctrl.setStage(primaryStage);

		Scene scene = new Scene(pane);


		primaryStage.setScene(scene);
		primaryStage.setTitle("Document Finder");
		primaryStage.show();

	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
