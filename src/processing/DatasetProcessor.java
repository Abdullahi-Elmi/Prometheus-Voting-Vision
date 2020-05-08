package processing;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import processing.Convolution;

import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 

import processing.DataPoint;


public class DatasetProcessor implements Iterable<DataPoint> {
	
	/**
	 * Represents one set of data to train and test on 
	 */
	
	private final ArrayList<DataPoint> data = new ArrayList<>();
	private JSONObject json_data;
	
	public DatasetProcessor(String filename, AugmentationOptions opts) throws Exception {
		//Search for images in provided directory name
		File dir = new File(filename);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
		    for (File child : dirList) {
		    	
		    	// Check if we found an extension
		    	if (!getExtension(child.getName()).isPresent())
		    		continue;
		    	
		    	// Check if file is of type json
		    	if (getExtension(child.getName()).get().equals("json")) {
		    		String location = null;
		    		String object = null;
		    		try {
		    			// Get and parse json data 
			    		json_data = (JSONObject) new JSONParser().parse(new FileReader(child)); 
			    		location = (String) json_data.get("location");
			    		object = (String) json_data.get("object");
		    		}	catch (IOException e) {
		    			System.out.println("There was an issue reading the json file");
						e.printStackTrace();
		    		} catch (ParseException e) {
		    			System.out.println("There was an issue parsing the json file");
						e.printStackTrace();
					}
		    		
		    		
		    		// Get the image from the image path
		    		BufferedImage image = null;
		    		try {
		    			String imageFile = filename + child.getName().replace(".json", ".jpg");
		    			System.out.println("Loading image file: " + imageFile);
		    			image = ImageIO.read(new File(imageFile));
		    		}
		    		catch (IOException e) {
		    			System.out.println("There was an issue reading the image file");
		    			e.printStackTrace();
		    		}
		    		
		    		// Add data point and augmented versions if requested
		    		data.add(new DataPoint(image, location, object));
		    		if (opts.flip) {
		    			String newObject = object;
		    			if (newObject != null) {
		    				if (newObject.equals("left"))
		    					newObject = "right";
		    				else if(newObject.equals("right"))
		    					newObject = "left";
		    			}
	    				
	    				data.add(new DataPoint(Augmentor.flip(image), location, newObject));
		    		}

	    			// Add grayscale version
	    			if (opts.toGrayscale)
	    				data.add(new DataPoint(Augmentor.deColorized(image), location, object));
	
	    			// Add scaled version
	    			if (opts.scale > 0)
	    				data.add(new DataPoint(Augmentor.resize(image, opts.scale), location, object));
			    	}
		    }
		    if (data.size() == 0) {
		    	throw new Exception("No json files were found in your input file");
		    }
		} else {
			throw new Exception("There was an error opening the given location. Please check your paths");
		}
	}
	
	// Helper method
	private Optional<String> getExtension(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
	
	public void mergeRegions(int q) {
		for (DataPoint p : this.data) {
			SRMerger merger = new SRMerger(p.getIntImage(), p.getWidth(), p.getHeight(), 32);
			int[] im = merger.getSegmentedImage();
			p.setImage(DataPoint.toDouble(im));
		}
	}
	
	public void applyConvolution(Kernel k, boolean alpha) {
		for (DataPoint p : this.data) {
			p.setImage(DataPoint.toDouble(
					Convolution.simpleConvolution(p.getIntImage(), k, p.getWidth(), p.getHeight(), alpha)));
		}
	}
	
	public void scale(float factor) {
		for (DataPoint p : this.data) {
			p.setImage(DataPoint.getPixelData(
							Augmentor.resize(p.getIntImage().clone(), factor, p.getWidth(), p.getHeight())));
		}
	}
	
	public double[][] getNNInputs() {
		double[][] result = new double[this.getLength()][];
		int i = 0;
		for (DataPoint d : this.data) {
			result[i] = d.getImage();
			i++;
		}
		return result;
	}
	
	public double[][] getNNOutputs(boolean object) {
		double[][] result = new double[this.getLength()][];
		int i = 0;
		for (DataPoint d : this.data) {
			if (!object) {
				result[i] = new double[4];
				for (int j = 0 ; j < 4 ; j++) {
					if (d.getLocation().ordinal() == j) {
						result[i][j] = 1.0;
					}else {
						result[i][j] = 0.0;
					}
				}
			} else {
				result[i] = new double[3];
				for (int j = 0 ; j < 3 ; j++) {
					if (d.getObject().ordinal() == j) {
						result[i][j] = 1.0;
					}else {
						result[i][j] = 01.0;
					}
				}
			}
			i++;
		}
		return result;
	}
	
	public void normalize(double newMin, double newMax) {
		for (DataPoint d : this.data) {
			double[] im = d.getImage();
			double min = min(im);
			double max = max(im);
			double[] newIm = new double[im.length];
			for(int i = 0 ; i < im.length ; i++)
	        {
	            newIm[i] = (((im[i] - (double)min) / (double)(max - (double)min)) * (newMax - (double)newMin)) * newMax;
	        }
			d.setImage(newIm);

		}
	}
	
	private double min(double[] arr) {
		double min = arr[0];
		for (int i = 0 ; i < arr.length ; i++) {
			if (arr[i] < min)
				min = arr[i];
		}
		return min;
	}
	
	private double max(double[] arr) {
		double max = arr[0];
		for (int i = 0 ; i < arr.length ; i++) {
			if (arr[i] > max)
				max = arr[i];
		}
		return max;
	}
	
	public DataPoint get(int i) {
		return this.data.get(i);
	}
	
	@Override
	public Iterator<DataPoint> iterator() {
		return data.iterator();
	}
	
	public int getLength() {
		return this.data.size();
	}
	
	public void toCSV(String filename) throws FileNotFoundException {
		String output = this.toString(false);
		try (PrintWriter out = new PrintWriter(filename)) {
		    out.println(output);
		}
	}
	
	public String toString(boolean object) {
		String builder = "";
		for (DataPoint p : data) {
			builder += p.toString(object) + "\n";
		}
		return builder;
	}


}
