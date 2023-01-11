package ir.ap.client.components.map.panel;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import ir.ap.client.GameView;
import ir.ap.client.components.Hexagon;
import ir.ap.client.components.map.MapView;
import ir.ap.client.components.map.serializers.TileSerializer;
import ir.ap.client.components.map.serializers.UnitSerializer;
import ir.ap.model.Map;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class UnitActionsView {
    @FXML
    Button move;

    @FXML
    Button sleep;
    
    @FXML
    Button alert;

    @FXML
    Button wake;

    @FXML
    Button delete;

    @FXML
    Button cancel;

    @FXML
    Button foundCity;

    @FXML
    Button buildRoad;

    @FXML
    Button removeRoad;

    @FXML
    Button clearLand;

    @FXML
    MenuButton lands;

    @FXML
    Button buildImprovement;

    @FXML
    Button repairImprovement;

    @FXML
    MenuButton improvements;

    @FXML
    Button fortify;

    @FXML
    Button garrison;

    @FXML
    Button rangeAttack;

    @FXML
    Button meleeAttack;

    @FXML
    Button rangeSetup;

    @FXML
    Button pillage;

    @FXML
    AnchorPane root;

    UnitSerializer unitSerializer;
    String currentUsername;

    public void initialize(){
        root.getStyleClass().add("backgroundColorBR");
        Stream.of(move, sleep, wake, alert, delete, cancel, foundCity, buildRoad, removeRoad, clearLand, lands,
            buildImprovement, repairImprovement, improvements, fortify, garrison, rangeAttack, meleeAttack,
            pillage, rangeSetup).forEach( button -> button.getStyleClass().add("gameButton"));
            Stream.of(foundCity, buildRoad, removeRoad, clearLand, lands,
            buildImprovement, repairImprovement, improvements, fortify, garrison, rangeAttack, meleeAttack,
            pillage, rangeSetup).forEach( button -> button.setPrefWidth(135));
    }

    public void setUnit(UnitSerializer unitSerializer, String currentUsername){
        this.unitSerializer = unitSerializer;
        this.currentUsername = currentUsername;
        TileSerializer tile = GameView.getTileById(unitSerializer.getTileId());
        if( unitSerializer.getUnitAction() == null )cancel.setVisible(false);
        if( unitSerializer.getUnitType().equals("WORKER") ){
            Stream.of(foundCity, fortify, garrison, rangeAttack, meleeAttack, pillage, rangeSetup).forEach(button -> button.setVisible(false));
            if( GameView.tileHasRoadOrRailRoad(unitSerializer.getTileId()) ){
                buildRoad.setVisible(false);
            }else{
                removeRoad.setVisible(false);
            }
            lands.getItems().clear();
            int featureId = GameView.getTerrainFeature(unitSerializer.getTileId());
            if( featureId == 4 ){
                lands.getItems().add(new MenuItem("JUNGLE"));
            }else if( featureId == 8 ){
                lands.getItems().add(new MenuItem("FOREST"));
            }else if( featureId == 11 ){
                lands.getItems().add(new MenuItem("MARSH"));
            }
            improvements.getItems().clear();
            if(tile.getImprovement() == null){
                repairImprovement.setVisible(false);
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 1) ){
                    MenuItem item = new MenuItem("CAMP");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 2) ){
                    MenuItem item = new MenuItem("FARM");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(),3) ){
                    MenuItem item = new MenuItem("LUMBER_MILL");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 4) ){
                    MenuItem item = new MenuItem("MINE");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 5) ){
                    MenuItem item = new MenuItem("PASTURE");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 6) ){
                    MenuItem item = new MenuItem("PLANTATION");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 7) ){
                    MenuItem item = new MenuItem("QUARRY");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 8) ){
                    MenuItem item = new MenuItem("TRADING_POST");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
                if( GameView.tileCanBuildImprovement(tile.getIndex(), 9) ){
                    MenuItem item = new MenuItem("FACTORY");
                    improvements.getItems().add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event){
                            improvements.setText(item.getText());
                        }
                    });
                }
            }
            else{
                buildImprovement.setVisible(false);
            }
        }else if( unitSerializer.getUnitType().equals("SETTLER") ){
            Stream.of(buildRoad, removeRoad, clearLand, lands, buildImprovement, repairImprovement, improvements,
            fortify, garrison, rangeAttack, meleeAttack, pillage, rangeSetup).forEach(button -> button.setVisible(false));
        }else{
            Stream.of(buildRoad, removeRoad, clearLand, lands, buildImprovement, repairImprovement, improvements,
            foundCity).forEach(button -> button.setVisible(false));
            if(unitSerializer.getCombatType().equals("MELEE")){
                rangeAttack.setVisible(false);
                rangeSetup.setVisible(false);
            }else{
                meleeAttack.setVisible(false);
            }
        }
    }

    public void move(){
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getEventType().toString().equals("MOUSE_CLICKED") ){
                    Hexagon hexagon = MapView.getHexagonIntersect(event.getX(), event.getY());
                    if( hexagon != null ){
                        if(GameView.unitAction("unitMoveTo", currentUsername, hexagon.getTile().getIndex(), false)){
                            GameView.updateGame();
                        }
                    }
                    MapView.removeMouseEvent();
                }                
            }
        };
        MapView.addMouseEvent(mouseHandler);
    }

    public void sleep(){
        GameView.unitAction("unitSleep", currentUsername);
    }

    public void wake(){
        GameView.unitAction("unitWake", currentUsername);
    }

    public void alert(){
        GameView.unitAction("unitAlert", currentUsername);
    }

    public void delete(){
        if(GameView.unitAction("unitDelete", currentUsername)){
            GameView.removeUnitInfoPanel();
            GameView.updateGame();            
        }
    }

    public void cancel(){
        if(GameView.unitAction("unitCancelMission", currentUsername)){
            GameView.updateGame();
        }
    }

    public void foundCity(){
        if(GameView.unitAction("unitFoundCity", currentUsername, false)){
            GameView.updateGame();
        }
    }

    public void buildRoad(){
        if(GameView.unitAction("unitBuildRoad", currentUsername, false)){
            GameView.updateGame();
        }
    }

    public void removeRoad(){
        if( GameView.unitAction("unitRemoveRoute", currentUsername, false)){
            GameView.updateGame();
        }
    }

    public void clearLand(){
        // check if lands is empty
        if( lands.getText().equals("Land") )return;
        String method = "";
        if( lands.getText().equals("JUNGLE") )method = "unitRemoveJungle";
        else if( lands.getText().equals("FOREST") )method = "unitRemoveForest";
        else if(lands.getText().equals("MARSH") )method = "unitRemoveMarsh";
        if( GameView.unitAction(method, currentUsername, false) ){
            GameView.updateGame();
        }
    }

    public void buildImprovement(){
        if( improvements.getText().equals("Improvement") )return;
        int impId = 0;
        String impName = improvements.getText();
        if(impName.equals("CAMP"))impId = 1;
        else if(impName.equals("FARM"))impId = 2;
        else if(impName.equals("LUMBER_MILL"))impId = 3;
        else if(impName.equals("MINE"))impId = 4;
        else if(impName.equals("PASTURE"))impId = 5;
        else if(impName.equals("PLANTATION"))impId = 6;
        else if(impName.equals("QUARRY"))impId = 7;
        else if(impName.equals("TRADING_POST"))impId = 8;
        else if(impName.equals("FACTORY"))impId = 9;
        if( GameView.unitAction("unitBuildImprovement", currentUsername, impId, false) ){
            GameView.updateGame();
        }
    }

    public void repairImprovement(){
        if( improvements.getText().equals("Improvement") )return;
        if( GameView.unitAction("unitRepair", currentUsername, false) ){
            GameView.updateGame();
        }
    }

    public void fortify(){
        GameView.unitAction("unitFortify", currentUsername);
    }

    public void garrison(){
        GameView.unitAction("unitGarrison", currentUsername);
    }

    public void pillage(){
        //TODO: tabe sho toye gameController nadarim
    }

    public void rangeSetup(){
        if(GameView.unitAction("unitSetupRanged", currentUsername)){
            GameView.updateGame();
        }
    }

    public void rangeAttack(){
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getEventType().toString().equals("MOUSE_CLICKED") ){
                    Hexagon hexagon = MapView.getHexagonIntersect(event.getX(), event.getY());
                    if( hexagon != null ){
                        if( (hexagon.getCombatUnit() != null && hexagon.getCombatUnit().hasCollision(event.getX(), event.getY())) || (hexagon.getNonCombatUnit() != null && hexagon.getNonCombatUnit().hasCollision(event.getX(), event.getY())) ){
                            if(GameView.unitAction("unitAttack", currentUsername, hexagon.getTile().getIndex(), false)){
                                GameView.updateGame();
                            }
                        }else if( hexagon.cityHasCollision(event.getX(), event.getY()) ){
                            if(GameView.unitAction("cityAttack", currentUsername, hexagon.getTile().getIndex(), false)){
                                GameView.updateGame();
                            }
                        } 
                    }
                    MapView.removeMouseEvent();
                }                
            }
        };
        MapView.addMouseEvent(mouseHandler);
    }

    public void meleeAttack(){
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getEventType().toString().equals("MOUSE_CLICKED") ){
                    Hexagon hexagon = MapView.getHexagonIntersect(event.getX(), event.getY());
                    if( hexagon != null ){
                        if( (hexagon.getCombatUnit() != null && hexagon.getCombatUnit().hasCollision(event.getX(), event.getY())) || (hexagon.getNonCombatUnit() != null && hexagon.getNonCombatUnit().hasCollision(event.getX(), event.getY())) ){
                            if(GameView.unitAction("unitAttack", currentUsername, hexagon.getTile().getIndex(), false)){
                                GameView.updateGame();
                            }
                        }else if( hexagon.cityHasCollision(event.getX(), event.getY()) ){
                            if(GameView.unitAction("cityAttack", currentUsername, hexagon.getTile().getIndex(), false)){
                                GameView.updateGame();
                            }
                        } 
                    }
                    MapView.removeMouseEvent();
                }                
            }
        };
        MapView.addMouseEvent(mouseHandler);
    }

}
