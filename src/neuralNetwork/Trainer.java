package neuralNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import experiments.Plotter;

/**
 * Class that trains a network using Backpropagation algorithm
 * Inspired by: https://github.com/vivin/DigitRecognizingNeuralNetwork
 * 
 * @author Milo Sobral
 *
 */
public class Trainer {
	
	private NeuralNetwork neuralNetwork;
    private double learningRate;
    private double momentum;
    private double charTime;
    private double epoch;
    private ArrayList<Double> errorList;
    
    public Trainer(NeuralNetwork neuralNetwork, double learningRate, double momentum, double characteristicTime) {
    	this.neuralNetwork = neuralNetwork;
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.charTime = characteristicTime;
    }
    
    /**
     * Trains the neural network
     * @param inputs double[][] containing all the inputs to train on
     * @param outputs double[][] containing all the outputs in the same order as the inputs
     * @param maxEpochs Max number of iterations over the dataset before training is finished
     * @param minError Once error reaches this treshold, training is stopped
     * @param checkpoint save the dataset every 'checkpoint' epochs
     * @return List of all the losses for each epoch
     */
    public ArrayList<Double> train(double[][] inputs, double[][] outputs, int maxEpochs, double minError, int checkpoint) {
    	double error;
        double sum = 0.0;
        double average = 25.0;
        int epoch = 1;
        int samples = 25;
        double[] errors = new double[samples];
        errorList = new ArrayList<Double>();
        
		Plotter plt = Plotter.createAndShowGui(errorList);

        do {

        	plt.setScores(errorList);
        	
            error = backpropagate(inputs, outputs);

            sum -= errors[epoch % samples];
            errors[epoch % samples] = error;
            sum += errors[epoch % samples];

            
            errorList.add(error);
            
            System.out.println("Error for epoch " + epoch + ": " + 
            		error + " Learning rate: " + 
            		(learningRate / (1 + (epoch / charTime))));
            
            // Save the current Neural Network every 'checkpoint' epochs to make sure no progress is lost
            if (epoch % checkpoint == 0 && epoch != 0 && checkpoint != -1) {
            	String destination = "backups\\";
            	File directory = new File(destination);
                if (! directory.exists()){
                    directory.mkdir();
                }
            	this.neuralNetwork.save(destination);
            }
            epoch++;
        } while(error > minError && epoch <= maxEpochs);
        
        return errorList;
    }
    	
    /**
     * One epoch of the backpropagation algorithm
     * @param inputs double[][] containing all the inputs to train on
     * @param expectedOutputs double[][] containing all the outputs in the same order as the inputs
     * @return the error for that epoch
     */
    private double backpropagate(double[][] inputs, double[][] expectedOutputs) {
    	double error = 0;

        Map<NeuronConnection, Double> connectionDiffs = new HashMap<NeuronConnection, Double>();

        for (int i = 0; i < inputs.length; i++) {

            double[] input = inputs[i];
            double[] expectedOutput = expectedOutputs[i];

            List<Layer> layers = neuralNetwork.getLayers();

            long startTime = System.nanoTime();
            
            neuralNetwork.setInputs(input);
            double[] output = neuralNetwork.getOuputs();
            
            System.out.println(Arrays.toString(output));

            System.out.println(Arrays.toString(expectedOutput));
            
            long endTime = System.nanoTime();
    		long timeElapsed = endTime - startTime;

    		System.out.println("Time to feedforward in milliseconds : " + 
    								timeElapsed / 1000000);
    		
    		startTime = System.nanoTime();
            
            // First step calculate error for each neuron
            for (int j = layers.size() - 1; j > 0; j--) {
                Layer layer = layers.get(j);
                
                // Go through each neuron in the layer, go through each outgoing connection and compute the error of the Neuron
                for (int k = 0; k < layer.getNeurons().size(); k++) {
                    Neuron neuron = layer.getNeurons().get(k);
                    double neuronError = 0;

                    if (layer.isOutputLayer()) {
                        neuronError = neuron.getDerivative() * (output[k] - expectedOutput[k]);
                    } else {
                        neuronError = neuron.getDerivative();

                        double sum = 0;
                        List<Neuron> downstreamNeurons = layer.getNextLayer().getNeurons();
                        for (Neuron downstreamNeuron : downstreamNeurons) {

                            int l = 0;
                            boolean found = false;
                            while (l < downstreamNeuron.getIncoming().size() && !found) {
                            	NeuronConnection synapse = downstreamNeuron.getIncoming().get(l);

                                if (synapse.getNeuron() == neuron) {
                                    sum += (synapse.getWeight() * downstreamNeuron.getError());
                                    found = true;
                                }

                                l++;
                            }
                        }

                        neuronError *= sum;
                    }

                    neuron.setError(neuronError);
                }
            }

            // Second step 
            // Update weights previously computed errors
            for(int j = layers.size() - 1; j > 0; j--) {
                Layer layer = layers.get(j);

                for(Neuron neuron : layer.getNeurons()) {

                    for(NeuronConnection connection : neuron.getIncoming()) {
                    	
                    	// Update the weights by a factor of the learning rate and the momentum
                        double newLearningRate = charTime > 0 ? learningRate / (1 + (epoch / charTime)) : learningRate;
                        double delta = newLearningRate * neuron.getError() * connection.getNeuron().getOutput();

                        if(connectionDiffs.get(connection) != null) {
                            double previousDelta = connectionDiffs.get(connection);
                            delta += momentum * previousDelta;
                        }

                        connectionDiffs.put(connection, delta);
                        connection.setWeight(connection.getWeight() - delta);
                    }
                }
            }
            
            endTime = System.nanoTime();
    		timeElapsed = endTime - startTime;

    		System.out.println("Time to backpropagate in milliseconds : " + 
    								timeElapsed / 1000000);

            output = neuralNetwork.getOuputs();
            error += computeError(output, expectedOutput);
           
        }

        return error / inputs.length;
    }
    
    // Computes mean squared error function on an output pair
    private double computeError(double[] actual, double[] expected) {
    	if (actual.length != expected.length) {
            throw new IllegalArgumentException("Length must be equal");
        }

        double sum = 0.0;

        for (int i = 0; i < expected.length; i++) {
            sum += Math.pow(expected[i] - actual[i], 2);
        }
        
        return sum / 2.0;    
    }
    
    public ArrayList<Double> getErrors() {
    	return this.errorList;
    }
    
}
