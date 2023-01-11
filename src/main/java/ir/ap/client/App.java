package ir.ap.client;

import ir.ap.client.components.UserSerializer;
import ir.ap.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;

public class App extends Application {

    public static final int SERVER_PORT = 8000;

    public static final double SCREEN_WIDTH = 1024;
    public static final double SCREEN_HEIGHT = 576;

    private static Scene scene;
    private static Stage stage;

    private static MediaPlayer menuMusic;
    private static MediaPlayer launchGameMusic;

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    private static Parent loadFXML(String fxml, boolean currentView) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Parent result = fxmlLoader.load();
        if (currentView)
            View.currentView = fxmlLoader.getController();
        return result;
    }

    public static void setRoot(String fxml) throws IOException {
        if( fxml.equals("fxml/launch-game-view.fxml") ){
            if (menuMusic != null)
                menuMusic.stop();
            launchGameMusic = new MediaPlayer(new Media(App.class.getResource("png/civAsset/Sounds/LaunchGameMusic.mp3").toExternalForm()));
            launchGameMusic.setAutoPlay(true);
            launchGameMusic.play();
        }
        else if( fxml.equals("fxml/game-view.fxml") ){
            if (launchGameMusic != null)
                launchGameMusic.stop();
        }
        scene.setRoot(loadFXML(fxml, true));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void exit() {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;
        scene = new Scene(loadFXML("fxml/login-view.fxml", true), SCREEN_WIDTH, SCREEN_HEIGHT);
        scene.getStylesheets().add(GameView.class.getResource("css/styles.css").toExternalForm());
        menuMusic = new MediaPlayer(new Media(App.class.getResource("png/civAsset/Sounds/MenuMusic.mp3").toExternalForm()));
        menuMusic.setAutoPlay(true);
        menuMusic.play();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Civ");
        stage.show();
    }
}
