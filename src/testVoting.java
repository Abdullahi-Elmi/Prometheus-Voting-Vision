import java.util.Arrays;
import java.util.Random;
import experiments.Experiment;
import neuralNetwork.Layer;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;
import neuralNetwork.activation.LinearActivation;
import neuralNetwork.activation.ReluActivation;
import processing.AugmentationOptions;
import processing.Convolution;
import processing.DataPoint;
import processing.DatasetProcessor;
import processing.ImageVisualizer;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class testVoting {
	public static String savedNN;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Please input an integer to represent the operation you'd like to complete.");
		System.out.println("Legend:");
		System.out.println("- 0: Quit the program \n- 1: tryPCA() \n- 2: datasetLoader() \n- 3: demoConvolution()");
		System.out.println("- 4: demoSRM() \n- 5: demoLoadNN() \n- 6: demoTrainNN() \n- 7: demoTestNN()");
		
		Scanner in = new Scanner(System.in);
		
		while(true) {
			System.out.println("Input a new command: ");
			int input = in.nextInt();
			
			if(input == 0) {
				System.out.println("Quitting the program");
				in.close();
				System.exit(0);
			}
			
			switch (input) {
				case 1:
					System.out.println("tryPCA()");
					tryPCA();
					break;
				case 2:
					System.out.println("demoDatasetLoader()");
					demoDatasetLoader();
					break;
				case 3:
					System.out.println("demoConvolution()");
					demoConvolution();
					break;
				case 4:
					System.out.println("demoSRM()");
					demoSRM();
					break;
				case 5:
					System.out.println("demoLoadNeuralNetwork()");
					demoLoadNeuralNetwork();
					break;
				case 6:
					System.out.println("demoTrainNeuralNetwork()");
					demoTrainNeuralNetwork();
					break;
				case 7:
					System.out.println("demoTestNeuralNetwork()");
					demoTestNeuralNetwork();
					break;
				default:
					System.out.println("I'm sorry, I couldn't understand your input.");
					break;
			}
		}
	}
	
	
	public static void tryPCA() {
		String imageFolder = "image/data";//store the path to the folder with images
		String labelFolder = "image/labels";//store the path to the folder with labels
		int dimension = 150; //Set the jet dimension. 
		//All jets will be reduced to the dimension above by PCA before input into the classifier. 		
		int trainSize = 100;//set the number of images for training
		int testSize = 54;//set the number of images for testing
		//(Their size can be changed for other split ratio; now is 100:54 )
		
		JetBuilder jb = new JetBuilder();//initialze the JetBuilder
		double [] jet= jb.buildJet("image/data/20191207_010957.jpg");
		
		double [][] trainJets = new double [trainSize][ ];  //initialize empty array to store training jets
		int [] trainLabels = new int [trainSize];                   //initialize empty array to store labels for training jets
		double [][] testJets = new double [testSize][ ];    //initialize empty array to testing jets
		int [] testLabels = new int [testSize];                     //initialize empty array to store labels for testing jets
		                                                      
		long start = System.nanoTime();   //record the start time
		
		final File folder = new File(imageFolder);   //Open the folder that stores the images
		
		int i = 0;
		for (final File fileEntry : folder.listFiles()) //This loop transforms all images in the folder to jet,
		{   String name = fileEntry.getName();          //finds their labels in the labels folder, 
		   if(!(name.equals(".DS_Store")))              //and saves them into arrays initialized above
			{
			   if(i<trainJets.length) //Transform and save images into the training jets
			   {   //Arrays.copyOfRange(jb.buildJet("image/data/"+ name), jet.length-dimension, jet.length); 				   
				   trainJets[i]= Arrays.copyOfRange(jb.buildJet(imageFolder+"/"+ name), jet.length-200, jet.length);//build a jet for this training image and then truncate it (small enough for PCA reducer to calculate). Keep the last 2 entries in jet. 				   
				   trainLabels[i]=getLabelForSurroundingClassification(labelFolder+ "/"+ name.substring(0, 15) +".json"); //read json file in the labels folder to get the label for this image
			   }
			   else if(i<trainJets.length+testJets.length)//Transform and save images into the testing jets
			   {
				   testJets[i-trainJets.length]= Arrays.copyOfRange(jb.buildJet(imageFolder+"/"+ name), jet.length-200, jet.length); //build a jet for this testing image and then truncate it (small enough for PCA reducer to calculate). Keep the last 2 entries in jet. 				 
				   testLabels[i-trainJets.length]=getLabelForSurroundingClassification(labelFolder+"/"+ name.substring(0, 15) +".json"); //read json file in the labels folder to get the label for this image
			   }
			   i++;
			}
		}
		
		PCAreducer rd = new PCAreducer(trainJets, dimension);//initialize a PCA reducer
		trainJets = rd.transformMultiple(trainJets);//reduce the dimension of the training jets
		testJets = rd.transformMultiple(testJets);//reduce the dimension of the testing jets
		
		System.out.println("Total number of images:" + i);//print total number of images
		System.out.println("Number of training images:" +  trainSize);
		System.out.println("Number of testing images:" + testSize);
		
		votingVisionPCA pca = new votingVisionPCA(4, trainJets[0].length, 1000000000); //initialize the classifier with the number of classes, jets' dimension, and the threshold for undefined.
		pca.train( trainJets, trainLabels);                                            //Train the classifier with the training jets
		double acc = pca.evaluate(testJets, testLabels);        //Test the classifier with the testing jets
		System.out.println( "Overall accuracy:"+acc);           //print the accuracy on the testing jets
		
		long end = System.nanoTime();                                                  //record the ending time
		System.out.println("Running time:" + (end-start)/(1000000));                   //print the running time in millisecond 
	}
	
	public static int getLabelForSurroundingClassification(String name ) //get the label of the input training image for the environment classification
	{
		JSONParser parser = new JSONParser();
		Object obj = null;
		try { obj = parser.parse(new FileReader(name)); } catch (Exception e) {}
        JSONObject jsonObject = (JSONObject) obj;
        String loc = (String) jsonObject.get("location");
        if( loc.equals("room"))
        {return 0;}
        else if( loc.equals("hall"))
        {return 1;}
        else if( loc.equals("stair"))
        {return 2;}
        else 
        {return 3;}
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
		
		/*
		dataProc.scale(10);
		
		System.out.println("Writing dataset to file: " + csvFilename);
		try {
			dataProc.toCSV(csvFilename);
		} catch (FileNotFoundException e) {
			System.out.println("It did not work");
		}*/
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
		//String filename = "/home/miloview/Documents/VotingVisionMcGill/test_neural_network-1575848141195.nn";
		//String filename = "test_neural_network-1585875432368.nn";
		String filename = savedNN;
		double[][][] dataset = getDataset("testing\\");
		double[][] inputs = dataset[0];
		double[][] outputs = dataset[1];
		
		System.out.println("Loading Neural Network from disk...");
		NeuralNetwork nn = NeuralNetwork.loadFromFile(filename);
		System.out.println("Done...");
		
		System.out.println("Testing...");
		Experiment exp = new Experiment(nn, inputs, outputs, 0.01f);
		exp.test(null);
		System.out.println("Done...");
	}
}
