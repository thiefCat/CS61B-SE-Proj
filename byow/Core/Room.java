package byow.Core;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class Room represents a rectangle entity in the world
 * @params start: a coordinate indicating the pixel that is extended from the hallway by one move
 *         position: an integer which is randomly sampled from [0, l) which indicating the
 *                   position of start on that edge
 */
public class Room extends Entity {
    private int position;
    private Coordinate light;
    public Room(Coordinate start, Coordinate direction, int width, int height,
                int position, Entity parent, Random rand) {
        this.start = start;
        this.direction = direction;
        this.width = width;
        this.height = height;
        this.doors = new ArrayList<>();
        this.position = position;
        this.parent = parent;
        this.children = new ArrayList<>();
        createRoomCorners();
        createDoors(rand);
        if (parent != null) {
            parent.children.add(this);
        }

        this.light = this.start;
    }

    private void createRoomCorners() {
        Coordinate up = Coordinate.UP;
        Coordinate down = Coordinate.DOWN;
        Coordinate right = Coordinate.RIGHT;
        Coordinate left = Coordinate.LEFT;
        if (direction.equals(up)) {
            this.bl = start.move(position, left);
            this.br = bl.move(width - 1, right);
            this.tl = bl.move(height - 1, up);
            this.tr = br.move(height - 1, up);
        } else if (direction.equals(down)) {
            this.tl = start.move(position, left);
            this.tr = tl.move(width - 1, right);
            this.bl = tl.move(height - 1, down);
            this.br = tr.move(height - 1, down);
        } else if (direction.equals(right)) {
            this.bl = start.move(position, down);
            this.br = bl.move(width - 1, right);
            this.tl = bl.move(height - 1, up);
            this.tr = br.move(height - 1, up);
        } else if (direction.equals(left)) {
            this.br = start.move(position, down);
            this.bl = br.move(width - 1, left);
            this.tr = br.move(height - 1, up);
            this.tl = bl.move(height - 1, up);
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
        doors.add(createDoor(rand, Coordinate.DOWN));
        doors.add(createDoor(rand, Coordinate.UP));
        doors.add(createDoor(rand, Coordinate.RIGHT));
        doors.add(createDoor(rand, Coordinate.LEFT));
        return doors;
    }

    @Override
    public Coordinate getLight() {
        return light;
    }
}
