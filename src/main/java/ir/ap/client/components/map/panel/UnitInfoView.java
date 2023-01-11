package ir.ap.client.components.map.panel;

import ir.ap.client.App;
import ir.ap.client.components.map.serializers.UnitSerializer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;

public class UnitInfoView {
    @FXML
    private AnchorPane root;

    @FXML
    private ImageView unitImageView;

    @FXML
    private TextField unitName;

    @FXML
    private TextField combatStrenght;

    @FXML
    private TextField movement;

    @FXML
    private TextField health;

    private UnitSerializer unitSerializer;

    public void initialize(){
        root.getStyleClass().add("backgroundColorB");
        unitName.getStyleClass().add("gameText");
        movement.getStyleClass().add("gameText");
        health.getStyleClass().add("gameText");
        combatStrenght.getStyleClass().add("gameText");
    }

    public UnitSerializer getUnitSerializer() {
        return unitSerializer;
    }

    public void setUnitSerializer(UnitSerializer unitSerializer) {
        this.unitSerializer = unitSerializer;
        this.unitName.setText(unitSerializer.getUnitType());
        this.movement.setText("Movement " + unitSerializer.getMp() + "/" + unitSerializer.getMaxMp());
        this.health.setText("Health " + unitSerializer.getHp());
        this.combatStrenght.setText("Combat Strenght " + unitSerializer.getCombatStrenght());
        this.unitImageView.setImage(new Image(App.class.getResource("png/civAsset/units/Units/" + lowerCaseString(unitSerializer.getUnitType()) + ".png").toExternalForm()));
    }

    public String lowerCaseString(String s1){
        String s2 = s1.toLowerCase();
        String s3 = Character.toUpperCase(s2.charAt(0)) + s2.substring(1);
        return s3;
    }
}
