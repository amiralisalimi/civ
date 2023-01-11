package ir.ap.client.components.map.serializers;

public class MapSerializer {
    private int height;
    private int width;

    private int focusId;
    private int focusX;
    private int focusY;

    private TileSerializer[][] map;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getFocusId() {
        return focusId;
    }

    public int getFocusX() {
        return focusX;
    }

    public int getFocusY() {
        return focusY;
    }

    public TileSerializer[][] getMap() {
        return map;
    }
}
