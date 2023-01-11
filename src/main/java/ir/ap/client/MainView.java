package ir.ap.client;

import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

public class MainView extends View {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label message;

    public void initialize() {
        if (currentUsername != null) {
            usernameLabel.setText(currentUsername);
        }
    }

    public void enterLaunchGame() {
        try {
            send("launchGame");
            App.setRoot("fxml/launch-game-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterGame() {
        try {
            App.setRoot("fxml/game-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterScoreboard() {
        try {
            App.setRoot("fxml/scoreboard-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterChat() {
        try {
            App.setRoot("fxml/chat-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void continueGame() {
        JsonObject result = send("continueGame", currentUsername);
        if (responseOk(result)) {
            enterGame();
        } else {
            error("No game in progress");
        }
    }

    private void error(String msg) {
        message.setText(msg);
        message.setTextFill(Color.RED);
    }
}
