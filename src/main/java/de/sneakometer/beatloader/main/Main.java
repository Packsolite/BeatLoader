package de.sneakometer.beatloader.main;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.setupMainStage(stage);
		stage.show();
	}

	private void setupMainStage(Stage stage) throws IOException {
		URL mainLayout = getClass().getResource("/view/Welcome.fxml");
		URL mainStyle = getClass().getResource("/css/welcome.css");

		/* Welcome Scene */
		Parent root = FXMLLoader.load(mainLayout);
		Scene scene = new Scene(root);
		ObservableList<String> styles = scene.getStylesheets();
		styles.add(mainStyle.toExternalForm());

		/* Setup stage */
		Image icon = new Image("icon/icon.png");
		ObservableList<Image> icons = stage.getIcons();
		icons.add(icon);
		stage.setResizable(false);
		stage.setTitle("Beat Loader");
		stage.setScene(scene);
	}
}
