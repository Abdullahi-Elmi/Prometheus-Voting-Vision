import java.util.Arrays;

public class votingVisionPCA{//This is the class for the classifier
	
	 private double [][]dictionary;
	 private int threshold;
	 private int [] jetUsed;
	
     public votingVisionPCA(int numberOfClass, int numberOfFeatures, int thresholdIn)
	{ //constructor for the classifier
      //input how many classes to classify, 
      //how many features does each jet have,and
      //the threshold for a image to be not classifiable. 
		dictionary = new double  [numberOfClass][numberOfFeatures];
		jetUsed = new int [numberOfClass];
		threshold = thresholdIn;
	}
	
	 public void train(double [][] jetsForTrain, int [] labels)//Ideal training examples should be balanced
	 { 
		 for(int i=0; i<dictionary.length; i++)
		 {   int count = 0;
			 for(int j=0; j<jetsForTrain.length; j++)
			 {
				  if (labels[j]==i)
					  {
					  dictionary[i] = arrayMath.arraySum(dictionary[i], jetsForTrain[j]);
					  count++;
					  }	  
			 }
			 dictionary[i] = arrayMath.arrayDivision(dictionary[i], count );
			 jetUsed[i] = count;
		 }
		 
	 }
	 
	 public int predict(double[]jet) 
	 {   //calculate the distance from the input jet to each reference jet in the dictionary,
	     //test if the closest distance is in a reasonable range(if not output -1), and output the class of the closest reference jet.
		 double [] result = new double [dictionary.length];
		 for(int i=0; i<dictionary.length; i++)
		 {
			 result[i] =0;
			 for(int j=0; j<jet.length; j++) 
			 {
				 result[i] = result[i]+Math.pow(jet[j]-dictionary[i][j], 2);
			 }
				 
		 }
		 double [] possibleClass = arrayMath.arrayMinValueAndIndex(result);
		 if(Math.sqrt(possibleClass[0])<=threshold)
			 {   int index = (int)possibleClass[1];
				 double [] temp = arrayMath.arrayTimes(dictionary[index], jetUsed[index]);
				 double [] temp2 = arrayMath.arraySum(temp,jet);
				 jetUsed[index]=jetUsed[index]+1;
				 dictionary[index] = arrayMath.arrayDivision(temp2, jetUsed[index]);
			 	return (int)possibleClass[1];
			 }
		 else
		 {
			 double[][] temp = new double[dictionary.length + 1][];
			 for (int i = 0; i < dictionary.length; i++)
			 {
			 	temp[i] = dictionary[i];
			 }
			 temp[dictionary.length]=jet;
		 	return -1;
		 }
	 }
	 
	 public double evaluate(double[][]jets, int[]labels) //evaluate the classifier's accuracy on the testing jets
	 {   
		 int count = 0;
		 int [] countEachLabel = new int [dictionary.length] ;
		 int [] countEachLabelTrue = new int [dictionary.length] ;
		 double [][] countEachLabelFalse = new double [dictionary.length][dictionary.length] ;
		 int [] results = new int [jets.length];
		 
		 for(int i=0; i<jets.length; i++)
		 {   int pre = predict(jets[i]);
		     results[i] = pre;
		     countEachLabel[labels[i]]= countEachLabel[labels[i]]+1;
			 if(pre == labels[i])
			 {
				 countEachLabelTrue[labels[i]]= countEachLabelTrue[labels[i]]+1;
				 count++;
			 }else {
				 countEachLabelFalse[labels[i]][(int)pre]= countEachLabelFalse[labels[i]][(int)pre]+1;
			 }	 
		 }
		
		 for(int j=0; j<countEachLabel.length; j++)
		 {
			 countEachLabelFalse[j][j] = 0-countEachLabel[j];
			 double [] temp = arrayMath.arrayDivision(countEachLabelFalse[j], countEachLabel[j]);
			 System.out.println("Accuracy of class "+ j +": "+ (countEachLabelTrue[j]/(double)countEachLabel[j]) +";  "+"Error distribution: "+Arrays.toString(temp));
		 }
		 
		 System.out.println("Predictions on testing jets:"+ Arrays.toString(results));//print the prediction results on the testing jets
		 System.out.println("True labels of testing jets:" + Arrays.toString(labels));//print true testing labels
			
		 return count/(double)jets.length; 
	 }

}





