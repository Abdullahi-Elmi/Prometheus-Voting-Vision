
public class arrayMath {//helper class to define some array math operations for this project 
	
	public static double[] arraySum(double[]a, double[]b) 
	 {
		 //find the sum of two array with same length by adding the array elements at corresponding location.
	 
		 if (a.length!=b.length)
		 {return null;}
		 
		 double [] c = new double [a.length];
		 for(int i=0; i<a.length; i++)
		 {
			 c[i]=a[i]+b[i];
		 }
		 return c; 
		 
	 }
	
	public static double[] arrayMinus(double[]a, double[]b) 
	 {
		 //find the sum of two array with same length by adding the array elements at corresponding location.
	 
		 if (a.length!=b.length)
		 {return null;}
		 
		 double [] c = new double [a.length];
		 for(int i=0; i<a.length; i++)
		 {
			 c[i]=a[i]-b[i];
		 }
		 return c; 
		 
	 }

	public static double[] arrayDivision(double [] a, double b) 
	 {
		 //divide each element in array a by double b.
		 
		 double [] c = new double [a.length];
		 for(int i=0; i<a.length; i++)
		 {
			 c[i]=a[i]/b;
		 }
		 return c; 	 
	 }

	public static double[] arrayTimes(double[] a, double b)
	{
		//Multiply each element in array a with double b.

		double [] c = new double [a.length];
		for(int i=0; i<a.length; i++)
		{
			c[i]=a[i]*b;
		}
		return c;
	}
	 public static double[] arrayMinValueAndIndex(double[]a)
	 {
		 //get the min value and the location of the max value in an array
		 double [] min = {a[0],0}; 
		 for(int i=1;i < a.length;i++)
		 { 
			 if(a[i] < min[0])
			 { 
				 min[0] = a[i];
				 min[1] = i; 
		      } 
		 }
		 return min;
	 }
	 public static double arrayMinValue(double[]a)
	 {
		 //get the min value and the location of the max value in an array
		 double min = a[0]; 
		 for(int i=1;i < a.length;i++)
		 { 
			 if(a[i] < min)
			 { 
				 min = a[i]; 
		      } 
		 }
		 return min;
	 }
	 public static double arrayMaxValue(double[]a)
	 {
		 //get the min value and the location of the max value in an array
		 double max = a[0]; 
		 for(int i=1;i < a.length;i++)
		 { 
			 if(a[i] > max)
			 { 
				 max = a[i]; 
		      } 
		 }
		 return max;
	 }
}
