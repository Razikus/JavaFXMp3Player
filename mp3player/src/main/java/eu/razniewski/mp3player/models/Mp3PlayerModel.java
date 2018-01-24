package eu.razniewski.mp3player.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class Mp3PlayerModel {

    private MediaPlayer player;

    private Media currentMedia;

    private ObservableList<PlaylistEntry> items;

    private BooleanProperty mediaLoaded;

    private StringProperty currentTitle;

    public Mp3PlayerModel() {
        this.items = FXCollections.observableArrayList();
        this.mediaLoaded = new SimpleBooleanProperty(false);
        this.currentTitle = new SimpleStringProperty("");
    }

    public void setCurrentMediaInPlayer(File file) {
        setCurrentMediaInPlayer(file.toURI());
    }

    public void setCurrentMediaInPlayer(URI uri) {
        setCurrentMediaInPlayer(uri.toString());
    }

    public void setCurrentMediaInPlayer(String uri) {
        if(player != null) {
            player.stop();
        }
        currentMedia = new Media(uri);

        player = new MediaPlayer(currentMedia);
        mediaLoaded.setValue(true);
        if(currentMedia.getMetadata().get("title") != null) {
            currentTitle.setValue(currentMedia.getMetadata().get("title").toString());
        } else {
            currentTitle.setValue(Paths.get(URI.create(currentMedia.getSource())).getFileName().toString());
        }
    }

    public boolean play() {
        if(player == null) {
            return false;
        } else {
            player.play();
            return true;
        }
    }

    public boolean stop() {
        if(player == null) {
            return false;
        } else {
            player.stop();
            return true;
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public Media getCurrentMedia() {
        return currentMedia;
    }

    public ObservableList<PlaylistEntry> getItems() {
        return items;
    }


    public boolean isMediaLoaded() {
        return mediaLoaded.get();
    }

    public BooleanProperty mediaLoadedProperty() {
        return mediaLoaded;
    }

    public String getCurrentTitle() {
        return currentTitle.get();
    }

    public StringProperty currentTitleProperty() {
        return currentTitle;
    }
}
