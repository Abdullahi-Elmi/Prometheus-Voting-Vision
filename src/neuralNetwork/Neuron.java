package neuralNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import neuralNetwork.activation.ActivationFunction;


public class Neuron implements Serializable {

    private List<NeuronConnection> incoming;
    private double currentOutput;
    private double currentDerivative;
    private double currentSum;
    private double currentError;
    private ActivationFunction accFunc;

    /**
     * Constructor to initialize a Neuron with a certain activation function
     * @param accFunc
     */
    public Neuron(ActivationFunction accFunc) {
    	incoming = new ArrayList<NeuronConnection>();
        this.accFunc = accFunc;
        this.currentError = 0;
        // we set the activation function when we create the network
    }

    /**
     * Add a connection to this Neuron
     * @param input the connection in question
     */
    public void addIncoming(NeuronConnection input) {
        incoming.add(input);
    }

    /**
     * Gets all the weights incoming into the neuron
     * @return double array with all the weights in order that they were added
     */
    public double[] getWeights() {
    	double[] result = new double[incoming.size()];
    	for (int i = 0 ; i < result.length ; i++) {
    		result[i] = incoming.get(i).getWeight();
    	}
    	return result;
    }

    /**
     * Calculate the weighted sum of all the inputs into this neuron with the output of the previous ones
     */
    private void computeSum() {
    	double result = 0;
        for (NeuronConnection nc : this.incoming) {
        	result += (double) nc.getWeight() * nc.getNeuron().getOutput();
        }
        this.currentSum = result;
    }

    /**
     * Activates the Neuron by computing its output based on previous connections and applying activation function
     */
    public void activate() {
    	computeSum();
    	this.currentOutput = accFunc.activate(this.currentSum);
    	this.currentDerivative = accFunc.derive(this.currentOutput);
    }

    // Basic Get & Set functions, Used for back-propagation, @param output
    public void setOutput(double output) {
        this.currentOutput = output;
    }

    public void setError(double error) {
        this.currentError = error;
    }

    public double getDerivative() {
        return this.currentDerivative;
    }

    public double getError() {
        return this.currentError;
    }
    
    public double getOutput() {
        return this.currentOutput;
    }
    
    public List<NeuronConnection> getIncoming() {
        return this.incoming;
    }
}
