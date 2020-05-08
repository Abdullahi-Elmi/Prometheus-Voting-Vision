package experiments;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import org.neuroph.core.learning.error.MeanSquaredError;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import neuralNetwork.*;
import neuralNetwork.activation.LinearActivation;
import neuralNetwork.activation.SigmoidActivation;

import org.json.simple.parser.ParseException;

import processing.*;


public class ExperimentRunner {
	
	public static String savedNN;
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		// Current Experiment here 
		testProcedural();
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;

		System.out.println("Execution time in nanoseconds  : " + timeElapsed);

		System.out.println("Execution time in milliseconds : " + 
								timeElapsed / 1000000);
	}
	
	public static NeuralNetwork BenchmarkGenerating() {
		int layer_1 = 1536;
		int layer_2 = 100;
		int layer_3 = 4;
		
		NeuralNetwork test = new NeuralNetwork("test neural network");
		
        Layer inputLayer = new Layer(null);
        for (int a = 0 ; a < layer_1 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            n.setOutput(0);
            inputLayer.addNeuron(n);
        }
        
        Layer hidden = new Layer(inputLayer);
        for (int a = 0 ; a < layer_2 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            n.setOutput(0);
            hidden.addNeuron(n);
        }
        


        Layer outputLayer = new Layer(hidden);
        for (int a = 0 ; a < layer_2 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            n.setOutput(0);
            hidden.addNeuron(n);
        }
        
        test.addLayer(inputLayer);
        test.addLayer(hidden);
        test.addLayer(outputLayer);
        
        return test;
        
	}
	
	public static double[][][] getDataset(){
		
		String filename = "proceduralDataDemo\\";
		AugmentationOptions aug = new AugmentationOptions(false, false, -1);
		DatasetProcessor dataProc = null;
		try {
			dataProc = new DatasetProcessor(filename, aug);
		} catch (Exception e) {
			System.out.println("Problem loading Dataset...");
			e.printStackTrace();
			return null;
		}
		
		System.out.println("Convoluting...");
		dataProc.applyConvolution(Convolution.SOBEL_VERTICAL, false);
		System.out.println("Scaling...");
		dataProc.scale(8);
		System.out.println("Normalizing...");
		dataProc.normalize(0, 1);
		
		double[][] convInputs = dataProc.getNNInputs();
		double[][] outputs = dataProc.getNNOutputs(false);
		
		try {
			dataProc = new DatasetProcessor(filename, aug);
		} catch (Exception e) {
			System.out.println("Problem loading Dataset...");
			e.printStackTrace();
			return null;
		}
		
		System.out.println("Merging...");
		dataProc.mergeRegions(32);
		System.out.println("Scaling...");
		dataProc.scale(8);
		System.out.println("Normalizing...");
		dataProc.normalize(0, 1);
		
		double[][] srmInputs = dataProc.getNNInputs();
		
		double[][] inputs = new double[convInputs.length][convInputs[0].length + srmInputs[0].length];
		
		for (int i = 0 ; i < inputs.length ; i++) {
			System.arraycopy(convInputs[i], 0, inputs[i], 0, convInputs[i].length);
	        System.arraycopy(srmInputs[i], 0, inputs[i], convInputs[i].length, srmInputs[i].length);
		}

		double[][][] result = {inputs, outputs};
		return result;
	}
	
	public static void trainProcedural() {
		
		int layer_1 = 9600;
		int layer_2 = 1000;
		int layer_3 = 100;
		int layer_4 = 40;
		int layer_5 = 4;
		long startTime = System.nanoTime();

		NeuralNetwork nn = new NeuralNetwork("ProceduralStructure2");
		
        Layer inputLayer = new Layer(null);
        for (int a = 0 ; a < layer_1 ; a++) {
        	Neuron n = new Neuron(new SigmoidActivation());
            n.setOutput(0);
            inputLayer.addNeuron(n);
        }
        
        Layer hidden = new Layer(inputLayer);
        for (int a = 0 ; a < layer_2 ; a++) {
        	Neuron n = new Neuron(new SigmoidActivation());
            n.setOutput(0);
            hidden.addNeuron(n);
        }
        
        Layer hidden2 = new Layer(hidden);
        for (int a = 0 ; a < layer_3 ; a++) {
        	Neuron n = new Neuron(new SigmoidActivation());
            n.setOutput(0);
            hidden2.addNeuron(n);
        }
        
        Layer hidden3 = new Layer(hidden2);
        for (int a = 0 ; a < layer_4 ; a++) {
        	Neuron n = new Neuron(new SigmoidActivation());
            n.setOutput(0);
            hidden3.addNeuron(n);
        }

        Layer outputLayer = new Layer(hidden3);
        for (int a = 0 ; a < layer_5 ; a++) {
        	Neuron n = new Neuron(new SigmoidActivation());
            n.setOutput(0);
            outputLayer.addNeuron(n);
        }
        
        nn.addLayer(inputLayer);
        nn.addLayer(hidden);
        nn.addLayer(hidden2);
        nn.addLayer(hidden3);
        nn.addLayer(outputLayer);

        nn.randomize();
        
        long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;

		System.out.println("time to load network in milliseconds : " + 
								timeElapsed / 1000000);
        
        double[][][] dataset = getDataset();
		double[][] inputs = dataset[0];
		double[][] outputs = dataset[1];

		Experiment exp = new Experiment(nn, inputs, outputs, 0.8f);
		System.out.println("Initializing training...");
		exp.train(0.001, 1, 1, 100, 0.005, 5);
		System.out.println("Done...");
		
		System.out.println("Saving trained weights...");
		savedNN = exp.saveNeuralNetwork("..\\");
		System.out.println("Done...");
	}
	
	public static void testProcedural() {
		
		//NeuralNetwork nn = NeuralNetwork.loadFromFile("/home/miloview/Documents/VotingVisionMcGill/backups/ProceduralStructure2-1576127522031.nn");
		NeuralNetwork nn = NeuralNetwork.loadFromFile(savedNN);
        double[][][] dataset = getDataset();
		double[][] inputs = dataset[0];
		double[][] outputs = dataset[1];

		Experiment exp = new Experiment(nn, inputs, outputs, 0.3f);
		exp.test(null);
	}
	

}
