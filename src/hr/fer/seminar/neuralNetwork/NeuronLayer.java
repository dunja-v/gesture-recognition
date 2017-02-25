package hr.fer.seminar.neuralNetwork;

/**
 * NeuronLayer class implements a single (hidden or output) layer of
 * {@link Neuron} used in {@link NeuralNetwork}.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 */
public class NeuronLayer {
    /** Array of neurons. */
    private Neuron[] neurons;
    /** Field of all the weights of every neuron in the layer. */
    private double[][] weights;
    /** Number of neurons in the layer. */
    private int size;

    /**
     * Constructs a new NeuronLayer with the given number of neurons
     * in which each neuron has the given number of inputs. Weights of
     * all the neurons in the layer are initialised to a random number
     * in range [0,1].
     * 
     * @param numberOfNeurons Number of neurons in the layer
     * @param numberOfInputs Number of inputs for every neuron
     */
    public NeuronLayer(int numberOfNeurons, int numberOfInputs) {
        this.neurons = new Neuron[numberOfNeurons];
        this.weights = new double[numberOfNeurons][numberOfInputs];
        this.size = numberOfNeurons;

        for (int i = 0; i < numberOfNeurons; i++) {
            int numOfWeights = numberOfInputs + 1;// treshold
            double[] singleNeuronWeights = new double[numOfWeights];
            for (int j = 0; j < numOfWeights; j++) {
                singleNeuronWeights[j] = Math.random();
            }
            neurons[i] = new Neuron(singleNeuronWeights);
            weights[i] = singleNeuronWeights;
        }
    }

    /**
     * Returns the number of neurons in the neuron layer.
     * 
     * @return Number of neurons in the neuron layer
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the inpurs of all the neurons in the layer to the given
     * values.
     * 
     * @param inputs Input vector
     * @throws NeuralNetworkException If the length of the input
     *         vector does not match the number of inputs of the
     *         neuron
     */
    public void setInputs(double[] inputs) {

        for (int i = 0; i < neurons.length; i++) {
            neurons[i].setInputs(inputs);
        }
    }

    /**
     * Sets the weights of all the neurons in the layer to the given
     * values. The j-th weight of the i-th neuron is set to the value
     * of weights[i][j].
     * 
     * @param weights Weights to be set
     * @throws NeuralNetworkException If the number of weights doesn't
     *         match the number of neurons or the number of inputs
     */
    public void setWeights(double[][] weights) {
        if (weights.length != neurons.length) {
            throw new IllegalArgumentException("Invalid number of weight vectors.");
        }
        this.weights = weights;
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].setWeights(weights[i]);
        }
    }

    /**
     * Returns the current weights of all the neurons in the layer.
     * 
     * @return Weights of the neurons
     */
    public double[][] getWeights() {
        return weights;
    }

    /**
     * Calculates the aoutputs of all the neurons in the layer based
     * on set weights and inputs.
     * 
     * @return Output vector
     */
    public double[] calculateOutputs() {
        double[] outputs = new double[neurons.length];

        for (int i = 0; i < neurons.length; i++) {
            outputs[i] = neurons[i].activate();
        }

        return outputs;
    }

}
