import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JetBuilder {
	
	public double[] buildJet(String name)//build a jet for the input image with full dimension (number of pixels + 2)
	{   
		BufferedImage img = null;
		
	    try{ 
	    	img = ImageIO.read(new File (name));
	    	} 
	    catch (IOException e) {
	    	e.printStackTrace();
	    	}
	    
	    double []colourJet = transToArray(img);
		return colourJet;
	}
	
	
	private double[] transToArray(BufferedImage bufferedImage) {
		
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		double[] image = new double [height*width+2];
		
		//Save the RGB characteristics into the array
		for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
		Color color = new Color(bufferedImage.getRGB(j, i));
		image[i*height+j] = color.getRed()+color.getGreen()+color.getBlue();
		}
		}
		
		//Save the edges information into the array
		double[][] horizontalFilter = {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}};
		double[][] verticalFilter = {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
		
		EdgesDetector edH = new EdgesDetector();
		EdgesDetector edV = new EdgesDetector();
		
    	try{ edH.detectEdges(bufferedImage, horizontalFilter, true); 
    	     edV.detectEdges(bufferedImage, verticalFilter, false);
    	     }
    	catch (IOException e) { e.printStackTrace(); } 
   
		image[height*width] = edH.horizontalCount;
		image[height*width+1] = edV.verticalCount;
		
		return image;
		}

}

