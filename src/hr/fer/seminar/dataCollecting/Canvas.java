package hr.fer.seminar.dataCollecting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Canvas for showing a list of {@link DecimalPoint} on the component.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class Canvas extends JComponent {

    private static final long serialVersionUID = 1L;
    /**Radius of every drawn point which is shown in GUI.*/
    private static final int POINT_RADIUS = 5;
    
    /**List of all points contained in the current drawing.*/
    private List<Point> points = new ArrayList<>();

    /**
     * Creates a new canvas with the given list of points.
     * @param points Points to be shown on the canvas
     */
    public Canvas(List<Point> points) {
        super();
        this.points=points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        for (Point p : points) {
            g.setColor(Color.BLACK);
            g.fillOval(p.x, p.y, POINT_RADIUS, POINT_RADIUS);
        }
    }
}
