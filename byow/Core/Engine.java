package byow.Core;

import edu.princeton.cs.algs4.StdDraw;
import byow.Input.InputEngine;
import byow.Input.KeyboardInputEngine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    public static final int HANDLE_MENU = 0;
    public static final int HANDLE_SEED = 1;
    public static final int HANDLE_MOVE = 2;
    public static final int HANDLE_RENAME = 3;
    private int state;
    private World world;
    private Boolean gameOver;
    private Boolean quit;
    private String record;
    private String seedString;
    private Boolean lineOfSight;
    private Boolean lights;
    private String name;
    public static final char[] NUMBERS = "1234567890".toCharArray();
    public static final int PAUSE_TIME = 800;
    public static final int SIGHT = 5;

    public Engine() {
        this.state = 0;
        this.gameOver = false;
        this.quit = false;
        this.seedString = "";
        this.lineOfSight = false;
        this.lights = false;
        this.name = "";
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        record = "";
        for (int i = 0; i < input.length(); i++) {
            if (!gameOver && !quit) {
                char c = Character.toUpperCase(input.charAt(i));
                if (state == HANDLE_MENU) {
                    if (c != 'R') {
                        record  = record + c;
                    }
                    handleMenu(c);
                } else if (state == HANDLE_SEED) {
                    record  = record + c;
                    if (c == 'S' && seedString.length() != 0) {
                        randomWorld(seedString);
                        state = HANDLE_MOVE;
                        continue;
                    }
                    seedString = seedString + handleSeed(c);
                } else if (state == HANDLE_RENAME) {
                    if (c == 'S') {
                        state = HANDLE_MENU;
                        continue;
                    }
                    name = name + c;
                } else {
                    record  = record + c;
                    handleMove(c);
                }
                checkQuit();
            }
        }
        if (quit) {
            try {
                saveRecord();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return world.getTiles();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputEngine inputEngine = new KeyboardInputEngine();
        ter.initialize(WIDTH, HEIGHT);
        ter.drawMenu(state, seedString, name);
        record = "";
        while (!gameOver && !quit) {
            if (state == HANDLE_MOVE) {
                draw(getCurrentTile());
            }
            if (StdDraw.hasNextKeyTyped()) {
                char c = inputEngine.getNextKey();
                if (state == HANDLE_MENU) {
                    if (c != 'R') {
                        record  = record + c;
                    }
                    handleMenu(c);
                    ter.drawMenu(state, seedString, name);
                } else if (state == HANDLE_SEED) {
                    record  = record + c;
                    if (c == 'S' && seedString.length() != 0) {
                        randomWorld(seedString);
                        state = HANDLE_MOVE;
                        continue;
                    }
                    seedString = seedString + handleSeed(c);
                    ter.drawMenu(state, seedString, name);
                } else if (state == HANDLE_RENAME) {
                    if (c == 'S') {
                        state = HANDLE_MENU;
                        ter.drawMenu(state, seedString, name);
                        continue;
                    }
                    name = name + c;
                    ter.drawMenu(state, seedString, name);
                } else {
                    record  = record + c;
                    handleMove(c);
                    draw(getCurrentTile());
                }
                checkQuit();
            }
        }
        if (quit) {
            try {
                saveRecord();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
        if (gameOver) {
            StdDraw.pause(PAUSE_TIME);
            ter.drawMessage("Congratulation! You win!");
            StdDraw.pause(PAUSE_TIME);
            System.exit(0);
        }
    }

    private void saveRecord() throws IOException {
        FileWriter writer = new FileWriter("record.txt", false);
        writer.write(name + "\n");
        writer.write(record.substring(0, record.length() - 2));
        writer.close();
    }
    private void loadRecord() {
        try {
            FileReader reader = new FileReader("record.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);
            if (name.equals("")) {
                name = bufferedReader.readLine();
            } else {
                bufferedReader.readLine();
            }
            record = bufferedReader.readLine();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            quit = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMenu(char c) {
        if (c == 'N') {
            state = HANDLE_SEED;
        } else if (c == 'L') {
            loadRecord();
            if (record != null) {
                interactWithInputString(record);
            } else {
                System.exit(0);
            }
        } else if (c == 'Q') {
            System.exit(0);
        } else if (c == 'R') {
            state = HANDLE_RENAME;
        }
    }

    private String handleSeed(char c) {
        if (isNumber(c)) {
            return String.valueOf(c);
        }
        return "";
    }

    private void handleMove(char c) {
        if (c == 'W') {
            move(Coordinate.UP);
        } else if (c == 'S') {
            move(Coordinate.DOWN);
        } else if (c == 'A') {
            move(Coordinate.LEFT);
        } else if (c == 'D') {
            move(Coordinate.RIGHT);
        } else if (c == 'L') {
            lineOfSight = !lineOfSight;
        } else if (c == 'O') {
            lights = !lights;
        }
    }

    private void checkQuit() {
        if (state == HANDLE_MOVE && record.substring(record.length() - 2).equals(":Q")) {
            quit = true;
        }
    }

    private int[][] getMask() {
        Coordinate position = world.getAvatarPosition();
        int[][] returnMask = new int[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                int x = i - position.getX();
                int y = j - position.getY();
                if (Math.abs(x) + Math.abs(y) <= SIGHT) {
                    returnMask[i][j] = 1;
                } else {
                    returnMask[i][j] = 0;
                }
            }
        }
        return returnMask;
    }

    private TETile[][] combine(TETile[][] frame, int[][] mask) {
        TETile[][] res = TETile.copyOf(frame);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (mask[i][j] == 0) {
                    res[i][j] = Tileset.NOTHING;
                }
            }
        }
        return res;
    }

    private TETile[][] turnOffLights(TETile[][] frame) {
        TETile[][] res = TETile.copyOf(frame);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (res[i][j].description() == "floor") {
                    res[i][j] = Tileset.FLOOR;
                }
                if (res[i][j].description() == "you") {
                    res[i][j] = Tileset.AVATAR;
                }
            }
        }
        return res;
    }

    private void draw(TETile tile) {
        TETile[][] frame = world.getTiles();
        if (!lineOfSight) {
            frame = combine(frame, getMask());
        }
        if (lights) {
            frame = turnOffLights(frame);
        }
        ter.renderFrame(frame);

        StdDraw.setPenColor(Color.WHITE);
        if (tile == null) {
            StdDraw.textLeft(1, HEIGHT - 1, "");
        } else if (!name.equals("") && tile.character() == '@') {
            StdDraw.textLeft(1, HEIGHT - 1, name);
        } else {
            StdDraw.textLeft(1, HEIGHT - 1, tile.description());
        }
        StdDraw.textLeft(WIDTH / 2, HEIGHT - 1, "Press l to enable/disable line of sight");
        StdDraw.textLeft(WIDTH / 2, HEIGHT - 2, "Press o to turn on/off the light");
        StdDraw.show();
    }

    private TETile getCurrentTile() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            TETile tile = world.getTiles()[mouseX][mouseY];
            return tile;
        }
        return null;
    }

    private void move(Coordinate direction) {
        Coordinate avatarPosition = world.getAvatarPosition();
        Coordinate nextPosition = avatarPosition.add(direction);
        if (world.getTiles()[nextPosition.getX()][nextPosition.getY()].description() == "floor"
                || world.getTiles()[nextPosition.getX()][nextPosition.getY()] == Tileset.UNLOCKED_DOOR) {
            //world.swapTile(avatarPosition, nextPosition);
            world.moveAvatar(nextPosition);
        } else if (nextPosition.equals(world.getExitPosition())) {
            gameOver = true;
            world.changeTile(nextPosition, Tileset.UNLOCKED_GATE);
        } else if (world.getTiles()[nextPosition.getX()][nextPosition.getY()] == Tileset.LOCKED_DOOR) {
            world.changeTile(nextPosition, Tileset.UNLOCKED_DOOR);
        }
    }

    private boolean isNumber(char ch) {
        for (char c : NUMBERS) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    private void randomWorld(String s) {
        long seed = Long.parseLong(s);
        world = new World(WIDTH, HEIGHT, seed);
        world.generateWorld();
    }
}