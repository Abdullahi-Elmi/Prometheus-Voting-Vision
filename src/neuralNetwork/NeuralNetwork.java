package neuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.IIOException;

//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
// This isn't really meant for public usage (internal class in java, that can change b/w updates)

public class NeuralNetwork implements Serializable {
	/**
	 * Basic implementation of a multilayer perceptron
	 */
	private String tag;
    private List<Layer> layers;
    private Layer inputLayer;
    private Layer outputLayer;
	
	
	public NeuralNetwork(String tag) {
		this.tag = tag;
		this.layers = new ArrayList<Layer>();
	}
	
	public void addLayer(Layer l) {
		this.layers.add(l);
		
		if (l.getPreviousLayer() == null && this.layers.size() == 1)
			this.inputLayer = l;
		
		if(this.layers.size() > 1)
            this.layers.get(layers.size() - 2).setNextLayer(l);

        this.outputLayer = this.layers.get(this.layers.size() - 1);
	}
	
	public void setInputs(double[] inputs) {
		if (inputs.length != this.inputLayer.getNeurons().size())
			throw new IllegalArgumentException("Size of inputs should be the same as size of input Layer");
		
		int biasOffset = 0;
		if (this.inputLayer.hasBias())
			biasOffset = 1;
		for (int i = biasOffset ; i < inputs.length ; i++) 
			this.inputLayer.getNeurons().get(i).setOutput(inputs[i - biasOffset]);
	}
	
	/**
	 * Set all the weights in the network to random values
	 */
	public void randomize() {
		for(Layer layer : this.layers) {
            for(Neuron neuron : layer.getNeurons()) {
                for(NeuronConnection c : neuron.getIncoming()) {
                    c.setWeight((Math.random() * 1) - 0.5);
                }
            }
        }
	}
	
	public List<Layer> getLayers() {
		return this.layers;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	/**
	 * Forwards computations through all the layers in the network
	 * @return the resulting values at the output layer
	 */
	public double[] getOuputs() {
		double[] outputs = new double[this.outputLayer.getNeurons().size()];
		
		// Feed forward all the Layers in the Network
        for(int i = 1; i < layers.size(); i++) {
            layers.get(i).forward();
        }
        
        int count = 0;
        for (Neuron n : this.outputLayer.getNeurons())
        	outputs[count++] = n.getOutput();
		return outputs;
	}
	
	
	/**
	 * Save the whole Neural Network to disk
	 * @param destination folder where the file should be saved
	 */
	public String save(String destination) {
        String fileName = destination + this.tag.replaceAll(" ", "_") + "-" + new Date().getTime() +  ".nn";
        System.out.println("Writing trained neural network to file " + fileName);

        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(this);
        }

        catch(IOException e) {
            System.out.println("Could not write to file: " + fileName);
            e.printStackTrace();
        }

        finally {
            try {
                if(objectOutputStream != null) {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
            }

            catch(IOException e) {
                System.out.println("Could not write to file: " + fileName);
                e.printStackTrace();
            }
        }
        return fileName;
	}
	
	/**
	 * Static method to load a neural network from disk
	 * @param nnetFile file location where the desired Neural Network isaved
	 * @return a reference to the Neural Network
	 */
	public static NeuralNetwork loadFromFile(String nnetFile) {
		NeuralNetwork nn = null;
		try {
			FileInputStream fi = new FileInputStream(new File(nnetFile));
			ObjectInputStream oi = new ObjectInputStream(fi);

			// Read objects
			nn = (NeuralNetwork) oi.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Could not load Neural Network");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load Neural Network");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Could not load Neural Network");

		} 
		
		return nn;
	}
}
