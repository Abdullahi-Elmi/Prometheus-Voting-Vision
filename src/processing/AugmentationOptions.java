package processing;

public class AugmentationOptions {
	public boolean toGrayscale;
	public float scale;
	public boolean flip;
	
	public AugmentationOptions(boolean flip, boolean toGrayscale, float scale) {
		this.flip = flip;
		this.toGrayscale = toGrayscale;
		this.scale = scale;
	}
}
