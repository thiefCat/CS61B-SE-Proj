package byow.Core;

import java.util.List;
import java.util.Random;

public class Coordinate {
    public static final Coordinate UP = new Coordinate(0, 1);
    public static final Coordinate DOWN = new Coordinate(0, -1);
    public static final Coordinate RIGHT = new Coordinate(1, 0);
    public static final Coordinate LEFT = new Coordinate(-1, 0);
    private int x;
    private int y;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public Coordinate changeX(int num) {
        return new Coordinate(x + num, y);
    }
    public Coordinate changeY(int num) {
        return new Coordinate(x, y + num);
    }
    public Coordinate add(Coordinate c) {
        return new Coordinate(x + c.x, y + c.y);
    }

    public Coordinate multiply(int k) {
        return new Coordinate(k * x, k * y);
    }

    /**
     * @param distance the distance of the move
     * @param direction the direction of the move
     * @return the destination coordinate
     */
    public Coordinate move(int distance, Coordinate direction) {
        return this.add(direction.multiply(distance));
    }

    public static Coordinate sampleDirection(Random rand) {
        List<Coordinate> directions = List.of(UP, DOWN, RIGHT, LEFT);
        int randIndex = RandomUtils.uniform(rand, 0, directions.size());
        return directions.get(randIndex);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate coor) {
            if (this.x == coor.x && this.y == coor.y) {
                return true;
            }
        }
        return false;
    }
}
