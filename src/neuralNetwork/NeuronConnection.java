package neuralNetwork;

import java.io.Serializable;
// Meant to define the connections between neurons
public class NeuronConnection implements Serializable {
	
	private Neuron neuron;
    private double weight;

    public NeuronConnection(Neuron source, double weight) {
        this.neuron = source;
        this.weight = weight;
    }

    public Neuron getNeuron() {
        return neuron;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
	
}
