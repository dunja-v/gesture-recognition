package hr.fer.seminar.dataCollecting;

/**
 * Enumeration of one-hot encoding for gestures which can be
 * recognized by {@link GestureRecognition} application.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 */
public enum Gesture {
    ALPHA("1000"), BETA("0100"), GAMMA("0010"), EPSILON("0001");
    
    /**One-hot encoding vector.*/
    private String vectorValue;

    /**Length of the encoding vector.*/
    public static final int vectorLength = 4;

    /**
     * Creates new enumeration value.
     * @param vectorValue encoding vector
     */
    private Gesture(String vectorValue) {
        this.vectorValue = vectorValue;
    }

    /**
     * Returns the value of the one-hot encoding vector.
     * @return encoding vector
     */
    public String getOutputVector() {
        return vectorValue;
    }

}
