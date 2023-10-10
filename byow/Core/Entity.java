package byow.Core;

import java.util.List;
import java.util.Random;

public class Entity {
    int width;
    int height;
    Coordinate tl;   // top left
    Coordinate tr;   // top right
    Coordinate bl;   // bottom left
    Coordinate br;   // bottom right
    Coordinate direction;
    Coordinate start;
    Entity parent;
    List<Entity> children;
    List<Door> doors;

    /**
     * check whether two entities overlap with each other in the world
     * @param e
     * @return true if yes, false otherwise
     */
    public boolean overlap(Entity e) {
        if (parent == e) {
            return false;
        }
        for (Entity child : parent.children) {
            if (child == e) {
                return false;
            }
        }
        int xLow1 = e.bl.getX();
        int yLow1 = e.bl.getY();
        int xHigh1 = e.br.getX();
        int yHigh1 = e.tl.getY();
        int xLow2 = bl.getX();
        int yLow2 = bl.getY();
        int xHigh2 = br.getX();
        int yHigh2 = tl.getY();
        if (overlap(xLow1, xHigh1, xLow2, xHigh2) && overlap(yLow1, yHigh1, yLow2, yHigh2)) {
            return true;
        }
        return false;
    }

    /**
     * check whether two line segments intersect with each other
     * @param x1----x2,
     *            y1-----y2
     * @return true if intersection, false otherwise
     */
    public boolean overlap(int x1, int x2, int y1, int y2) {
        x1 -= 1;
        x2 += 1;
        y1 -= 1;
        y2 += 1;
        if (between(x1, y1, y2) || between(x2, y1, y2) || between(y1, x1, x2) || between(y2, x1, x2)) {
            return true;
        }
        return false;
    }

    /**
     * check whether x is between a and b, a <= b
     * @return true if yes, false otherwise
     */
    public boolean between(int x, int a, int b) {
        if (x >= a && x <= b) {
            return true;
        }
        return false;
    }

    /**
     * @param rand Random(seed)
     * @param doorDirection direction of the door
     * @return a randomly created door from the room in the specific direction
     */
    public Door createDoor(Random rand, Coordinate doorDirection) {
        if (doorDirection.equals(Coordinate.LEFT)) {
            Coordinate randPosition = bl.changeY(RandomUtils.uniform(rand, 0, height));
            Coordinate doorPosition = randPosition.add(doorDirection);
            return new Door(doorPosition, doorDirection, this);
        } else if (doorDirection.equals(Coordinate.UP)) {
            Coordinate randPosition = tl.changeX(RandomUtils.uniform(rand, 0, width));
            Coordinate doorPosition = randPosition.add(doorDirection);
            return new Door(doorPosition, doorDirection, this);
        } else if (doorDirection.equals(Coordinate.DOWN)) {
            Coordinate randPosition = bl.changeX(RandomUtils.uniform(rand, 0, width));
            Coordinate doorPosition = randPosition.add(doorDirection);
            return new Door(doorPosition, doorDirection, this);
        } else if (doorDirection.equals(Coordinate.RIGHT)) {
            Coordinate randPosition = br.changeY(RandomUtils.uniform(rand, 0, height));
            Coordinate doorPosition = randPosition.add(doorDirection);
            return new Door(doorPosition, doorDirection, this);
        } else {
            return null;
        }
    }

    public Coordinate getLight() {
        return null;
    }

    public List<Door> getDoors() {
        return doors;
    }
}
