package byow.Input;

import byow.InputDemo.InputSource;
import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputEngine implements InputEngine {
    public KeyboardInputEngine() {
    }
    @Override
    public char getNextKey() {
        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        System.out.println(c);
        return c;
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
