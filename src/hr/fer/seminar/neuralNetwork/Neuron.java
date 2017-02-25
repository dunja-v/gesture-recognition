package hr.fer.seminar.neuralNetwork;

/**
 * Neuron class implements one neuron of the {@link NeuralNetwork}
 * with sigmoid activation function.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 *
 */
public class Neuron {
    /** Weights of the neuron-. */
    private double[] weights;
    /** Inputs of the neuron. */
    private double[] inputs;

    /**
     * Constructs a new Neuron with the given weights. The i-th weight
     * of the vector represents a number multiplied with the i-th
     * input of the neuron and the last weight is represents a
     * threshold. The length of the weight vector determines the
     * number of inputs for the neuron (number of inputs is equal to
     * weight vector - 1).
     * 
     * @param weights Weight vector
     */
    public Neuron(double[] weights) {
        this.weights = weights;
    }

    /**
     * Calculates the output of the neuron.
     * 
     * @return Output of the neuron
     */
    public double activate() {
        double net = 0;
        for (int i = 0; i < inputs.length; i++) {
            net += weights[i] * inputs[i];
        }
        net += weights[weights.length - 1] * 1; // prag okidanja

        return 1 / (1 + Math.exp(-net));
    }

    /**
     * Returns the weight vector of the neuron.
     * 
     * @return Weight vector
     */
    public double[] getWeights() {
        return weights;
    }

    /**
     * Sets the wieghts of the neuron to the given values.
     * 
     * @param weights New weight vector
     * @throws NeuralNetworkException If the length of the new weight
     *         vector differs from the old one
     */
    public void setWeights(double[] weights) {
        if (this.weights.length != weights.length)
            throw new NeuralNetworkException("Invalid length of weights array.");
        this.weights = weights;
    }

    /**
     * Returns the last given inputs of the neuton.
     * 
     * @return Inputs
     */
    public double[] getInputs() {
        return inputs;
    }

    /**
     * Sets the inputs of the neuron to the given values.
     * 
     * @param inputs Inputs to be set
     * @throws NeuralNetworkException If the length of the input
     *         vector does not match the number of inputs of the
     *         neuron
     */
    public void setInputs(double[] inputs) {
        if (this.weights.length != (inputs.length + 1))
            throw new NeuralNetworkException("Invalid length of inputs array.");
        this.inputs = inputs;
    }

}
