package ir.ap.client.components;

import ir.ap.client.App;
import ir.ap.client.GameView;
import ir.ap.client.components.map.serializers.RiverSerializer;
import ir.ap.client.components.map.serializers.TileSerializer;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.stream.Stream;

public class Hexagon extends Polygon {

    private static double RADIUS;
    private static double HEIGHT;

    public static double RESOURCE_SIZE = 40;
    public static double IMPROVEMENT_SIZE = 40;
    public static double CITY_SIZE = 50;

    TileSerializer tile;
    Unit combatUnit;
    Unit nonCombatUnit;
    ImageView improvementImage;
    ImageView cityImage;
    double x;
    double y;

    StackPane images = new StackPane();

    public Hexagon(double x, double y, double r) {
        this.x = x;
        this.y = y;
        double h = r / 2 * Math.sqrt(3);
        RADIUS = r;
        HEIGHT = h;
        getPoints().addAll(
                x - r / 2, y - h,
                x + r / 2, y - h,
                x + r, y,
                x + r / 2, y + h,
                x - r / 2, y + h,
                x - r, y
        );
        setStrokeWidth(1);
        setStroke(Color.GRAY);
        images.setLayoutX(x - r);
        images.setLayoutY(y - h);
        images.setMaxWidth(2 * r);
        images.setMaxHeight(2 * h);
    }
    
    public void setCombatUnit(Unit combatUnit) {
        this.combatUnit = combatUnit;
    }

    public void setNonCombatUnit(Unit nonCombatUnit) {
        this.nonCombatUnit = nonCombatUnit;
    }

    public ImageView getImprovementImage() {
        return improvementImage;
    }

    public void setImprovementImage(ImageView improvementImage) {
        this.improvementImage = improvementImage;
    }

    public ImageView getCityImage() {
        return cityImage;
    }

    public void setCityImage(ImageView cityImage) {
        this.cityImage = cityImage;
    }

