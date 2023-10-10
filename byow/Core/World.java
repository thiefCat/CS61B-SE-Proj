package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class World {
    private int width;
    private int height;
    private TETile[][] tiles;
    private List<Door> doors;
    private List<Entity> entities;
    private List<Room> rooms;
    private Random rand;
    private Coordinate avatarPosition;
    private Coordinate exitPosition;
    private List<Door> displayedDoors;
    public static final int ROOM_LOW = 3;
    public static final int ROOM_HIGH = 9;
    public static final int HALLWAY_LOW = 2;
    public static final int HALLWAY_HIGH = 9;
    public static final double P_BUILD_ROOM = 0.6;
    public static final int TAB = 3;
    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.tiles = new TETile[width][height];
        this.rand = new Random(seed);
        this.doors = new LinkedList<>();
        this.entities = new LinkedList<>();
        this.rooms = new ArrayList<>();
        this.displayedDoors = new ArrayList<>();
        createEmptyWorld();
    }

    public static void main(String[] args) {
    }

    public TETile[][] getTiles() {
        TETile[][] res = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == avatarPosition.getX() && y == avatarPosition.getY()) {
                    res[x][y] = Tileset.getAvatar(tiles[x][y].getBackgroundColor());
                } else {
                    res[x][y] = tiles[x][y];
                }
            }
        }

        return res;
    }

    private void createEmptyWorld() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * function that implements the main logic of generating
     * the random world
     */
    public TETile[][] generateWorld() {
        Room initialRoom = initializeRandomRoom();
        fill(initialRoom);
        entities.add(initialRoom);
        doors.addAll(initialRoom.getDoors());
        while (!doors.isEmpty()) {
            int length = doors.size();
            int randIndex = RandomUtils.uniform(rand, 0, length);
            Door door = doors.remove(randIndex);
            expand(door);
        }
        fillWall();
        fillDoor();
        this.avatarPosition = getAvatar();
        return getTiles();
    }

    public void expand(Door door) {
        Hallway hallway = createHallway(door);
        if (isInside(hallway) && noOverlap(hallway)) {
            fill(hallway);
            entities.add(hallway);
            if (RandomUtils.uniform(rand) < P_BUILD_ROOM) {
                Coordinate nextDoorPosition = hallway.getEnd().add(hallway.direction);
                Door nextDoor = new Door(nextDoorPosition, hallway.direction, hallway);
                Room room = createRoom(nextDoor);
                if (isInside(room) && noOverlap(room)) {
                    displayedDoors.add(new Door(hallway.getEnd(), hallway.direction, null));
                    fill(room);
                    entities.add(room);
                    rooms.add(room);
                    doors.addAll(room.getDoors());
                }
            } else {
                doors.addAll(hallway.getDoors());
            }
        }
    }

    /**
     * create the first room in the world
     * @return the created room
     */
    private Room initializeRandomRoom() {
        int x = RandomUtils.uniform(rand, 0, width);
        int y = RandomUtils.uniform(rand, 0, height);
        // choose a direction randomly
        Coordinate randDirection = Coordinate.sampleDirection(rand);
        Door randDoor = new Door(new Coordinate(x, y), randDirection, null);
        Room room = createRoom(randDoor);
        if (isInside(room)) {
            rooms.add(room);
            return room;
        } else {
            return initializeRandomRoom();
        }
    }

    /**
     * Return Hallway from a door with random length
     */
    public Hallway createHallway(Door door) {
        int randLength = RandomUtils.uniform(rand, HALLWAY_LOW, HALLWAY_HIGH);
        Hallway hallway = new Hallway(door.getPosition(), door.getDirection(), randLength, door.getParent(), rand);
        return hallway;
    }

    /**
     * Return a room with random size from a door
     */
    private Room createRoom(Door door) {
        Coordinate start = door.getPosition();
        Coordinate direction = door.getDirection();
        int w = RandomUtils.uniform(rand, ROOM_LOW, ROOM_HIGH);
        int h = RandomUtils.uniform(rand, ROOM_LOW, ROOM_HIGH);
        int position = getRandomPosition(w, h, direction);
        return new Room(start, direction, w, h, position, door.getParent(), rand);
    }

    private void fillDoor() {
        for (Door door : displayedDoors) {
            int x = door.getPosition().getX();
            int y = door.getPosition().getY();
            tiles[x][y] = Tileset.LOCKED_DOOR;
        }
    }

    private int getRandomPosition(int w, int h, Coordinate direction) {
        if (direction.equals(Coordinate.DOWN) || direction.equals(Coordinate.UP)) {
            return RandomUtils.uniform(rand, 0, w);
        } else {
            return RandomUtils.uniform(rand, 0, h);
        }
    }

    private double getDistance(Coordinate a, int x, int y) {
        return Math.sqrt(Math.pow(x - a.getX(), 2) + Math.pow(y - a.getY(), 2));
    }

    /**
     * add entity e to the world (change tiles to FLOOR)
     */
    private void fill(Entity e) {
        int xLow = e.bl.getX();
        int yLow = e.bl.getY();
        int xHigh = e.br.getX();
        int yHigh = e.tl.getY();
        Coordinate lightSource = e.getLight();
        for (int x = xLow; x <= xHigh; x++) {
            for (int y = yLow; y <= yHigh; y++) {
                if (lightSource == null) {
                    tiles[x][y] = Tileset.FLOOR;
                }
                else {
                    tiles[x][y] = Tileset.coloredFloor(getDistance(lightSource, x, y));
                }
                //tiles[x][y] = Tileset.FLOOR;
            }
        }
    }
    /**
     * Return true if a room does not have any overlap with existing rooms
     * Return false otherwise
     */
    public boolean noOverlap(Entity entity) {
        for (Entity e : entities) {
            if (entity.overlap(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * check whether Room or Hallway e is inside the world
     */
    public boolean isInside(Entity e) {
        int xLow = e.bl.getX() - 1;
        int yLow = e.bl.getY() - 1;
        int xHigh = e.br.getX() + 1;
        int yHigh = e.tl.getY() + 1;
        if (xLow >= 0 && xHigh < width && yLow >= 0 && yHigh < height - TAB) {
            return true;
        }
        return false;
    }

    /**
     * check whether a pixel with coordinate (x, y) is inside the world
     */
    private boolean isInside(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height - TAB) {
            return true;
        }
        return false;
    }

    /**
     * check whether a pixel with coordinate (x, y) should be a wall
     */
    private boolean isWall(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isInside(i, j) && tiles[i][j].description() == "floor") {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean possibleDoor(int x, int y) {
        if (isInside(x - 1, y) && tiles[x - 1][y] == Tileset.FLOOR
                || isInside(x + 1, y) && tiles[x + 1][y] == Tileset.FLOOR
                || isInside(x, y - 1) && tiles[x][y - 1] == Tileset.FLOOR
                || isInside(x, y + 1) && tiles[x][y + 1] == Tileset.FLOOR) {
            return true;
        }
        return false;
    }

    /**
     * fill the wall tiles
     */
    private void fillWall() {
        List<Coordinate> walls = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isWall(x, y) && tiles[x][y] == Tileset.NOTHING) {
                    tiles[x][y] = Tileset.WALL;
                    if (possibleDoor(x, y)) {
                        walls.add(new Coordinate(x, y));
                    }
                }
            }
        }
        int randIndex = RandomUtils.uniform(rand, walls.size());
        exitPosition = walls.get(randIndex);
        tiles[exitPosition.getX()][exitPosition.getY()] = Tileset.LOCKED_GATE;
    }

    private Coordinate getAvatar() {
        int randIndex = RandomUtils.uniform(rand, rooms.size());
        Room randRoom = rooms.get(randIndex);
        int randX = randRoom.bl.getX() + RandomUtils.uniform(rand, randRoom.width);
        int randY = randRoom.bl.getY() + RandomUtils.uniform(rand, randRoom.height);
        //tiles[randX][randY] = Tileset.AVATAR;
        return new Coordinate(randX, randY);
    }

    public Coordinate getExitPosition() {
        return exitPosition;
    }

    public Coordinate getAvatarPosition() {
        return avatarPosition;
    }

    /*public void swapTile(Coordinate p1, Coordinate p2) {
        TETile tile = tiles[p1.getX()][p1.getY()];
        tiles[p1.getX()][p1.getY()] = tiles[p2.getX()][p2.getY()];
        tiles[p2.getX()][p2.getY()] = tile;
        avatarPosition = p2;
    }*/

    public void moveAvatar(Coordinate newPos) {
        avatarPosition = newPos;
    }

    public void changeTile(Coordinate p, TETile tile) {
        tiles[p.getX()][p.getY()] = tile;
    }

    public void setWorld(TETile[][] newTiles) {
        tiles = newTiles;
    }

}
