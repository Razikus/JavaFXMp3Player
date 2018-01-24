package eu.razniewski.mp3player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Mp3Player extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(getClass().getResource("/fxml/Window.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load: " + getClass().getResource("/fxml/Window.fxml"));
            System.exit(1);
        }

        Scene scene = new Scene(root);

        primaryStage.setTitle("MP3 Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
