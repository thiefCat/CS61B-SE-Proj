package byow.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hallway extends Entity {
    private int length;
    private Coordinate end;
    public Hallway(Coordinate start, Coordinate direction, int length, Entity parent, Random rand) {
        this.start = start;
        this.direction = direction;
        this.length = length;
        this.doors = new ArrayList<>();
        this.end = start.move(length - 1, direction);
        this.parent = parent;
        this.children = new ArrayList<>();
        if (direction.equals(Coordinate.DOWN) || direction.equals(Coordinate.UP)) {
            this.width = 1;
            this.height = length;
        } else {
            this.width = length;
            this.height = 1;
        }
        createHallwayCorners(direction);
        createDoors(rand);
        if (parent != null) {
            parent.children.add(this);
        }
    }
    private void createHallwayCorners(Coordinate direction) {
        Coordinate up = Coordinate.UP;
        Coordinate down = Coordinate.DOWN;
        Coordinate right = Coordinate.RIGHT;
        Coordinate left = Coordinate.LEFT;
        if (direction.equals(up)) {
            this.tl = end;
            this.tr = end;
            this.br = start;
            this.bl = start;
        } else if (direction.equals(down)) {
            this.tl = start;
            this.tr = start;
            this.br = end;
            this.bl = end;
        } else if (direction.equals(right)) {
            this.tl = start;
            this.tr = end;
            this.bl = start;
            this.br = end;
        } else if (direction.equals(left)) {
            this.tl = end;
            this.tr = start;
            this.bl = end;
            this.br = start;
        } else {
            this.tl = null;
            this.tr = null;
            this.bl = null;
            this.br = null;
        }
    }

    /**
     * Randomly sample 4 doors
     * Return a list of doors of the room
     */
    private List<Door> createDoors(Random rand) {
        Coordinate up = Coordinate.UP;
        Coordinate down = Coordinate.DOWN;
        Coordinate right = Coordinate.RIGHT;
        Coordinate left = Coordinate.LEFT;
        if (direction.equals(up)) {
            doors.add(createDoor(rand, left));
            doors.add(createDoor(rand, right));
            doors.add(createDoor(rand, up));
        } else if (direction.equals(down)) {
            doors.add(createDoor(rand, left));
            doors.add(createDoor(rand, down));
            doors.add(createDoor(rand, right));
        } else if (direction.equals(right)) {
            doors.add(createDoor(rand, right));
            doors.add(createDoor(rand, up));
            doors.add(createDoor(rand, down));
        } else {
            doors.add(createDoor(rand, left));
            doors.add(createDoor(rand, up));
            doors.add(createDoor(rand, down));
        }
        return doors;
    }

    public Coordinate getEnd() {
        return end;
    }
}
