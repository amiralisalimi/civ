package ir.ap.model;

public enum Direction {
    UP(0),
    UP_RIGHT(1),
    DOWN_RIGHT(2),
    DOWN(3),
    DOWN_LEFT(4),
    UP_LEFT(5);

    private final int id;

    Direction(int id) {
        this.id = id;
    }

    public static Direction getDirectionById(int id) {
        switch (id) {
            case 0:
                return UP;
            case 1:
                return UP_RIGHT;
            case 2:
                return DOWN_RIGHT;
            case 3:
                return DOWN;
            case 4:
                return DOWN_LEFT;
            case 5:
                return UP_LEFT;
            default:
                return null;
        }
    }

    public int getId() {
        return id;
    }
}
