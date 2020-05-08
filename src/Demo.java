import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

import experiments.Experiment;
import neuralNetwork.Layer;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;
import neuralNetwork.activation.LinearActivation;
import neuralNetwork.activation.ReluActivation;
import neuralNetwork.activation.SigmoidActivation;
import processing.AugmentationOptions;
import processing.Convolution;
import processing.DataPoint;
import processing.DatasetProcessor;
import processing.ImageVisualizer;
import java.util.Scanner;

public class Demo {
	public static String savedNN;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Please input an integer to represent the operation you'd like to complete.");
		System.out.println("Legend:");
		System.out.println("- 0: Quit the program \n- 1: datasetLoader() \n- 2: demoConvolution()");
		System.out.println("- 3: demoSRM() \n- 4: demoLoadNN() \n- 5: demoTrainNN() \n- 6: demoTestNN()");
		
		Scanner in = new Scanner(System.in);
		
		while(true) {
			System.out.println("Input a new command: ");
			int input = in.nextInt();
			
			if(input == 0) {
				System.out.println("Quitting the program");
				break;
			}
			
			switch (input) {
				case 1:
					System.out.println("demoDatasetLoader()");
					demoDatasetLoader();
					break;
				case 2:
					System.out.println("demoConvolution()");
					demoConvolution();
					break;
				case 3:
					System.out.println("demoSRM()");
					demoSRM();
					break;
				case 4:
					System.out.println("demoLoadNeuralNetwork()");
					demoLoadNeuralNetwork();
					break;
				case 5:
					System.out.println("demoTrainNeuralNetwork()");
					demoTrainNeuralNetwork();
					break;
				case 6:
					System.out.println("demoTestNeuralNetwork()");
					demoTestNeuralNetwork();
					break;
				default:
					System.out.println("I'm sorry, I couldn't understand your input.");
					break;
			}
		}
		in.close();
	}

	public static void demoDatasetLoader() {
		String filename = "imageDemo\\";
		//String filename = "proceduralDataDemo\\";
		//String csvFilename = "/home/miloview/Documents/VotingVisionMcGill/procData.csv";
		// csvFilename is never used in Milo's code
		
		long startTime = System.currentTimeMillis();
		
		// Flipping, not converting to grayscale, not scaling
		AugmentationOptions aug = new AugmentationOptions(true, false, -1);
		DatasetProcessor dataProc = null;
		try {
			dataProc = new DatasetProcessor(filename, aug);
		} catch (Exception e) {
			System.out.println("Problem loading Dataset...");
			e.printStackTrace();
			return;
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Length of dataSet: " + dataProc.getLength());
		System.out.println("Time to load: " + (endTime - startTime));
		
		DataPoint data = dataProc.get(0);
		DataPoint data2 = dataProc.get(1);
		int width = data.getWidth();
		int height = data.getHeight();
		
		ImageVisualizer.show(data.getIntImage(), width, height);
		ImageVisualizer.show(data2.getIntImage(), width, height);
		
//		dataProc.scale(10);
		
//		System.out.println("Writing dataset to file: " + csvFilename);
//		try {
//			dataProc.toCSV(csvFilename);
//		} catch (FileNotFoundException e) {
//			System.out.println("It did not work");
//		}
	}
	
	public static void demoConvolution() {
		String filename = "imageDemo\\";

		AugmentationOptions aug = new AugmentationOptions(false, false, -1);
		DatasetProcessor dataProc = null;
		try {
			dataProc = new DatasetProcessor(filename, aug);
		} catch (Exception e) {
			System.out.println("Problem loading Dataset...");
			e.printStackTrace();
			return;
		}
		
		dataProc.applyConvolution(Convolution.SOBEL_VERTICAL, true);
		
		
		for (DataPoint d : dataProc) {
			int width = d.getWidth();
			int height = d.getHeight();
			
			ImageVisualizer.show(d.getIntImage(), width, height);
		}
		DataPoint data = dataProc.get(0);
		
	}	
	
	public static void demoSRM() {
		String filename = "imageDemo\\";
		
		AugmentationOptions aug = new AugmentationOptions(false, false, -1);
		DatasetProcessor dataProc = null;
		try {
			dataProc = new DatasetProcessor(filename, aug);
		} catch (Exception e) {
			System.out.println("Problem loading Dataset...");
			e.printStackTrace();
			return;
		}
		
		dataProc.mergeRegions(32);
		
		
		for (DataPoint d : dataProc) {
			int width = d.getWidth();
			int height = d.getHeight();
			
			ImageVisualizer.show(d.getIntImage(), width, height);
		}
		DataPoint data = dataProc.get(0);
	}
	
	public static void demoLoadNeuralNetwork() {
		
		long startTime = System.currentTimeMillis();
		
		int layer_1 = 1000;
		int layer_2 = 1000;
		int layer_3 = 100;
		int layer_4 = 4;
		
		NeuralNetwork test = new NeuralNetwork("test neural network");
		
        Layer inputLayer = new Layer(null);
        for (int a = 0 ; a < layer_1 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            inputLayer.addNeuron(n);
        }
        
        Layer hidden = new Layer(inputLayer);
        for (int a = 0 ; a < layer_2 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            hidden.addNeuron(n);
        }
        
        Layer hidden2 = new Layer(hidden);
        for (int a = 0 ; a < layer_3 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            hidden2.addNeuron(n);
        }

        Layer outputLayer = new Layer(hidden2);
        for (int a = 0 ; a < layer_4 ; a++) {
        	Neuron n = new Neuron(new LinearActivation());
            outputLayer.addNeuron(n);
        }
        
        test.addLayer(inputLayer);
        test.addLayer(hidden);
        test.addLayer(hidden2);
        test.addLayer(outputLayer);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Time to load Neural Network: " + (endTime - startTime));
		
		double[] testInput = new double[1000];
		Random rand = new Random();
		
		for (int i = 0 ; i < testInput.length ; i++) {
			testInput[i] = rand.nextDouble();
		}
		test.randomize();

		startTime = System.currentTimeMillis();
		
		test.setInputs(testInput);
		double[] outputs = test.getOuputs();
		
		System.out.println(Arrays.toString(outputs));
		
		endTime = System.currentTimeMillis();
		System.out.println("Time to feed forward: " + (endTime - startTime));
		
	}
	
	private static NeuralNetwork initNN() {
		int layer_1 = 9600;
		int layer_2 = 1000;
		int layer_3 = 100;
		int layer_4 = 4;
		
		NeuralNetwork nn = new NeuralNetwork("test neural network");
		
        Layer inputLayer = new Layer(null);
        for (int a = 0 ; a < layer_1 ; a++) {
        	Neuron n = new Neuron(new ReluActivation());
            n.setOutput(0);
            inputLayer.addNeuron(n);
        }
        
        Layer hidden = new Layer(inputLayer);
        for (int a = 0 ; a < layer_2 ; a++) {
        	Neuron n = new Neuron(new ReluActivation());
            n.setOutput(0);
            hidden.addNeuron(n);
        }
        
        Layer hidden2 = new Layer(hidden);
        for (int a = 0 ; a < layer_3 ; a++) {
        	Neuron n = new Neuron(new ReluActivation());
            n.setOutput(0);
            hidden2.addNeuron(n);
        }

        Layer outputLayer = new Layer(hidden2);
        for (int a = 0 ; a < layer_4 ; a++) {
        	Neuron n = new Neuron(new ReluActivation());
            n.setOutput(0);
            outputLayer.addNeuron(n);
        }
        
        nn.addLayer(inputLayer);
        nn.addLayer(hidden);
        nn.addLayer(hidden2);
        nn.addLayer(outputLayer);

        nn.randomize();
        
        return nn;
	}
	
	private static double[][][] getDataset(String filename){
		//String filename = "proceduralDataDemo\\";
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
	
	public static void demoTrainNeuralNetwork() {
		
        NeuralNetwork nn = initNN();
		
        double[][][] dataset = getDataset("training\\");
        double[][] inputs = dataset[0];
		double[][] outputs = dataset[1];
		// int inputLength = (int) (inputs.length * 0.8);
		// System.out.println("The input layer should be:" + inputLength);
		Experiment exp = new Experiment(nn, inputs, outputs, 0.8f);
		System.out.println("Initializing training...");
		exp.train(0.1, 5, 1, 20, 0.05, -1);
		System.out.println("Done...");
		
		System.out.println("Saving trained weights...");
		savedNN = exp.saveNeuralNetwork("..\\");
		System.out.println("Done...");
		
	}
	
	public static void demoTestNeuralNetwork() {
		
		//String filename = "..\\test_neural_network-1585875432368.nn";
		String filename = savedNN;
		double[][][] dataset = getDataset("testing\\");
		double[][] inputs = dataset[0];
		double[][] outputs = dataset[1];
		
		//String filename = "/home/miloview/Documents/VotingVisionMcGill/test_neural_network-1575848141195.nn";
		
		System.out.println("Loading Neural Network from disk...");
		NeuralNetwork nn = NeuralNetwork.loadFromFile(filename);
		System.out.println("Done...");
		
		System.out.println("Testing...");
		Experiment exp = new Experiment(nn, inputs, outputs, 0.01f);
		exp.test(null);
		System.out.println("Done...");

	}
}
