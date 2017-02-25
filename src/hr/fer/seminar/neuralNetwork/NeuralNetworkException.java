package hr.fer.seminar.neuralNetwork;

/**
 * Thrown to indicate that there has been a mistake in setting the
 * parameters or during the training of the {@link NeuralNetwork}.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 */
public class NeuralNetworkException extends RuntimeException {

    /**
     * Used during deserialisation to verify that the sender and
     * receiver of a serialised object have loaded classes for that
     * object that are compatible with respect to serialisation.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NeuralNetworkException class with the specified
     * detail message.
     * 
     * @param message The detail message
     */
    public NeuralNetworkException(String message) {
        super(message);
    }

}
