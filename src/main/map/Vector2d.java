package map;

import java.util.Objects;

public class Vector2d {
    public int x;
    public int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public boolean precedes(Vector2d other) {
        if (other.x >= this.x && other.y >= this.y) return true;
        else return false;
    }

    public boolean follows(Vector2d other) {
        if (other.x <= this.x && other.y <= this.y) return true;
        else return false;
    }

    public Vector2d upperRight(Vector2d other) {
        Vector2d upper = new Vector2d(Math.max(this.x, other.x), Math.max(this.y, other.y));
        return upper;
    }

    public Vector2d lowerLeft(Vector2d other) {
        Vector2d lower = new Vector2d(Math.min(this.x, other.x), Math.min(this.y, other.y));
        return lower;
    }

    public Vector2d add(Vector2d other) {
        Vector2d sum = new Vector2d(this.x + other.x, this.y + other.y);
        return sum;
    }

    public Vector2d subtract(Vector2d other) {
        Vector2d difference = new Vector2d(this.x - other.x, this.y - other.y);
        return difference;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;

        Vector2d that = (Vector2d) other;
        if (!(other instanceof Vector2d))
            return false;
        else {
            if (this.x == that.x && this.y == that.y) {
                return true;
            }
        }
        return false;
    }


    public Vector2d opposite() {
        Vector2d oppositeV = new Vector2d(-(this.x), -(this.y));
        return oppositeV;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

}