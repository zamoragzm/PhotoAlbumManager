package photoalbum.utility;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Based on Thumbnail.java by Marco Schmidt
 * (http://schmidt.devlib.org/java/save-jpeg-thumbnail.html)
 * There appear to be no licensing restrictions on this code.
 */

// Represents a thumbnail constructed from a larger image
public class Thumbnail {

	private static final int MAX_WIDTH = 120;
	private static final int MAX_HEIGHT = 80;

    private int width;
    private int height;
    private BufferedImage thumbnailImage;

    // REQUIRES: fullImage != null
	// EFFECTS: constructs thumbnail image from given image,
    //          to fit within rectangle of size MAX_WIDTH X MAX_HEIGHT
	public Thumbnail(Image fullImage) {
		width = MAX_WIDTH;
		height = MAX_HEIGHT;
		computeThumbnail(fullImage);
	}

	public Image getThumbnailImage() {
		return thumbnailImage;
	}

	// REQUIRES: fullImage != null
    // MODIFIES: this
    // EFFECTS:  computes thumbnail from given image, maintaining aspect ratio of given image,
    //           scaled to fit within rectangle of size MAX_WIDTH X MAX_HEIGHT
	private void computeThumbnail(Image fullImage) {
		int thumbWidthToUse = width;
		int thumbHeightToUse = height;

		// Calculate the desired ratio of the thumbnail
		double thumbRatio = (double) width / (double) height;

		// Calculate the ratio of the image
		double imageRatio = (double) fullImage.getWidth(null)
				/ (double) fullImage.getHeight(null);

		if (thumbRatio < imageRatio) {
			thumbHeightToUse = (int) (width / imageRatio);
		} else {
			thumbWidthToUse = (int) (height * imageRatio);
		}

		// Draw the full image to the thumbnail image, scaling it on the fly
		thumbnailImage = new BufferedImage(thumbWidthToUse, thumbHeightToUse,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbnailImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(fullImage, 0, 0, thumbWidthToUse,
				thumbHeightToUse, null);
	}
}
