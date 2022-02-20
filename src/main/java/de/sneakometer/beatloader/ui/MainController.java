package de.sneakometer.beatloader.ui;

import de.sneakometer.beatloader.core.BeatLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class MainController {
	@FXML
	private Label minStarsLabel;
	@FXML
	private Label maxStarsLabel;
	@FXML
	private Label statusLabel;
	@FXML
	private Slider minStarsSlider;
	@FXML
	private Slider maxStarsSlider;
	@FXML
	private CheckBox createPlaylistBox;
	@FXML
	private CheckBox favoriteBox;
	@FXML
	private CheckBox redownloadBox;

	public void onClickDownload(ActionEvent event) {
		double minStars = minStarsSlider.getValue();
		double maxStars = maxStarsSlider.getValue();
		boolean createPlaylist = createPlaylistBox.isSelected();
		boolean favorite = favoriteBox.isSelected();
		boolean redownload = redownloadBox.isSelected();
		Button button = (Button) event.getSource();
		button.setDisable(true);
		statusLabel.setVisible(true);
		BeatLoader.start(minStars, maxStars, createPlaylist, favorite, redownload)
				.thenRun(() -> Platform.runLater(() -> button.setDisable(false)))
				.exceptionally(ex -> {
					ex.printStackTrace();
					return null;
				});
	}

	@FXML
	public void initialize() {
		minStarsSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue.doubleValue() > maxStarsSlider.getValue()) {
						minStarsSlider.setValue(maxStarsSlider.getValue());
					} else {
						minStarsLabel.setText("Minimum Star Difficulty: " + Math.round(minStarsSlider.getValue() * 10) / 10D);
					}
				});
		maxStarsSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue.doubleValue() < minStarsSlider.getValue()) {
						maxStarsSlider.setValue(minStarsSlider.getValue());
					} else {
						maxStarsLabel.setText("Maximum Star Difficulty: " + Math.round(maxStarsSlider.getValue() * 10) / 10D);
					}
				});
		BeatLoader.subscribeStatus(status -> Platform.runLater(() -> statusLabel.setText(status)));
	}
}
