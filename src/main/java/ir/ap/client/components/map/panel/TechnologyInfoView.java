package ir.ap.client.components.map.panel;

import ir.ap.client.GameView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TechnologyInfoView {
    // ArrayList<String> improvementsNames = new ArrayList<String>();
    // ArrayList<String> resourcesNames = new ArrayList<String>();
    // ArrayList<String> unitTypesNames = new ArrayList<String>();
    // ArrayList<String> buildingTypesNames = new ArrayList<String>();
    // ArrayList<String> unitActionsNames = new ArrayList<String>();
    // ArrayList<String> technologiesNames = new ArrayList<String>();

    @FXML
    VBox improvements;

    @FXML
    VBox resources;

    @FXML
    VBox unitTypes;

    @FXML
    VBox buildingTypes;

    @FXML
    VBox unitActions;

    @FXML
    VBox technologies;

    @FXML
    MenuButton technologyMenuButton;

    @FXML
    Button researchButton;

    @FXML
    AnchorPane root;

    public void initialize(){

    }

    public void addItemImprovement(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        improvements.getChildren().add(label);
    }
    public void addItemResource(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        resources.getChildren().add(label);
    }
    public void addItemUnitType(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        unitTypes.getChildren().add(label);
    }
    public void addItemBuildingType(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        buildingTypes.getChildren().add(label);
    }
    public void addItemUnitActions(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        unitActions.getChildren().add(label);
    }
    public void addItemTechnologies(String name){
        Label label = new Label(name);
        label.setPrefWidth(307);
        label.setPrefHeight(24);
        label.getStyleClass().add("gameText");
        technologies.getChildren().add(label);
    }   

    public void addTechnologyResearch(String name){
        MenuItem item = new MenuItem(name);
        technologyMenuButton.getItems().add(item);
    }

    public void researchTech(){
        String name = technologyMenuButton.getText();
        if( name.equals("Technology") )return;
        GameView.researchTech(name);
    }

    public void close(){
        GameView.removeFromGameView(root);
    }
}
