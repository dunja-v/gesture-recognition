package hr.fer.seminar.recognition;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hr.fer.seminar.dataCollecting.DecimalPoint;

/**
 * Utility class containing methods for loading gesture signals from
 * files.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class GestureLoader {

    /**
     * Private constructor which disables instancing objects of this
     * class.
     */
    private GestureLoader() {

    }

    /**
     * Loads the coordinates and expected output from the example and
     * chooses the given number of representative coordinates.
     * 
     * @param path Path of the example file
     * @param numOfOutputs Number of outputs in the file
     * @param numOfPoints Number of desired points
     * @return Array of representative coordinates and expected output
     *         from the example
     */
    public static double[] loadExample(Path path, int numOfOutputs, int numOfPoints) {

        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<DecimalPoint> points = new ArrayList<>();
        int numOfInputs = lines.size() - numOfOutputs;
        for (int i = 0; i < numOfInputs; i += 2) {
            points.add(new DecimalPoint(Double.parseDouble(lines.get(i)),
                    Double.parseDouble(lines.get(i + 1))));
        }

        List<DecimalPoint> representativePoints = extractRepresentativePoints(points, numOfPoints);

        double[] example = new double[numOfPoints * 2 + numOfOutputs];
        for (int i = 0; i < numOfPoints; i++) {
            example[2 * i] = representativePoints.get(i).getX();
            example[2 * i + 1] = representativePoints.get(i).getY();
        }
        for (int j = 0; j < numOfOutputs; j++) {
            example[example.length - numOfOutputs + j] = Double
                    .parseDouble(lines.get(numOfInputs + j));
        }

        return example;
    }

    /**
     * Loads all the examples from the given directory and creates the
     * filed containing the given number of representative points from
     * each gesture and the expected output vector.
     * 
     * @param path Path of the Directory containing examples
     * @param numOfOutputs Number of outputs in the file
     * @param numOfPoints Number of desired points
     * @return Array of inputs and expected outputs
     */
    public static double[][] loadDataSet(Path path, int numOfOutputs, int numOfPoints) {
        List<double[]> dataSet = new ArrayList<>();

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    dataSet.add(loadExample(file, numOfOutputs, numOfPoints));
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        double[][] loadedDataSet = new double[dataSet.size()][];
        for (int i = 0; i < loadedDataSet.length; i++) {
            loadedDataSet[i] = dataSet.get(i);
        }

        return loadedDataSet;
    }

    /**
     * Extracts the given number of representative points from the
     * list of normalised points. The points are selected so that they
     * are approximately equally distanced from one another and cover
     * the entire space of the gesture.
     * 
     * @param normalisedPoints Normalised points of the gesture
     * @param numOfPoints Number of points to be extracted
     * @return Representative points
     */
    public static List<DecimalPoint> extractRepresentativePoints(
            List<DecimalPoint> normalisedPoints, int numOfPoints) {

        double gestureLength = 0;
        DecimalPoint currentPoint = normalisedPoints.get(0);
        DecimalPoint previousPoint;
        List<Double> distances = new ArrayList<>();
        distances.add(0.0);

        for (int i = 1; i < normalisedPoints.size(); i++) {
            previousPoint = currentPoint;
            currentPoint = normalisedPoints.get(i);

            double distance = currentPoint.distanceFrom(previousPoint);

            gestureLength += distance;
            distances.add(gestureLength);
        }

        List<DecimalPoint> representative = new ArrayList<DecimalPoint>();

        for (int i = 0; i < numOfPoints; i++) {
            double targetedDistance = i * gestureLength / (numOfPoints - 1);

            Optional<Double> optionalNearestDistance = distances.parallelStream()
                    .min((d1, d2) -> Double.compare(Math.abs(targetedDistance - d1),
                            Math.abs(targetedDistance - d2)));
            Double nearestDistance = optionalNearestDistance.get();

            DecimalPoint nextPoint = normalisedPoints.get(distances.indexOf(nearestDistance));

            representative.add(nextPoint);
        }

        return representative;
    }

}
