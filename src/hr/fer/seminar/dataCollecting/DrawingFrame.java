package hr.fer.seminar.dataCollecting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;

/**
 * Application used for drawing a gesture, providing basic signal
 * processing and saving gestures to files. The user can choose to
 * draw one of the given gestures from {@link Gesture} enumeration and
 * save it to file.
 * 
 * The points on canvas which gesture contains are normalised to fit
 * the range [-1, 1].
 * 
 * Each gesture is saved to a folder with the gesture name within the
 * current directory. The file name is set to current time in
 * 'yyyy_MM_dd_hh_mm_ss' format.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class DrawingFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Points the gesture contains. */
    private List<Point> points = new ArrayList<>();

    /**
     * Creates a new DrawingFrame.
     * 
     * @throws HeadlessException
     */
    public DrawingFrame() throws HeadlessException {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(20, 20);
        setTitle("Drawing gestures");
        initGUI();
        setSize(600, 600);
        setVisible(true);
    }

    /**
     * Initialises the graphical user interface of the frame.
     */
    private void initGUI() {

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        JPanel menu = new JPanel();
        cp.add(menu, BorderLayout.NORTH);

        JPanel drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.white);
        drawingPanel.setLayout(new BorderLayout());
        cp.add(drawingPanel, BorderLayout.CENTER);

        Canvas canvas = new Canvas(points);
        drawingPanel.add(canvas, BorderLayout.CENTER);

        JLabel gesturesTitle = new JLabel("Available gestures:");
        menu.add(gesturesTitle);

        JComboBox<Gesture> gestures = new JComboBox<>(
                new Gesture[] { Gesture.ALPHA, Gesture.BETA, Gesture.GAMMA, Gesture.EPSILON });
        menu.add(gestures);

        JButton saveButton = new JButton("Save");
        menu.add(saveButton);
        saveButton.setEnabled(false);

        JButton newButton = new JButton("New");
        menu.add(newButton);

        canvas.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                saveButton.setEnabled(true);
            }
        });

        canvas.addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                canvas.repaint();
            }

        });

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points.clear();
                canvas.repaint();
                saveButton.setEnabled(false);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                saveButton.setEnabled(false);
                newButton.setEnabled(false);

                SwingWorker normaliseInput = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {

                        List<DecimalPoint> normalisedPoints = normalisePoints(points);

                        saveGesture(normalisedPoints, gestures);
                        return null;
                    }

                    @Override
                    protected void done() {
                        newButton.setEnabled(true);
                    }
                };

                normaliseInput.execute();
            }
        });

    }

    /**
     * Method run on program start.
     * 
     * @param args Command line arguments (ignored in the method)
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }

        SwingUtilities.invokeLater(DrawingFrame::new);

    }

    /**
     * Normalises points so they fit in [-1,1] value range.
     * 
     * @param points List of points the gesture contains
     * @return List of normalised points
     */
    public static List<DecimalPoint> normalisePoints(List<Point> points) {

        OptionalDouble optionalAvgX = points.parallelStream().mapToDouble(p -> p.getX()).average();
        OptionalDouble optionalAvgY = points.parallelStream().mapToDouble(p -> p.getY()).average();

        Double avgX = optionalAvgX.getAsDouble();
        Double avgY = optionalAvgY.getAsDouble();

        List<DecimalPoint> normalisedPoints = points.parallelStream()
                .map(p -> new DecimalPoint(p.getX() - avgX, p.getY() - avgY))
                .collect(Collectors.toList());

        OptionalDouble maxX = normalisedPoints.parallelStream().mapToDouble(p -> Math.abs(p.getX()))
                .max();
        OptionalDouble maxY = normalisedPoints.parallelStream().mapToDouble(p -> Math.abs(p.getY()))
                .max();

        Double max = Double.max(maxX.getAsDouble(), maxY.getAsDouble());

        for (DecimalPoint point : normalisedPoints) {
            point.setX(point.getX() / max);
            point.setY(point.getY() / max);
        }

        return normalisedPoints;
    }

    /**
     * Saves the given gesture to file with the name set to current
     * time in 'yyyy_MM_dd_hh_mm_ss' format.
     * 
     * @param points List of points to be saved
     * @param gestures JComboBox for selecting the type of gesture
     */
    private void saveGesture(List<DecimalPoint> points, JComboBox<Gesture> gestures) {

        LocalDateTime currTime = LocalDateTime.now();
        DateTimeFormatter fileNameFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String fileName = currTime.format(fileNameFormat);

        Gesture gesture = (Gesture) gestures.getSelectedItem();
        Path saveDirectory = Paths.get(gesture.toString());
        Path saveFile = Paths.get(gesture.toString(), fileName);

        List<String> coordinates = points.parallelStream()
                .map(p -> (new String(p.getX() + "\n" + p.getY()))).collect(Collectors.toList());

        String outputVector = gesture.getOutputVector();
        for (int i = 0; i < outputVector.length(); i++) {
            coordinates.add(outputVector.substring(i, i + 1));
        }
        try {
            Files.createDirectories(saveDirectory);
            Files.createFile(saveFile);
            Files.write(saveFile, coordinates, Charset.forName("UTF-8"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}
