package ir.ap.client.components;

import java.io.IOException;

import ir.ap.client.App;
import ir.ap.client.GameView;
import ir.ap.client.components.map.serializers.UnitSerializer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Unit extends Circle{

    public static int UNIT_SIZE = 25;

    private UnitSerializer unitSerializer;

    public Unit(double x, double y, double r, UnitSerializer unitSerializer){
        super(x, y, r);
        this.unitSerializer = unitSerializer;        
        try{
            setBackground("png/civAsset/units/Units/" + lowerCaseString(unitSerializer.getUnitType()) + ".png");
        }catch(Exception ex){
            System.out.println("salam");
            System.out.println(lowerCaseString(unitSerializer.getUnitType()));
        }
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override 
            public void handle(MouseEvent mouseEvent) {
                try {
                    GameView.showUnitInfoPanel(unitSerializer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public boolean hasCollision(double x, double y){
        Circle point = new Circle(x, y, 1);
        return this.getBoundsInParent().intersects(point.getBoundsInParent());
    }

    public String lowerCaseString(String s1){
        String s2 = s1.toLowerCase();
        String s3 = Character.toUpperCase(s2.charAt(0)) + s2.substring(1);
        return s3;
    }

    public void setBackground(String path){
        this.setFill(new ImagePattern(new Image(String.valueOf(App.class.getResource(path))))) ;
    }

    public UnitSerializer getUnitSerializer() {
        return unitSerializer;
    }

    public void setUnitSerializer(UnitSerializer unitSerializer) {
        this.unitSerializer = unitSerializer;
    }
}
