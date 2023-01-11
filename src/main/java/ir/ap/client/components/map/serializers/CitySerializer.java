package ir.ap.client.components.map.serializers;

public class CitySerializer {

    private int id;
    private String name;
    private boolean dead;
    // private int civId;
    private String civName;
    private int tileId;
    private boolean isCenter;

    public String getName() {
        return name;
    }

    public boolean isDead() {
        return dead;
    }

    public int getId() {
        return id;
    }

    public String getCivName() {
        return civName;
    }

    public int getTileId() {
        return tileId;
    }

    public boolean isCenter() {
        return isCenter;
    }  

    // public int getCivId() {
    //     return civId;
    // }
}
