package ir.ap.client.components.map;

import javafx.event.EventHandler;

import com.google.gson.JsonObject;
import ir.ap.client.View;
import ir.ap.client.components.Hexagon;
import ir.ap.client.components.Unit;
import ir.ap.client.components.map.serializers.MapSerializer;
import ir.ap.client.components.map.serializers.TileSerializer;
import ir.ap.client.components.map.serializers.UnitSerializer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MapView extends View {

    private static final double TILE_RADIUS = 70;
    private static final double TILE_HEIGHT = TILE_RADIUS / 2 * Math.sqrt(3);
    private static Hexagon[][] hexagons = new Hexagon[1000][1000];
    private static AnchorPane root2;

    @FXML
    private AnchorPane root;

    public void initialize() {
        showCurrentMap();
    }

    public void showCurrentMap() {
        JsonObject mapJson = send("mapShow", currentUsername, 0, 1000, 1000);
        if (!responseOk(mapJson)) {
            System.out.println(getField(mapJson, "msg", String.class));
            enterMain();
            return;
        }
        root.getChildren().clear();
        MapSerializer map = GSON.fromJson(mapJson, MapSerializer.class);
        int height = map.getHeight();
        int width = map.getWidth();
        TileSerializer[][] tiles = map.getMap();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Hexagon tile = getHexagonByTile(tiles[i][j], i, j); // TODO: mishe har dafe new nakard!
                addHexagon(tile);
                if(tile.getTile().getImprovement() != null)tile.showImprovement(root);
                tile.showUnits(root);
                if(tile.getTile().getResource() != null)tile.showResource(root);
                // tiles[ i ][ j ].setTilHexagon(tile);
            }
        }
        root2 = root;
    }

    private void addHexagon(Hexagon tile) {
        root.getChildren().add(tile);
        root.getChildren().add(tile.getImages());
    }

    private Hexagon getHexagonByTile(TileSerializer tile, int i, int j) {
        if( hexagons[ i ][ j ] == null ){
            double mapX = (TILE_RADIUS + 3 * TILE_RADIUS / 2 * tile.getY());
            double mapY = (2 * TILE_HEIGHT * tile.getX() + (tile.getY() % 2 == 0 ? 2 * TILE_HEIGHT : TILE_HEIGHT));
            return hexagons[ i ][ j ] = new Hexagon(mapX, mapY, TILE_RADIUS, tile); 
        }
        hexagons[ i ][ j ].setTile(tile);
        return hexagons[ i ][ j ];
    }

    public static Hexagon[][] getHexagons() {
        return hexagons;
    }

    public static Hexagon getHexagonIntersect(double x, double y){
        for(int i = 0 ; i < 1000 ; i ++){
            for(int j = 0 ; j < 1000 ; j ++){
                if( hexagons[ i ][ j ] == null )continue;
                if( hexagons[ i ][ j ].hasCollision(x, y) ){
                    return hexagons[ i ][ j ];
                }
            }
        }
        return null;
    }

    public static void addMouseEvent(EventHandler e){
        root2.setOnMouseClicked(e);
    }

    public static void removeMouseEvent(){
        root2.setOnMouseClicked(null);
    }

    public boolean isAllUnitsGetAction(){
        for(int i = 0 ; i < 1000 ; i ++){
            for(int j = 0 ; j < 1000 ; j ++){
                if( hexagons[ i ][ j ] == null )continue;
                if( hexagons[ i ][ j ].getTile() == null )continue;
                UnitSerializer cu = hexagons[ i ][ j ].getTile().getCombatUnit();
                UnitSerializer ncu = hexagons[ i ][ j ].getTile().getNonCombatUnit();
                if(cu != null && cu.getUnitAction() == null )return false;
                if( ncu != null && ncu.getUnitAction() == null )return false;
            }
        }
        return true;
    }
}
