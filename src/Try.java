import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Try {
	public static void main(String[] args) {
		
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
	

}
