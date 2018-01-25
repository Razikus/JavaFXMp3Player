package eu.razniewski.mp3player.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;


public class Mp3PlayerModel {

    private MediaPlayer player;

    private Media currentMedia;

    private ObservableList<PlaylistEntry> items;

    private BooleanProperty mediaLoaded;

    private StringProperty currentTitle;

    private DoubleProperty currentProgress;

    private DoubleProperty currentDuration;

    private DoubleProperty maxDuration;

    private ObjectProperty<PlaylistEntry> currentPlayListEntry;

    private DoubleProperty volume;

    private boolean play = false;

    private Canvas canvas;

    public Mp3PlayerModel(Canvas canvas) {
        this.items = FXCollections.observableArrayList();
        this.mediaLoaded = new SimpleBooleanProperty(false);
        this.currentTitle = new SimpleStringProperty("");
        this.currentProgress = new SimpleDoubleProperty(0.0);
        this.currentDuration = new SimpleDoubleProperty(0.0);
        this.maxDuration = new SimpleDoubleProperty(0.0);
        this.currentPlayListEntry = new SimpleObjectProperty<>();
        this.volume = new SimpleDoubleProperty(0.5);
        this.canvas = canvas;

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

        player.setOnReady(() ->{
            maxDuration.setValue(player.getTotalDuration().toSeconds());
        });


        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                currentDuration.setValue(newValue.toSeconds());
                currentProgress.setValue(newValue.toSeconds() / maxDuration.get());
            }
        });

        player.setOnEndOfMedia(() -> {
            currentProgress.setValue(0);
            player.stop();
            player.setStartTime(Duration.ZERO);

            playNextItem();
        });

        ColorCircle circle = new ColorCircle();

        player.volumeProperty().bind(volumeProperty());
        player.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setStroke(circle.getColor());


                double[] xPoints = new double[magnitudes.length * 2];
                double[] yPoints = new double[magnitudes.length * 2];

                int indices = (int) (canvas.getWidth() / magnitudes.length);
                int xIndex = 0;
                int yIndex = 0;

                for (int i = 0; i < magnitudes.length; i++) {
                    double x = (i * indices) + 5;
                    double x1 = x;
                    double y1 = canvas.getHeight();
                    double x2 = x;
                    double y2 = (Math.abs(magnitudes[i]) * 4) - 80 ;
                    xPoints[xIndex] = x1;
                    xIndex++;
                    xPoints[xIndex] = x2;
                    xIndex++;

                    yPoints[yIndex] = y1;
                    yIndex++;
                    yPoints[yIndex] = y2;
                    yIndex++;
                }

                gc.strokePolygon(xPoints, yPoints, xPoints.length);
            }
        });

    }



    private void playNextItem() {
        Iterator<PlaylistEntry> it = items.iterator();
        boolean next = false;
        while(it.hasNext()) {
            PlaylistEntry entry = it.next();

            if(next) {
                currentPlayListEntryProperty().setValue(entry);
                setCurrentMediaInPlayer(entry.getFileUri());
                player.play();
            }

            if(entry.equals(currentPlayListEntry.get())) {
                next = true;
            }

        }
    }

    public boolean play() {
        if(player == null) {
            return false;
        } else {
            player.play();
            play = true;
            return true;
        }
    }

    public boolean stop() {
        if(player == null) {
            return false;
        } else {
            player.stop();
            player.setStartTime(Duration.ZERO);
            play = false;
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

    public double getCurrentProgress() {
        return currentProgress.get();
    }

    public DoubleProperty currentProgressProperty() {
        return currentProgress;
    }

    public void setCurrentProgress(double currentProgress) {
        this.currentProgress.set(currentProgress);
    }

    public double getCurrentDuration() {
        return currentDuration.get();
    }

    public DoubleProperty currentDurationProperty() {
        return currentDuration;
    }

    public void setCurrentDuration(double currentDuration) {
        this.currentDuration.set(currentDuration);
    }

    public double getMaxDuration() {
        return maxDuration.get();
    }

    public DoubleProperty maxDurationProperty() {
        return maxDuration;
    }

    public void setMaxDuration(double maxDuration) {
        this.maxDuration.set(maxDuration);
    }

    public PlaylistEntry getCurrentPlayListEntry() {
        return currentPlayListEntry.get();
    }

    public ObjectProperty<PlaylistEntry> currentPlayListEntryProperty() {
        return currentPlayListEntry;
    }

    public void setCurrentPlayListEntry(PlaylistEntry currentPlayListEntry) {
        this.currentPlayListEntry.set(currentPlayListEntry);
    }

    public String generatePlaylistContent() {
        Gson gson = new Gson();
        return gson.toJson(items);
    }

    public ArrayList<PlaylistEntry> generatePlaylistFromJson(String json) {
        Gson gson = new Gson();
        ArrayList<PlaylistEntry> entries = gson.fromJson(json, new TypeToken<ArrayList<PlaylistEntry>>(){}.getType());
        return entries;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean pause() {
        if(player == null) {
            return false;
        } else {
            player.stop();
            play = false;
            return true;
        }
    }

    public double getVolume() {
        return volume.get();
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume.set(volume);
    }
}
