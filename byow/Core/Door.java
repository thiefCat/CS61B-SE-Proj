package byow.Core;


/**
 * Doors are one unit outside of rooms and Hallways
 */
public class Door {
    private Coordinate position;
    private Coordinate direction;
    private Entity parent;
    public Door(Coordinate position, Coordinate direction, Entity parent) {
        this.position = position;
        this.direction = direction;
        this.parent = parent;
    }
    public Coordinate getPosition() {
        return position;
    }
    public Coordinate getDirection() {
        return direction;
    }
    public Entity getParent() {
        return parent;
    }

}