    public Hexagon(double x, double y, double r, TileSerializer tile) {
        this(x, y, r);
        this.setTile(tile);
        Hexagon me = this;
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                if( (me.combatUnit != null && me.combatUnit.hasCollision(x, y)) || (me.nonCombatUnit != null && me.nonCombatUnit.hasCollision(x, y)) || (cityImage != null && me.cityHasCollision(x, y)) ){
                    return;
                }
                GameView.showTileInfoPanel(tile);        
            }
        });
    }
    
    public TileSerializer getTile() {
        return tile;
    }

    public boolean hasCollision(double x, double y){
        Circle point = new Circle(x, y, 1);
        return this.getBoundsInParent().intersects(point.getBoundsInParent());
    }

    public void showUnits(AnchorPane root){
        if(tile.getCombatUnit() != null){
            showCombatUnit(x + (RADIUS/4), y + (HEIGHT/2), tile);
            root.getChildren().add( this.combatUnit );
        }
        if(tile.getNonCombatUnit() != null){
            showNonCombatUnit(x - (RADIUS/4), y + (HEIGHT/2), tile);
            root.getChildren().add( this.nonCombatUnit );
        }
    }

    public void showCombatUnit(double x, double y, TileSerializer tile1){
        if( this.combatUnit == null ){
            this.combatUnit = new Unit(x, y, (double)Unit.UNIT_SIZE, tile1.getCombatUnit());
        }
    }

    public void showNonCombatUnit(double x, double y, TileSerializer tile1){
        if( this.nonCombatUnit == null ){
            this.nonCombatUnit = new Unit(x, y, (double)Unit.UNIT_SIZE, tile1.getNonCombatUnit());
        }
    }

    public void showImprovement(AnchorPane root){
        improvementImage = new ImageView();
        try {
            improvementImage = new ImageView(new Image(App.class.getResource("png/civAsset/icons/ImprovementIcons/" + lowerCaseString(tile.getImprovement().getName()) + ".png").toExternalForm()));            
        } catch (Exception e) {
            System.out.println(tile.getImprovement().getName() + "not found!");            
        }    
        improvementImage.setFitWidth(IMPROVEMENT_SIZE);
        improvementImage.setFitHeight(IMPROVEMENT_SIZE);
        improvementImage.setLayoutX(x + (RADIUS/2) - (IMPROVEMENT_SIZE/2));
        improvementImage.setLayoutY(y - (IMPROVEMENT_SIZE/2));
        if(improvementImage != null){
            root.getChildren().add(improvementImage);
            improvementImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    //TODO: in ejbarie?
                }
            });
        }    
    }    

    public void showResource(AnchorPane root){
        ImageView resourcImage = new ImageView();
        try{
            resourcImage = new ImageView(new Image(App.class.getResource("png/civAsset/icons/ResourceIcons/" + lowerCaseString(tile.getResource().getName()) + ".png").toExternalForm()));
        }catch(Exception ex){
            System.out.println(tile.getResource().getName() + "not found!");
        }    
        resourcImage.setFitWidth(RESOURCE_SIZE);
        resourcImage.setFitHeight(RESOURCE_SIZE);
        resourcImage.setLayoutX(x - (RADIUS/2) - (RESOURCE_SIZE/2));
        resourcImage.setLayoutY(y - (RESOURCE_SIZE/2));
        if(resourcImage != null)
            root.getChildren().add(resourcImage);
    }        

    public Unit getCombatUnit() {
        return combatUnit;
    }

    public Unit getNonCombatUnit() {
        return nonCombatUnit;
    }

    public void setTile(TileSerializer tile) {
        this.tile = tile;
        this.images.getChildren().clear();
        loadFill();
    }

    public StackPane getImages() {
        return images;
    }

    public String lowerCaseString(String s1){
        String s2 = s1.toLowerCase();
        String s3 = Character.toUpperCase(s2.charAt(0)) + s2.substring(1);
        return s3;
    }

    public void loadFill() {
        ImageView terrainImage = new ImageView(getTerrainTypeImage(getTile().getTerrainTypeId()));
        ImageView featureImage = new ImageView(getTerrainFeatureImage(getTile().getTerrainFeatureId()));
        ImageView fogImage = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/FogOfWar.png").toExternalForm()));
        ImageView revealed = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/Revealed.png").toExternalForm()));
        ImageView riverBottom = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/River-Bottom.png").toExternalForm()));
        ImageView riverBottomRight = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/River-BottomRight.png").toExternalForm()));
        ImageView riverBottomLeft = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/River-BottomLeft.png").toExternalForm()));
        cityImage = null;
        if( getTile().getCityInTile() != null ){
            if( getTile().getCityInTile().isCenter() == true ){
                cityImage = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/City center-" + lowerCaseString(GameView.getEra()) + " era.png").toExternalForm()));            
            }else{
                cityImage = new ImageView(new Image(App.class.getResource("png/civAsset/map/Tiles/city.png").toExternalForm()));
            }
            cityImage.setFitWidth(CITY_SIZE);
            cityImage.setFitHeight(CITY_SIZE);
        }
        Stream.of(terrainImage, featureImage, fogImage, revealed, riverBottom, riverBottomLeft, riverBottomRight).forEach( image -> 
        image.setFitWidth(2 * RADIUS));
        Stream.of(terrainImage, featureImage, fogImage, revealed, riverBottom, riverBottomLeft, riverBottomRight).forEach( image -> 
        image.setFitHeight(2 * HEIGHT));
        setFill(Color.WHITE);
        if (terrainImage != null)
            images.getChildren().add(terrainImage);
        if (featureImage != null)
            images.getChildren().add(featureImage);
        if (getTile().getHasRiver() != null){
            RiverSerializer riverSerializer = getTile().getHasRiver();
            if(riverSerializer.getDown() == true)images.getChildren().add(riverBottom);
            if(riverSerializer.getDownLeft() == true)images.getChildren().add(riverBottomLeft);
            if(riverSerializer.getDownRight() == true)images.getChildren().add(riverBottomRight);
        }
        if (cityImage != null){
            images.getChildren().add(cityImage);
            cityImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    GameView.showCityProductConstructionPanel();
                }
            });
        }
        if (getTile().getKnowledge().equals("FOG_OF_WAR"))
            images.getChildren().add(fogImage);
        if (getTile().getKnowledge().equals("REVEALED"))
            images.getChildren().add(revealed);
    }
    
    public boolean cityHasCollision(double x, double y){
        if( cityImage == null )return false;
        Circle point = new Circle(x, y, 1);
        return cityImage.getBoundsInParent().intersects(point.getBoundsInParent());
    }


    private Image getTerrainTypeImage(int terrainTypeId) {
        String addr = "png/civAsset/map/Tiles/";
        switch (terrainTypeId) {
            case 2:
                return new Image(App.class.getResource(addr + "Desert.png").toExternalForm());
            case 3:
                return new Image(App.class.getResource(addr + "Grassland.png").toExternalForm());
            case 4:
                return new Image(App.class.getResource(addr + "Hill.png").toExternalForm());
            case 5:
                return new Image(App.class.getResource(addr + "Mountain.png").toExternalForm());
            case 6:
                return new Image(App.class.getResource(addr + "Ocean.png").toExternalForm());
            case 7:
                return new Image(App.class.getResource(addr + "Plains.png").toExternalForm());
            case 8:
                return new Image(App.class.getResource(addr + "Snow.png").toExternalForm());
            case 9:
                return new Image(App.class.getResource(addr + "Tundra.png").toExternalForm());
            default:
                return null;
        }
    }

    private Image getTerrainFeatureImage(int terrainFeatureId) {
        String addr = "png/civAsset/map/Tiles/";
        switch (terrainFeatureId) {
            case 3:
                return new Image(App.class.getResource(addr + "Flood plains.png").toExternalForm());
            case 4:
                return new Image(App.class.getResource(addr + "Jungle.png").toExternalForm());
            case 7:
                return new Image(App.class.getResource(addr + "Ice.png").toExternalForm());
            case 8:
                return new Image(App.class.getResource(addr + "Forest.png").toExternalForm());
            case 11:
                return new Image(App.class.getResource(addr + "Marsh.png").toExternalForm());
            case 13:
                return new Image(App.class.getResource(addr + "Oasis.png").toExternalForm());
            default:
                return null;
        }
    }
}
