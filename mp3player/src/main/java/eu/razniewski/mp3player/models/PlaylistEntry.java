package eu.razniewski.mp3player.models;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class PlaylistEntry {

    private URI fileUri;
    private String title;

    public PlaylistEntry(URI fileUri, String title) {
        this.fileUri = fileUri;
        this.title = title;
    }

    public PlaylistEntry(File file) {
        this.fileUri = file.toURI();
        this.title = file.getName();
    }

    public URI getFileUri() {
        return fileUri;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistEntry that = (PlaylistEntry) o;
        return Objects.equals(fileUri, that.fileUri) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileUri, title);
    }
}
