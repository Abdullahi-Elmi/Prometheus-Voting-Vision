package processing;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import be.abeel.io.Base64.InputStream;
// creates error, doesn't seem to be used

public class Convolution {
	
	private final static float[] vertical = {1, 0, -1, 1, 0, -1, 1, 0, -1};
	private final static float[] sobel_vertical = {1, 0, -1, 2, 0, -2, 1, 0, -1};
	private final static float[] scharr_vertical = {3, 0, -3, 10, 0, -10, 3, 0, -3};
	private final static float[] horizontal= {1, 1, 1, 0, 0, 0, -1, -1, -1};
	private final static float[] sobel_horizontal = {1, 2, 1, 0, 0, 0, -1, -2, -1};
	private final static float[] scharr_horizontal = {3, 10, 3, 0, 0, 0, -3, -10, -3};
	private final static float[] sharpen = {0, -1, 0, -1, 5, -1, 0, -1, 0};
	private final static float[] gaussian_blur = {(float) (1/16.0), (float) (2/16.0), (float) (1/16.0), (float) (2/16.0), (float) (4/16.0), (float) (2/16.0), (float) (1/16.0), (float) (2/16.0), (float) (1/16.0)};
	
	private static float[] box_blur() {
		float[] blur_vector = new float[9];
		for (int i = 0 ; i < blur_vector.length ; i++)
			blur_vector[i] = (float) (1.0 / 9);
		return blur_vector;
	}
	
	public final static Kernel VERTICAL = new Kernel(3, 3, vertical);
	public final static Kernel SOBEL_VERTICAL = new Kernel(3, 3, sobel_vertical);
	public final static Kernel SCHARR_VERTICAL = new Kernel(3, 3, scharr_vertical);
	public final static Kernel HORIZONTAL = new Kernel(3, 3, horizontal);
	public final static Kernel SOBEL_HORIZONTAL = new Kernel(3, 3, sobel_horizontal);
	public final static Kernel SCHARR_HORIZONTAL = new Kernel(3, 3, scharr_horizontal);
	public final static Kernel SHARPEN = new Kernel(3, 3, sharpen);
	public final static Kernel BOX_BLUR = new Kernel(3, 3, box_blur());
	public final static Kernel GAUSSIAN_BLUR = new Kernel(3, 3, gaussian_blur);

	
	public static int[] simpleConvolution(BufferedImage input, Kernel k) {
		int height = input.getHeight();
		int width = input.getWidth();
		int[] inputArray = new int[height * width];
		if (input.getType() == BufferedImage.TYPE_INT_ARGB || input.getType() == BufferedImage.TYPE_INT_RGB) 
			input.getRaster().getDataElements(0, 0, width, height, inputArray);
		else
			input.getRGB(0, 0, width, height, inputArray, 0, width);
		return simpleConvolution(inputArray, k, width, height, true);
	}
	
	
	public static int[] simpleConvolution(int[] input, Kernel k, 
			int width, int height, boolean alpha) {
		int counter = 0;
		int row_count = k.getHeight();
		int col_count = k.getWidth();
		float[] kernelArray = k.getKernelData(null);
		int half_row = row_count / 2;
		int half_col = col_count / 2;
		
		int[] result = new int[width * height];
		
		for (int i = 0 ; i < height ; i++) {
			for (int j = 0 ; j < width ; j++) {
				// Initialize RGBA values for current pixel
				float valR = 0;
				float valG = 0;
				float valB = 0;
				float valA = 0;
				
				// Go through all the rows in the kernel
				for (int curr_row = -half_row ; curr_row <= half_row ; curr_row++) {
					int height_offset = i * width;
					int row_offset = col_count * (curr_row + half_row) + half_col;
					// Go through all the columns in the kernel
					for (int curr_col = -half_col; curr_col <= half_col ; curr_col++) {
						float curr_kernel_val = kernelArray[row_offset + curr_col];
						if (curr_kernel_val != 0) {
							int width_offset = j + curr_col;
							if (!(width_offset >= 0 && width_offset < width)) {
								width_offset = j;
							}
							int curr_rgb_val = input[height_offset + width_offset];
							valR += curr_kernel_val * ((curr_rgb_val >> 16) & 0xff);
							valG += curr_kernel_val * ((curr_rgb_val >> 8) & 0xff);
							valB += curr_kernel_val * (curr_rgb_val & 0xff);
							valA += curr_kernel_val * ((curr_rgb_val >> 24) & 0xff);
						}
					}
				}
				// populate result with result of calculation
				int finalR = clamp((int)(valR + 0.5));
				int finalG = clamp((int)(valG + 0.5));
				int finalB = clamp((int)(valB + 0.5));
				int finalA = alpha ? clamp((int)(valA+0.5)) : 0xff;
				result[counter++] = (finalA << 24) | (finalR << 16) | (finalG << 8) | finalB;				
			}
		}
		
		return result;
	}
	
	
	private static int clamp(int input) {
		return (input < 0) ? 0 : (input > 255) ? 255 : input; 
	}
	
	public static void main(String[] args) {
		
		BufferedImage img = null;

		try 
		{
		    img = ImageIO.read(new File("dataDemo\\20191107_105143.jpg")); // eventually C:\\ImageTest\\pic2.jpg
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
		ImageVisualizer.show(simpleConvolution(img, SOBEL_VERTICAL), img.getWidth(), img.getHeight());
		
	}
	
}

