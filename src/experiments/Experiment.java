package experiments;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.json.simple.parser.ParseException;

import neuralNetwork.NeuralNetwork;
import neuralNetwork.Trainer;
import processing.DataPoint;


public class Experiment {
	
	private double[][] inputTraining;
	private double[][] outputTraining;
	private double[][] inputTesting;
	private double[][] outputTesting;
	
	private int testingLength;
	private int trainingLength;
	private NeuralNetwork expNN;
	private String variable;
	
	private ArrayList<Double> resultErrors;
	
	public Experiment(NeuralNetwork nn, double[][] input, double[][] output, float split) {
		// Store the neural network
		this.expNN = nn;
		if (!(split > 0 && split < 1))
			throw new IllegalArgumentException();
		
		// Shuffle the input and output datasets
		shuffle(input);
		shuffle(output);
			
		// Split and store training and testng subsets
		this.trainingLength = (int) (input.length * split);
		this.testingLength = input.length - this.trainingLength;
		
		this.inputTraining = this.getTrainingSubset(input);
		this.inputTesting = this.getTestingSubset(input);
		
		this.outputTraining = this.getTrainingSubset(output);
		this.outputTesting = this.getTestingSubset(output);
	}
	
	public void train(double learningRate, double momentum, double characteristicTime, int maxEpochs, double maxError, int checkpoint) {
		/** 
		 * Train the neural network with the current Dataset and parameter
		 */
		Trainer trainer = new Trainer(expNN, learningRate, momentum, characteristicTime);
		trainer.train(inputTraining, outputTraining, maxEpochs, maxError, checkpoint);
	}

	
	public void test(String saveResults) {
		/**
		 * test the neural network that has been trained with given parameters
		 */
		int rightCounter = 0;
		int wrongCounter = 0;
		// Initialize confusion Matrix
		int[][] confusionMatrix = new int[outputTesting[0].length][outputTesting[0].length];
		for (int i = 0 ; i < confusionMatrix.length ; i++) {
			int[] zeros = new int[confusionMatrix.length];
			Arrays.fill(zeros, 0);
			confusionMatrix[i] = zeros;
		}
		
		for (int i = 0 ; i < outputTesting.length ; i++) {
			int expectedClass = getMaxIndex(outputTesting[i]);
			expNN.setInputs(inputTesting[i]);
			double[] outputs = expNN.getOuputs();
			int resultClass = getMaxIndex(outputs);
			
			System.out.println("Expected class: " + expectedClass + "		Got class: " + resultClass);
			
			confusionMatrix[expectedClass][resultClass] += 1;
			
			if (expectedClass == resultClass) {
				rightCounter++;
			} else {
				wrongCounter++;
			}
		}
		
		double accuracy = (double) rightCounter / outputTesting.length;
		
		System.out.println("Confusion matrix (Rows = expected): ");
		
		printConfusionMatrix(confusionMatrix, Arrays.toString(DataPoint.Locations.values()).replaceAll("^.|.$", "").split(", "));
		System.out.println("The accuracy over the test set is " + accuracy);
	}
	
	private static void printConfusionMatrix(int[][] matrix, String[] classes) {
		System.out.print("	|	");
		for (int j = 0 ; j < classes.length ; j++) {
			System.out.print(classes[j] + "	|	");
		}
		System.out.println();
		for (int j = 0 ; j < classes.length ; j++) {
			System.out.print(classes[j] + "	|	");
			for (int k = 0 ; k < classes.length ; k++) {
				System.out.print(matrix[j][k] + "	|	");
			}
			System.out.println();
		}
	}
	
	private int getMaxIndex(double[] input) {
		double maxVal = input[0];
		int maxIndex = 0;

		for (int i = 0; i < input.length; i++) 
		{
			if (maxVal < input[i]) 
			{
				maxVal = input[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public void saveDataset(String filepath) {
		// TODO: Save the current dataset as a csv file
	}
	
	public String saveNeuralNetwork(String filepath) {
		String dest = this.expNN.save(filepath);
		return dest;
	}
	
	private double[][] getTrainingSubset(double[][] input) {
		double[][] result = new double[this.trainingLength][];
		for (int i = 0 ; i < this.trainingLength ; i++)
			result[i] = input[i];
		return result;
	}
	
	private double[][] getTestingSubset(double[][] input){
		double[][] result = new double[this.testingLength][];
		for (int i = this.trainingLength, counter = 0 ; i < this.trainingLength + this.testingLength; i++) {
			result[counter] = input[i];
			counter++;
		}
		return result;
	}
	
	private void shuffle(double[][] array) {
		Random rand = new Random();
		
		for (int i = 0; i < array.length; i++) {
			int randomIndexToSwap = rand.nextInt(array.length);
			double[] temp = array[randomIndexToSwap];
			array[randomIndexToSwap] = array[i];
			array[i] = temp;
		}
	}
	
}
