package ir.ap.client.components.map.panel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CurrentResearchView {
    @FXML
    private TextField researchName;

    @FXML
    private ImageView researchImageView;

    public void initialize(){
        researchName.setStyle("-fx-control-inner-background: blue;");
    }

    public void setLabel(String text){
        researchName.setText(text);
    }

    public void setImage(Image image){
        researchImageView.setImage(image);
    }
}
