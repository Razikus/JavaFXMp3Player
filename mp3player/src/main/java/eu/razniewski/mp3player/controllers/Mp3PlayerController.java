package eu.razniewski.mp3player.controllers;

import eu.razniewski.mp3player.models.Mp3PlayerModel;
import eu.razniewski.mp3player.models.PlaylistEntry;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class Mp3PlayerController {

    private MediaPlayer player;

    private Mp3PlayerModel model;

    @FXML
    private ListView<PlaylistEntry> playlist;

    @FXML
    private Canvas canvas;

    @FXML
    private Label status;

    @FXML
    void onOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Sounds", "*.mp3", "*.wav", "*.ogg"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if(files.size() > 0 ) {
            model.setCurrentMediaInPlayer(files.get(0));
        }
        for(File file: files) {
            if (file != null) {
                model.getItems().add(new PlaylistEntry(file));
            }
        }
    }

    @FXML
    void onPlay(ActionEvent event) {
        if(model.getPlayer() != null) {
            model.getPlayer().play();
        }
    }

    @FXML
    public void initialize() {
        model = new Mp3PlayerModel();
        playlist.setItems(model.getItems());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        status.textProperty().bind(model.currentTitleProperty());

        playlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    PlaylistEntry current = playlist.getSelectionModel().getSelectedItem();
                    if(current != null) {
                        model.setCurrentMediaInPlayer(current.getFileUri());
                        model.play();
                    }
                }
            }
        });

    }


}
