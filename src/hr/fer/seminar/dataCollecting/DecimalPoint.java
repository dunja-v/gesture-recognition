package hr.fer.seminar.dataCollecting;

/**
 * Represents a point in Cartesian coordinate system with decimal
 * coordinates.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class DecimalPoint {

    /** X coordinate. */
    private double x;
    /** Y coordinate. */
    private double y;

    /**
     * Creates a new DecimalPoint with the given coordinates.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public DecimalPoint(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate.
     * 
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x coordinate.
     * 
     * @param x X coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y coordinate.
     * 
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y coordinate.
     * 
     * @param y Y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Calculates the shortest distance between this point and the
     * given point.
     * 
     * @param p Point to calculate the distance from
     * @return Distance
     */
    public double distanceFrom(DecimalPoint p) {
        return Math.abs(Math.hypot(x - p.x, y - p.y));
    }

    /**
     * Returns the string containing point coordinates in the form:
     * (x,y).
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
