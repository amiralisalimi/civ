package ir.ap.client.components.map.panel;

import java.io.IOException;

import ir.ap.client.GameView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MenuView {
    @FXML 
    AnchorPane root;
    
    @FXML
    Label menuLabel;

    public void initialize(){
        root.getStyleClass().add("backgroundColorBR");
        menuLabel.getStyleClass().add("gameText");
    }

    public void continuee(){
        GameView.removeFromGameView(root);
    }

    public void exitGame() throws IOException{
        GameView.exitGame();
    }
}
