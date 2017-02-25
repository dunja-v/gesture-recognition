package hr.fer.seminar.neuralNetwork;

/**
 * NeuralNetwork class represents a feed forward neural network with
 * one hidden layer.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 */
public class NeuralNetwork {
    /** Hidden layer. */
    private NeuronLayer hiddenLayer;
    /** Output layer. */
    private NeuronLayer outputLayer;
    /** Inputs of the neural network. */
    private double[] inputs;
    /** Outputs of the hidden layer. */
    private double[] hiddenLayerOutputs;
    /** Outputs of the network. */
    private double[] outputs;

    /**
     * Creates a new feed forward neural network with the given number
     * of neurons. Weight between all the layers are initialised to
     * random numbers in range [0,1].
     * 
     * @param inputLayerSize Number of neurons in the input layer
     * @param hiddenLayerSize Number of neurons in the hidden layer
     * @param outputLayerSize Number of neurons in the output layer
     */
    public NeuralNetwork(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
        hiddenLayer = new NeuronLayer(hiddenLayerSize, inputLayerSize);
        outputLayer = new NeuronLayer(outputLayerSize, hiddenLayerSize);
    }

    /**
     * Sets the inputs of the neural network to the given values.
     * 
     * @param inputs Inputs of the neural network
     */
    public void setInputs(double[] inputs) {
        this.inputs = inputs;
    }

    /**
     * Calculates the outputs of the neural network based on the given
     * inputs and set weights.
     * 
     * @return Output of the neural network
     * @throws NeuralNetworkException If the number of input neurons
     *         differs from the length of the given array of the
     *         inputs have not been initialised
     */
    public double[] calculateOutputs() {
        if (inputs == null) {
            throw new NeuralNetworkException(
                    "Unable to calculate outputs because no inputs were given.");
        }

        hiddenLayer.setInputs(inputs);
        hiddenLayerOutputs = hiddenLayer.calculateOutputs();

        outputLayer.setInputs(hiddenLayerOutputs);
        outputs = outputLayer.calculateOutputs();

        return outputs;
    }

    /**
     * Returns the outputs of the hidden layer.
     * 
     * @return Outputs of the hidden layer
     */
    public double[] getHiddenLayerOutputs() {
        return hiddenLayerOutputs;
    }

    /**
     * Sets the weights of the output layer.
     * 
     * @param weights Weights of the output layer to be set
     */
    public void updateOutputLayerWeights(double[][] weights) {
        outputLayer.setWeights(weights);
    }

    /**
     * Sets the weights of the hidden layer.
     * 
     * @param weights Weights of the hidden layer to be set
     */
    public void updateHiddenLayerWeights(double[][] weights) {
        hiddenLayer.setWeights(weights);
    }

    /**
     * Returns the current weights of the output layer.
     * 
     * @return Current weights of the output layer
     */
    public double[][] getOutputLayerWeights() {
        return outputLayer.getWeights();
    }

    /**
     * Returns the current weights of the hidden layer.
     * 
     * @return Current weights of the hidden layer
     */
    public double[][] getHiddenLayerWeights() {
        return hiddenLayer.getWeights();
    }

    /**
     * Returns the number of the output neurons (length of the output vector).
     * 
     * @return Number of output neurons
     */
    public int getOutputLayerSize() {
        return outputLayer.getSize();
    }

}
