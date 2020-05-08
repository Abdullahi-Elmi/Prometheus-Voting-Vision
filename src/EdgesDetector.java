import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.lang.Object;
import java.lang.Thread;
//reference: K.Ramo, "Implement Simple Convolution with Java", Sept.27.2018
//Not a copy of the original code. Modified and added additional functionalities for this project.
public class EdgesDetector {
	
	public int verticalCount;//reflects the number and thickness of vertical edges when using vertical filter
	public int horizontalCount;//reflects the number and thickness of horizontal edges when using horizontal filter
	
    public EdgesDetector() {
    	verticalCount=0;
    	horizontalCount = 0;
    	}
    
	public void detectEdges(BufferedImage bufferedImage, double[][] filter, boolean ifH) throws IOException {
		double[][][] image = transformImageToArray(bufferedImage);
		if(ifH)
		{
			double[][] convolvedPixels = applyConvolutionH(bufferedImage.getWidth(),bufferedImage.getHeight(), image, filter);
			//return createImageFromConvolutionMatrix(bufferedImage, convolvedPixels); //uncomment this line when need to show horizontal edge detection result as a picture
		}
		else
		{
			double[][] convolvedPixels = applyConvolutionV(bufferedImage.getWidth(),bufferedImage.getHeight(), image, filter);
			//return createImageFromConvolutionMatrix(bufferedImage, convolvedPixels); //uncomment this line when need to show vertical edge detection result as a picture
		}
		
		}
	
	private double[][][] transformImageToArray(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		double[][][] image = new double[3][height][width];
		for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
		Color color = new Color(bufferedImage.getRGB(j, i));
		image[0][i][j] = color.getRed();
		image[1][i][j] = color.getGreen();
		image[2][i][j] = color.getBlue();
		}
		}
		return image;
		}
	
	private double[][] applyConvolutionV(int width, int height, double[][][] image, double[][] filter) {
		Convolution convolution = new Convolution();
		
		double[][] redConv = convolution.convolutionType2(image[0], height, width, filter, 3, 3, 1);
		double[][] greenConv = convolution.convolutionType2(image[1], height, width, filter, 3, 3, 1);
		double[][] blueConv = convolution.convolutionType2(image[2], height, width, filter, 3, 3, 1);
		
		double[][] finalConv = new double[redConv.length][redConv[0].length];
		
		for (int i = 0; i < redConv.length; i++) {
		for (int j = 0; j < redConv[i].length; j++) {
		finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
		
		if((i>1)&&(j>1)&&(i<redConv.length-1)&&(j<redConv[i].length-1)&&(finalConv[i][j]!=0)&&(finalConv[i-1][j-1]==0)&&(finalConv[i-1][j]==0)&&(finalConv[i-1][j+1]==0))
		{
			verticalCount++;
		}
		
		}
		}
		
		return finalConv;
		}
	
	private double[][] applyConvolutionH(int width, int height, double[][][] image, double[][] filter) {
		Convolution convolution = new Convolution();
		
		double[][] redConv = convolution.convolutionType2(image[0], height, width, filter, 3, 3, 1);
		double[][] greenConv = convolution.convolutionType2(image[1], height, width, filter, 3, 3, 1);
		double[][] blueConv = convolution.convolutionType2(image[2], height, width, filter, 3, 3, 1);
		
		double[][] finalConv = new double[redConv.length][redConv[0].length];
		
		for (int j = 0; j < redConv[0].length; j++) {
		for (int i = 0; i < redConv.length; i++) {
		finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
		
		if((i>1)&&(j>1)&&(i<redConv.length-1)&&(j<redConv[i].length-1)&&(finalConv[i][j]!=0)&&(finalConv[i][j-1]==0)&&(finalConv[i-1][j-1]==0)&&(finalConv[i+1][j-1]==0))
		{
			horizontalCount++;
		}
		
		}
		}
		
		return finalConv;
		}
	
	private File createImageFromConvolutionMatrix(BufferedImage originalImage, double[][] imageRGB) throws IOException {
		BufferedImage writeBackImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < imageRGB.length; i++) {
		for (int j = 0; j < imageRGB[i].length; j++) {
		Color color = new Color(fixOutOfRangeRGBValues(imageRGB[i][j]),
		fixOutOfRangeRGBValues(imageRGB[i][j]),
		fixOutOfRangeRGBValues(imageRGB[i][j]));
		writeBackImage.setRGB(j, i, color.getRGB());
		}
		}
		File outputFile = new File("image/EdgesDetected.png");
		ImageIO.write(writeBackImage, "png", outputFile);
		return outputFile;
		}
	
	private int fixOutOfRangeRGBValues(double value) {
		if (value < 0.0) {
		value = -value;
		}
		if (value > 255) {
		return 255;
		} else {
		return (int) value;
		}
		}

}

