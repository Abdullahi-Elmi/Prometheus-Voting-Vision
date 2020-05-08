package processing;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


/**
 * Class to display an image (mostly for debugging purposes)
 * @author miloview
 *
 */
public class ImageVisualizer {

	public static void show(BufferedImage image) {
		JFrame frame = new JFrame();
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		frame.add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void show(int[] pixels, int width, int height) {
		
		BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for(int x = 0; x < height; x++) {
		    for(int y = 0; y < width; y++) {
		        b.setRGB(y, x, pixels[x * width + y]);
		    }
		}
		
		show(b);
		
	}

}
