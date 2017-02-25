package hr.fer.seminar.recognition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import hr.fer.seminar.dataCollecting.Canvas;
import hr.fer.seminar.dataCollecting.DecimalPoint;
import hr.fer.seminar.dataCollecting.DrawingFrame;
import hr.fer.seminar.dataCollecting.Gesture;
import hr.fer.seminar.neuralNetwork.NeuralNetwork;
import hr.fer.seminar.neuralNetwork.NeuralNetworkTrainer;

/**
 * The program is used for setting parameters and creating an instance
 * of {@link NeuralNetwork}, training the NN on the previously
 * prepared dataset and drawing new gestures for the NN to recognise.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class GestureRecognition extends JFrame {

    private static final long serialVersionUID = -3191349331728259839L;

    /**
     * Recognition precision of the Neural Network (percentage of
     * accuracy needed for recognition to be successful).
     */
    private static final double PRECISION = 0.97;

    /**
     * Number of learning examples provided.
     */
    private static final int LEARNING_SET_SIZE = 40;

    /**
     * List of points a gesture contains.
     */
    private List<Point> points = new ArrayList<>();

    /**
     * Current instance of the neural network.
     */
    private NeuralNetwork network;
    /** Number of input neurons. */
    private int numOfInputs;
    /** Number of hidden layer neurons. */
    private int numOfHiddenN;
    /** Number of output neurons. */
    private int numOfOutputs = Gesture.vectorLength;
    /** Number of epochs used in training. */
    private int numOfEpochs;
    /** Learning rate. */
    private int learningRate;

    /** Inputs of the neural network. */
    double[][] inputs;
    /**
     * expected outputs of the neural network for the given inputs.
     */
    double[][] expectedOutputs;

    /**
     * Creates a new instance of GestureRecognition frame.
     * 
     * @throws HeadlessException
     */
    public GestureRecognition() throws HeadlessException {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(20, 20);
        setTitle("Gesture recognition");
        initGUI();
        setSize(800, 600);
        setVisible(true);
    }

    /** Initialises the GUI of GestureRecognition frame. */
    private void initGUI() {

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        JPanel gestureRecognition = new JPanel();
        gestureRecognition.setLayout(new BorderLayout());
        gestureRecognition.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cp.add(gestureRecognition, BorderLayout.CENTER);

        JPanel drawingPanel = new JPanel(new BorderLayout());
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gestureRecognition.add(drawingPanel, BorderLayout.CENTER);

        Canvas canvas = new Canvas(points);
        drawingPanel.add(canvas, BorderLayout.CENTER);

        JPanel canvasMenu = new JPanel();
        gestureRecognition.add(canvasMenu, BorderLayout.PAGE_START);

        JButton newGesture = new JButton("New");
        canvasMenu.add(newGesture);

        JButton recognize = new JButton("Recognize");
        recognize.setEnabled(false);
        canvasMenu.add(recognize);

        JPanel output = new JPanel();
        output.setBackground(Color.LIGHT_GRAY);
        gestureRecognition.add(output, BorderLayout.PAGE_END);

        JLabel gestureName = new JLabel("No gesture was given.");
        output.add(gestureName);

        canvas.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                recognize.setEnabled(true);
            }
        });

        canvas.addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                canvas.repaint();
            }

        });

        newGesture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points.clear();
                canvas.repaint();
                recognize.setEnabled(false);
                gestureName.setText("No gesture was given.");
            }
        });

        recognize.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (points == null) {
                    gestureName.setText("No gesture was drawn!");
                    return;
                } else if (network == null) {
                    gestureName.setText("No neural network was created.");
                    return;
                }
                String recognizedGesture = recognizeGesture();
                gestureName.setText(recognizedGesture);
            }
        });

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(0, 1));
        menu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cp.add(menu, BorderLayout.LINE_START);

        menu.add(new JLabel("Choose a number of points:"));
        JSlider numOfPoints = new JSlider(5, 100, 10);
        numOfPoints.setMajorTickSpacing(10);
        numOfPoints.setMinorTickSpacing(5);
        numOfPoints.setPaintTicks(true);
        numOfPoints.setPaintLabels(true);
        menu.add(numOfPoints);

        JLabel numOfPointsL = new JLabel();
        numOfPointsL.setHorizontalAlignment(SwingConstants.CENTER);
        numOfPointsL.setText(String.valueOf("Number of points: " + numOfPoints.getValue()));
        menu.add(numOfPointsL);

        numOfPoints.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                numOfPointsL.setText("Number of points: " + String.valueOf(numOfPoints.getValue()));
            }

        });

        menu.add(new JLabel("Choose a number of hidden layer neurons:"));
        JSlider numOfHiddenNeurons = new JSlider(1, 20, 5);
        numOfHiddenNeurons.setMajorTickSpacing(5);
        numOfHiddenNeurons.setMinorTickSpacing(1);
        numOfHiddenNeurons.setPaintTicks(true);
        numOfHiddenNeurons.setPaintLabels(true);
        menu.add(numOfHiddenNeurons);

        JLabel numOfHiddenL = new JLabel();
        numOfHiddenL.setHorizontalAlignment(SwingConstants.CENTER);
        numOfHiddenL.setText(
                String.valueOf("Number of hidden neurons: " + numOfHiddenNeurons.getValue()));
        menu.add(numOfHiddenL);

        numOfHiddenNeurons.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                numOfHiddenL.setText(String
                        .valueOf("Number of hidden neurons: " + numOfHiddenNeurons.getValue()));
            }

        });

        JButton createNewNetwork = new JButton("Create new neural network");
        menu.add(createNewNetwork);

        createNewNetwork.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                numOfInputs = numOfPoints.getValue() * 2;
                numOfHiddenN = numOfHiddenNeurons.getValue();
                network = new NeuralNetwork(numOfInputs, numOfHiddenN, numOfOutputs);

                points.clear();
                canvas.repaint();
                recognize.setEnabled(false);
                gestureName.setText("No gesture was given.");
            }
        });

        menu.add(new JLabel("Choose a number of epochs:"));
        JSlider numOfEpochsS = new JSlider(100, 10000, 5000);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(100, new JLabel("100"));
        labelTable.put(5000, new JLabel("5000"));
        labelTable.put(10000, new JLabel("10000"));
        numOfEpochsS.setMajorTickSpacing(1000);
        numOfEpochsS.setLabelTable(labelTable);
        numOfEpochsS.setPaintTicks(true);
        numOfEpochsS.setPaintLabels(true);
        menu.add(numOfEpochsS);

        JLabel numOfEpochsL = new JLabel();
        numOfEpochsL.setText(String.valueOf("Number of epochs: " + numOfEpochsS.getValue()));
        numOfEpochsL.setHorizontalAlignment(SwingConstants.CENTER);
        menu.add(numOfEpochsL);

        numOfEpochsS.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                numOfEpochsL
                        .setText(String.valueOf("Number of epochs: " + numOfEpochsS.getValue()));
            }

        });

        menu.add(new JLabel("Choose a learning rate:"));
        JSlider learningRateS = new JSlider(1, 10, 1);
        Hashtable<Integer, JLabel> rateLabelTable = new Hashtable<>();
        rateLabelTable.put(1, new JLabel(Double.toString(1. / LEARNING_SET_SIZE)));
        rateLabelTable.put(5, new JLabel(Double.toString(5. / LEARNING_SET_SIZE)));
        rateLabelTable.put(10, new JLabel(Double.toString(10. / LEARNING_SET_SIZE)));
        learningRateS.setLabelTable(rateLabelTable);
        learningRateS.setMajorTickSpacing(1);
        learningRateS.setPaintTicks(true);
        learningRateS.setPaintLabels(true);
        menu.add(learningRateS);

        JLabel learningRateL = new JLabel("Learning rate: " + learningRateS.getValue());
        menu.add(learningRateL);

        learningRateS.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                learningRateL.setText("Learning rate: " + learningRateS.getValue());
            }
        });

        JButton trainNetwork = new JButton("Train network");
        menu.add(trainNetwork);

        JLabel training = new JLabel();
        menu.add(training);

        trainNetwork.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (network == null) {
                    training.setText("No neural network was created!");
                    return;
                }

                learningRate = learningRateS.getValue();
                numOfEpochs = numOfEpochsS.getValue();
                SwingWorker trainNeuralNet = new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        training.setText("Training...");
                        loadExamples();
                        NeuralNetworkTrainer.train(network, inputs, expectedOutputs, numOfEpochs,
                                learningRate);
                        return null;
                    }

                    @Override
                    protected void done() {
                        training.setText("Network successfully trained!");
                    }
                };

                trainNeuralNet.execute();
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

        SwingUtilities.invokeLater(GestureRecognition::new);

    }

    /**
     * Loads the learning examples used for training the neural
     * network from "./learningExamples" folder.
     */
    private void loadExamples() {

        Path loadDirectory = Paths.get("./learningExamples");
        double[][] examples = GestureLoader.loadDataSet(loadDirectory, numOfOutputs,
                numOfInputs / 2);

        inputs = new double[examples.length][];
        expectedOutputs = new double[examples.length][];

        for (int i = 0; i < examples.length; i++) {
            double[] input = Arrays.copyOf(examples[i], numOfInputs);
            double[] expectedOutput = Arrays.copyOfRange(examples[i], numOfInputs,
                    numOfInputs + numOfOutputs);

            inputs[i] = input;
            expectedOutputs[i] = expectedOutput;
        }

    }

    /**
     * Attempts to recognise the drawn gesture.
     * 
     * @return Name of the gesture recognised of 'Unknown' if no
     *         gesture was recognised
     */
    private String recognizeGesture() {
        int numOfPoints = numOfInputs / 2;
        List<DecimalPoint> representativePoints = GestureLoader
                .extractRepresentativePoints(DrawingFrame.normalisePoints(points), numOfPoints);
        double[] gestureInput = new double[numOfInputs];
        for (int i = 0; i < numOfPoints; i++) {
            gestureInput[2 * i] = representativePoints.get(i).getX();
            gestureInput[2 * i + 1] = representativePoints.get(i).getY();
        }

        network.setInputs(gestureInput);
        double[] gestureOutput = network.calculateOutputs();

        if (gestureOutput[0] > PRECISION) {
            return Gesture.ALPHA.toString();
        } else if (gestureOutput[1] > PRECISION) {
            return Gesture.BETA.toString();
        }

        else if (gestureOutput[2] > PRECISION) {
            return Gesture.GAMMA.toString();
        }

        else if (gestureOutput[3] > PRECISION) {
            return Gesture.EPSILON.toString();
        } else
            return "Unknown";
    }

}
