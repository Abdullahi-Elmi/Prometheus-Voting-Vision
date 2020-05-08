package processing;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DataPoint {
	/**
	 * Represents one image's information and it's label
	 */
	
	public static enum Locations {
		hall,
		room, 
		open,
		stair
	};
	
	public static enum Objects {
		mid,
		left,
		right
	};
	
	private Locations location;
	private Objects object;
	private double[] image;
	private int height;
	private int width;
	
	
	/**
	 * Constructor when object is unknown
	 * @param image
	 * @param location
	 */
	public DataPoint(BufferedImage image, String location) {
		this(image, location, null);
	}
	
	/**
	 * Constructor when all variables are known
	 * @param image
	 * @param location
	 * @param object
	 */
	public DataPoint(BufferedImage image, String location, String object) {
		this.image = getPixelData(image);
		this.location = location != null ? Locations.valueOf(location) : null;
		this.object = object != null ? Objects.valueOf(object) : null;
		this.height = image.getHeight();
		this.width = image.getWidth();
	}
	
	/**
	 * Get the pixel data as an array
	 * @return pixel data of the image
	 */
	public static double[] getPixelData(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int[] result = new int[width * height];

		result = img.getRGB(0, 0, width, height, result, 0, width);
		return toDouble(result);
	}
	
	public static double[] toDouble(int[] arr) {
		double[] result = new double[arr.length];
		for (int i = 0 ; i < arr.length ; i++) {
			result[i] = arr[i];
		}
		return result;
	}
	
	public static int[] toInt(double[] arr) {
		int[] result = new int[arr.length];
		for (int i = 0 ; i < arr.length ; i++) {
			result[i] = (int) arr[i];
		}
		return result;
	}
	
	public double[] getLocationOutput() {
		double[] result = new double[4];
		int counter = 0;
		for (DataPoint.Locations l : DataPoint.Locations.values()) {
			result[counter]= l.compareTo(this.location) == 0 ? 1.0 : 0.0;
		}
		return result;
	}
	
	public double[] getObjectOutput() {
		double[] result = new double[3];
		int counter = 0;
		for (DataPoint.Objects l : DataPoint.Objects.values()) {
			result[counter]= l.compareTo(this.object) == 0 ? 1.0 : 0.0;
		}
		return result;
	}
	
	// BEWARE, SOME INFORMATION MIGHT BE LOST HERE
	public int[] getIntImage() {
		return toInt(this.image);
	}
	
	public double[] getImage() {
		return this.image;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() { 
		return this.width;
	}
	
	public void setImage(double[] array) {
		this.image = array;
	}
	
	public Locations getLocation() {
		return this.location;
	}
	
	public Objects getObject() {
		return this.object;
	}
	
	public void setLocation(Locations location) {
		this.location = location;
	}
	
	public void setObject(Objects object) {
		this.object = object;
	}
	
	public static String[] getLocationNames() {
	    return Arrays.stream(Locations.values()).map(Enum::name).toArray(String[]::new);
	}
	
	public static String[] getObjectNames() {
	    return Arrays.stream(Objects.values()).map(Enum::name).toArray(String[]::new);
	}
	
	
	public String toString(boolean object) {
		String builder = "";
		for (int i = 0 ; i < this.image.length ; i++) {
			builder += image[i] + ", ";
		}
		if (!object) {
			for (int i = 0 ; i < 4 ; i++) {
				if (this.location.ordinal() == i) {
					builder += "1.0";
				}else {
					builder += "0.0";
				}
				if (i != 3){
					builder += ",";
				}
			}
		} else {
			for (int i = 0 ; i < 3 ; i++) {
				if (this.object.ordinal() == i) {
					builder += "1.0";
				}else {
					builder += "0.0";
				}
				if (i != 2){
					builder += ",";
				}
			}
		}
		return builder;
	}
	
}
