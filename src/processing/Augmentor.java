package processing;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;


public class Augmentor {
	/**
	 * Class that provides some simple augmentation functions for images
	 */
	
	public static BufferedImage deColorized(BufferedImage input) {
		/* 
		 * returns a decolorized version of the input image
		 */
		
		BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = result.getGraphics();
		g.drawImage(input, 0, 0, null);
		g.dispose();
		return result;		
	}
	
	
	public static BufferedImage flip(BufferedImage input) {
		/* 
		 * returns a flipped version of the input image
		 */
		
		BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), 1);
		result.setData(input.getData());
		
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-input.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		result = op.filter(input, null);
				
		return result;
	}
	
	public static BufferedImage resize(BufferedImage input, float scale) {
		/**
		 *  returns a scaled version of the input image
		 */
		
		int width = input.getWidth();
		int height = input.getHeight();
		
		int newWidth = new Double(width / scale).intValue();
		int newHeight = new Double(height / scale).intValue();
		
		BufferedImage resized = new BufferedImage(newWidth, newHeight, input.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(input, 0, 0, newWidth, newHeight, 0, 0, input.getWidth(),
		    input.getHeight(), null);
		g.dispose();
		
		return resized;
	}
	
	public static BufferedImage resize(int[] pixels, float scale, int width, int height) {
		BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for(int x = 0; x < height; x++) {
		    for(int y = 0; y < width; y++) {
		        b.setRGB(y, x, pixels[x * width + y]);
		    }
		}
		return resize(b, scale);
	}
	
	
}
