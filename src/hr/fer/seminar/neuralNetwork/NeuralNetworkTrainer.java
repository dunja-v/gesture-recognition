package hr.fer.seminar.neuralNetwork;

/**
 * Utility class which implements methods for training an instance of
 * {@link NeuralNetwork} using the Backpropagation algorithm.
 * 
 * @author Dunja Vesinger
 * @version 1.0
 */
public class NeuralNetworkTrainer {

    /**
     * Metoda uči predanu neuronsku mrežu na temelju datog skupa
     * uzoraka za učenje Algoritmom propagacije pogreške unatrag.
     * 
     * Method trains the given neural network using the given dataset
     * and the Backpropagation algorithm.
     * 
     * @param network Neural network to be trained
     * @param inputs Set of inputs
     * @param expectedOutputs Set of expected outputs
     * @param numOfEpochs Number of training epochs
     * @param learningRate Learning rate of the algorithm
     */
    public static void train(NeuralNetwork network, double[][] inputs, double[][] expectedOutputs,
            int numOfEpochs, int learningRate) {

        for (int epoch = 0; epoch < numOfEpochs; epoch++) {
            double[][] outputs = new double[inputs.length][];
            double[][] hiddenLayerOutputs = new double[inputs.length][];

            for (int i = 0; i < inputs.length; i++) {
                network.setInputs(inputs[i]);
                outputs[i] = network.calculateOutputs();
                hiddenLayerOutputs[i] = network.getHiddenLayerOutputs();
            }

            NeuralNetworkTrainer.updateWeights(network, inputs, expectedOutputs, outputs,
                    hiddenLayerOutputs, learningRate);

        }
    }

    /**
     * Updates all the weights of the given neural network based on
     * the results of training within one epoch of the Backpropagation
     * algorithm.
     * 
     * @param network Neural network
     * @param inputs Set of inputs
     * @param expectedOutputs Set of expected outputs
     * @param outputs Outputs given by the neural network for the
     *        given inputs
     * @param hiddenLayerOutputs Outputs of the hidden layer for the
     *        given inputs
     * @param learningRate Learning rate
     */
    private static void updateWeights(NeuralNetwork network, double[][] inputs,
            double[][] expectedOutputs, double[][] outputs, double[][] hiddenLayerOutputs,
            int learningRate) {

        updateOutputLayerWeights(network, inputs, expectedOutputs, outputs, hiddenLayerOutputs,
                learningRate);
        updateHiddenLayerWeights(network, inputs, expectedOutputs, outputs, hiddenLayerOutputs,
                learningRate);

    }

    /**
     * Updates all the weights in the output layer of the given neural
     * network based on the results of training within one epoch of
     * the Backpropagation algorithm.
     * 
     * @param network Neural network
     * @param inputs Set of inputs
     * @param expectedOutputs Set of expected outputs
     * @param outputs Outputs given by the neural network for the
     *        given inputs
     * @param hiddenLayerOutputs Outputs of the hidden layer for the
     *        given inputs
     * @param learningRate Learning rate
     */
    private static void updateOutputLayerWeights(NeuralNetwork network, double[][] inputs,
            double[][] expectedOutputs, double[][] outputs, double[][] hiddenLayerOutputs,
            int learningRate) {

        double[][] outputLayerWeights = network.getOutputLayerWeights();
        double[][] newOutputLayerWeights = new double[outputLayerWeights.length][];

        // for every output neuron
        for (int numOfNeuron = 0; numOfNeuron < outputLayerWeights.length; numOfNeuron++) {
            double[] neuronWeights = outputLayerWeights[numOfNeuron];

            // for every weight of the neuron
            for (int weightNum = 0; weightNum < neuronWeights.length; weightNum++) {

                double delta = 0;

                // for every learning example
                for (int inputNum = 0; inputNum < inputs.length; inputNum++) {
                    double currentOutput = outputs[inputNum][numOfNeuron];
                    double currentExpectedOutput = expectedOutputs[inputNum][numOfNeuron];
                    double currentInput = weightNum == neuronWeights.length - 1 ? 1
                            : hiddenLayerOutputs[inputNum][weightNum]; // treshold

                    delta += (currentExpectedOutput - currentOutput) * currentOutput
                            * (1 - currentOutput) * currentInput;
                }

                delta /= inputs.length;
                neuronWeights[weightNum] += learningRate * delta;
            }
            newOutputLayerWeights[numOfNeuron] = neuronWeights;
        }

        network.updateOutputLayerWeights(newOutputLayerWeights);
    }

    /**
     * Updates all the weights in the hidden layer of the given neural
     * network based on the results of training within one epoch of
     * the Backpropagation algorithm.
     * 
     * @param network Neural network
     * @param inputs Set of inputs
     * @param expectedOutputs Set of expected outputs
     * @param outputs Outputs given by the neural network for the
     *        given inputs
     * @param hiddenLayerOutputs Outputs of the hidden layer for the
     *        given inputs
     * @param learningRate Learning rate
     */
    private static void updateHiddenLayerWeights(NeuralNetwork network, double[][] inputs,
            double[][] expectedOutputs, double[][] outputs, double[][] hiddenLayerOutputs,
            int learningRate) {

        double[][] hiddenLayerWeights = network.getHiddenLayerWeights();
        double[][] outputLayerWeights = network.getOutputLayerWeights();
        double[][] newHiddenLayerWeights = new double[hiddenLayerWeights.length][];

        // for every hidden layer neuron
        for (int numOfNeuron = 0; numOfNeuron < hiddenLayerWeights.length; numOfNeuron++) {
            double[] neuronWeights = hiddenLayerWeights[numOfNeuron];
            // for every weight of the neuron
            for (int weightNum = 0; weightNum < neuronWeights.length; weightNum++) {

                double delta = 0;

                // for every learning example
                for (int inputNum = 0; inputNum < inputs.length; inputNum++) {
                    double hiddenNeuronOutput = hiddenLayerOutputs[inputNum][numOfNeuron];
                    double currentInput = (weightNum >= (neuronWeights.length - 1)) ? 1
                            : inputs[inputNum][weightNum]; // treshold
                    double partialDelta = hiddenNeuronOutput * (1 - hiddenNeuronOutput)
                            * currentInput;
                    double innerDeltaSum = 0;

                    // for every component of the output
                    int numOfOutputs = network.getOutputLayerSize();
                    for (int outputNum = 0; outputNum < numOfOutputs; outputNum++) {
                        double currentOutput = outputs[inputNum][outputNum];
                        double currentExpectedOutput = expectedOutputs[inputNum][outputNum];

                        innerDeltaSum += (currentExpectedOutput - currentOutput) * currentOutput
                                * (1 - currentOutput) * outputLayerWeights[outputNum][numOfNeuron];
                    }

                    delta += partialDelta * innerDeltaSum;
                }

                delta /= inputs.length;
                neuronWeights[weightNum] += learningRate * delta;
            }
            newHiddenLayerWeights[numOfNeuron] = neuronWeights;
        }

        network.updateHiddenLayerWeights(newHiddenLayerWeights);
    }

}
