package eu.razniewski.mp3player.controllers;

import eu.razniewski.mp3player.models.Mp3PlayerModel;
import eu.razniewski.mp3player.models.PlaylistEntry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Mp3PlayerController {

    private boolean mouseOnSlider = false;

    private Mp3PlayerModel model;

    @FXML
    private ListView<PlaylistEntry> playlist;

    @FXML
    private Canvas canvas;

    @FXML
    private Label status;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Slider currentDuration;

    @FXML
    private Slider volumeSlider;

    @FXML
    void onOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Sounds", "*.mp3", "*.wav", "*.ogg"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if(files == null) {
            return;
        }
        PlaylistEntry first = null;
        boolean isEmpty = true;
        if(files.size() > 0 ) {
            first = new PlaylistEntry(files.get(0));
            model.getItems().add(first);
            playlist.getSelectionModel().selectFirst();
            model.setCurrentMediaInPlayer(files.get(0));
            model.currentPlayListEntryProperty().setValue(first);
        }
        for(File file: files) {
            if (file != null) {
                PlaylistEntry entry = new PlaylistEntry(file);
                if(!first.getFileUri().equals(entry.getFileUri())) {
                    model.getItems().add(entry);
                }
            }
        }
    }

    @FXML
    void onPlay(ActionEvent event) {
        if(model.getPlayer() != null) {
            model.play();
        }
    }

    @FXML
    void onSavePlaylist(ActionEvent event) {
        if(model.getItems().isEmpty()) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Playlist");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Playlist file (*.pla)", "*.pla"));
        fileChooser.setInitialFileName("playlist.pla");
        File file = fileChooser.showSaveDialog(null);

        if(file == null) {
            return;
        }
        String content = model.generatePlaylistContent();
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            return;
        }
    }

    @FXML
    void onOpenPlaylist(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Playlist");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Playlist file (*.pla)", "*.pla"));
        fileChooser.setInitialFileName("playlist.pla");
        File file = fileChooser.showOpenDialog(null);
        if(file == null) {
            return;
        }
        String content = null;
        try {
            content = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        } catch (IOException e) {
            return;
        }
        ArrayList<PlaylistEntry> entries = model.generatePlaylistFromJson(content);
        model.getItems().clear();
        model.getItems().addAll(entries);
        playlist.getSelectionModel().selectFirst();
        model.setCurrentPlayListEntry(playlist.getSelectionModel().getSelectedItem());
        model.setCurrentMediaInPlayer(playlist.getSelectionModel().getSelectedItem().getFileUri());
    }

    @FXML
    void onStop(ActionEvent event) {
        if(model.getPlayer() != null) {
            model.stop();
        }
    }

    @FXML
    void onPause(ActionEvent event) {
        if(model.getPlayer() != null) {
            model.pause();
        }
    }

    @FXML
    void onMouseEnterSlider(MouseEvent event) {
        mouseOnSlider = true;
        if(model.getPlayer() != null && model.isPlay()) {
            model.getPlayer().pause();
        }
    }

    @FXML
    void onMouseLeaveSlider(MouseEvent event) {
        mouseOnSlider = false;
        if(model.getPlayer() != null && model.isPlay()) {
            model.getPlayer().play();
        }
    }


    @FXML
    public void initialize() {
        model = new Mp3PlayerModel(canvas);
        playlist.setItems(model.getItems());

        status.textProperty().bind(model.currentTitleProperty());

        playlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    PlaylistEntry current = playlist.getSelectionModel().getSelectedItem();
                    if(current != null) {
                        model.setCurrentMediaInPlayer(current.getFileUri());
                        model.currentPlayListEntryProperty().setValue(current);
                        model.play();
                    }
                }
            }
        });

        progress.progressProperty().bind(model.currentProgressProperty());
        currentDuration.valueProperty().bindBidirectional(model.currentDurationProperty());
        currentDuration.maxProperty().bindBidirectional(model.maxDurationProperty());
        currentDuration.setShowTickLabels(true);

        currentDuration.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(mouseOnSlider) {
                    model.getPlayer().setStartTime(Duration.seconds(newValue.doubleValue()));
                }
            }
        });

        model.currentPlayListEntryProperty().addListener(new ChangeListener<PlaylistEntry>() {
            @Override
            public void changed(ObservableValue<? extends PlaylistEntry> observable, PlaylistEntry oldValue, PlaylistEntry newValue) {
                playlist.getSelectionModel().select(newValue);
            }
        });

        model.volumeProperty().bindBidirectional(volumeSlider.valueProperty());



    }


}
