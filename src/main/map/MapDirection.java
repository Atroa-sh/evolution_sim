package map;

public enum MapDirection {

    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTHEAST,
    NORTHWEST,
    SOUTHEAST,
    SOUTHWEST;

    public MapDirection turn(int turn) {
        int i;
        MapDirection[] movesInOrder = {NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
        for (i = 0; i < movesInOrder.length; i++) {
            if (movesInOrder[i] == this) break;
        }
        return movesInOrder[(i + turn) % movesInOrder.length];
    }

    public Vector2d toUnitVector() {
        switch (this) {
            case NORTH:
                return new Vector2d(0, 1);
            case EAST:
                return new Vector2d(1, 0);
            case WEST:
                return new Vector2d(-1, 0);
            case SOUTH:
                return new Vector2d(0, -1);
            case NORTHEAST:
                return new Vector2d(1, 1);
            case NORTHWEST:
                return new Vector2d(-1, 1);
            case SOUTHEAST:
                return new Vector2d(1, -1);
            case SOUTHWEST:
                return new Vector2d(-1, -1);
            default:
                return null;
        }
    }

    public String toString() {
        switch (this) {
            case NORTH:
                return "North";
            case EAST:
                return "East";
            case WEST:
                return "West";
            case SOUTH:
                return "South";
            case NORTHEAST:
                return "NorthEast";
            case NORTHWEST:
                return "NorthWest";
            case SOUTHEAST:
                return "SouthEast";
            case SOUTHWEST:
                return "SouthWest";
            default:
                return null;
        }
    }

}
