package neuralNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Layer implements Serializable {

	private List<Neuron> neurons;
    private Layer previousLayer;
    private Layer nextLayer;
    private Neuron bias; 
	
	public Layer() {
		previousLayer = null;
		neurons = new ArrayList<Neuron>();
	}
	
	public Layer(Layer prev) {
		previousLayer = prev;
		neurons = new ArrayList<Neuron>();
	}
	
	public Layer(Layer prev, Neuron bias) {
		previousLayer = prev;
		neurons = new ArrayList<Neuron>();
		// If a bias neuron is provided, add it into the layer
		this.bias = bias;
        neurons.add(bias);
	}

    public void addNeuron(Neuron neuron) {
    	neurons.add(neuron);
    	// This will always generate a fully connected Layer
        if(previousLayer != null) {
            for(Neuron previousLayerNeuron : previousLayer.getNeurons()) {
                neuron.addIncoming(new NeuronConnection(previousLayerNeuron, (Math.random() * 1) - 0.5));
            }
        }

    }

    public void addNeuron(Neuron neuron, double[] weights) {
    	
    	neurons.add(neuron);

        if(previousLayer != null) {
        	// Check if we have to connect to the previous layer
            if(previousLayer.getNeurons().size() != weights.length) {
                throw new IllegalArgumentException("Number of weights incorrect to initialize neurons");
            }

            List<Neuron> previousLayerNeurons = previousLayer.getNeurons();
            for(int i = 0; i < previousLayerNeurons.size(); i++) {
                neuron.addIncoming(new NeuronConnection(previousLayerNeurons.get(i), weights[i]));
            }
        }
    }

    public void forward() {
    	int biasOffset = 0;
    	if (this.hasBias()) {
    		biasOffset = 1;
    	}
    	
    	// Forwards all the current weights by computing all the neurons
    	for (int i = biasOffset ; i < this.neurons.size(); i++)
    		this.neurons.get(i).activate();
    }
    
    /**
     * Setter Methods
     */

    void setPreviousLayer(Layer previousLayer) {
		this.previousLayer = previousLayer;
    }
    
    void setNextLayer(Layer nextLayer) {
		this.nextLayer = nextLayer;
		nextLayer.setPreviousLayer(this);
    }
    
    /** 
     * Getter Methods
     */

    public Layer getNextLayer() {
    	return this.nextLayer;
    }
    
	public List<Neuron> getNeurons() {
        return this.neurons;
    }
	
    public Layer getPreviousLayer() {
        return this.previousLayer;
    }

    public boolean isOutputLayer() {
    	return this.nextLayer == null;
    }

    public boolean hasBias() {
    	return this.bias != null;
    }
	
	
}
