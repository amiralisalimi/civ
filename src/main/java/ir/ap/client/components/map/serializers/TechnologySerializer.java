package ir.ap.client.components.map.serializers;

import java.util.ArrayList;

public class TechnologySerializer {
    private String name;
    private int id;
    private int cost;
    private ArrayList<Enum<?>> objectsUnlocks;
    private int turnsLeftForFinish;

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public int getCost() {
        return cost;
    }
    public ArrayList<Enum<?>> getObjectsUnlocks() {
        return objectsUnlocks;
    }
    public int getTurnsLeftForFinish() {
        return turnsLeftForFinish;
    }  
}
