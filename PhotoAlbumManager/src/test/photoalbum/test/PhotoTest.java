package photoalbum.test;

import photoalbum.photo.Photo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic tests for photo that do not involve tags, image loading or display.
 */
public class PhotoTest {
	
	private Photo photo;
	private Date photo1AddedDate;

	@BeforeEach
	public void setUp() {
		photo = new Photo("1");
		photo1AddedDate = new Date();
		photo.setDateCreated(photo1AddedDate);
		photo.setDescription("description");
	}
	
	@Test
	public void testName() {
		assertEquals("1", photo.getName());
	}
	
	@Test
	public void testDate() {
		assertEquals(photo1AddedDate, photo.getDateCreated());
	}
	
	@Test
	public void testDescription() {
		assertEquals("description", photo.getDescription());
	}

    @Test
    public void testLoadNoFile() {
        assertFalse(photo.loadPhoto(new File("./data/NoFile.jpg")));
    }

    @Test
    public void testLoadNonImageFile() {
        assertFalse(photo.loadPhoto(new File("./data/Text.txt")));
    }

    @Test
    public void testLoadTooBigWiderThanTall() {
        assertTrue(photo.loadPhoto(new File("./data/TooBigWiderThanTall.png")));
        Image img = photo.getImage();
        assertEquals(Photo.MAX_WIDTH, img.getWidth(null));
        assertTrue(img.getHeight(null) <= Photo.MAX_HEIGHT);
    }

    @Test
    public void testLoadTooBigTallerThanWide() {
        assertTrue(photo.loadPhoto(new File("./data/TooBigTallerThanWide.png")));
        Image img = photo.getImage();
        assertEquals(Photo.MAX_HEIGHT, img.getHeight(null));
        assertTrue(img.getWidth(null) <= Photo.MAX_WIDTH);
    }

    @Test
    public void testLoadTooWideOnly() {
        assertTrue(photo.loadPhoto(new File("./data/TooWide.png")));
        Image img = photo.getImage();
        assertEquals(Photo.MAX_WIDTH, img.getWidth(null));
        assertTrue(img.getHeight(null) <= Photo.MAX_HEIGHT);
    }

    @Test
    public void testLoadTooTallOnly() {
        assertTrue(photo.loadPhoto(new File("./data/TooTall.png")));
        Image img = photo.getImage();
        assertEquals(Photo.MAX_HEIGHT, img.getHeight(null));
        assertTrue(img.getWidth(null) <= Photo.MAX_WIDTH);
    }

    @Test
    public void testFlipHorizontal() {
        Photo original = new Photo("test");
        Photo horizontalFlip = new Photo("horizontal flip");
        assertTrue(original.loadPhoto(new File("./data/TestImage.png")));
        assertTrue(horizontalFlip.loadPhoto(new File("./data/TestImageFlipHorizontal.png")));
        original.flipHorizontal();
        checkSamePixels(original, horizontalFlip);
    }

    @Test
    public void testToBlackAndWhite() {
        Photo original = new Photo("test");
        Photo blackAndWhite = new Photo("b & w");
        assertTrue(original.loadPhoto(new File("./data/TestImage.png")));
        assertTrue(blackAndWhite.loadPhoto(new File("./data/TestImageBW.png")));
        original.toBlackAndWhite();
        checkSamePixels(original, blackAndWhite);
    }

    @Test
    public void testBlur() {
        Photo original = new Photo("test");
        Photo blur = new Photo("blur");
        assertTrue(original.loadPhoto(new File("./data/TestImage.png")));
        assertTrue(blur.loadPhoto(new File("./data/TestImageBlur.png")));
        original.blur();
        checkSamePixels(original, blur);
    }

    private void checkSamePixels(Photo p1, Photo p2) {
        BufferedImage img1 = p1.getImage();
        BufferedImage img2 = p2.getImage();

        assertEquals(img1.getWidth(), img2.getWidth());
        assertEquals(img1.getHeight(), img2.getHeight());

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                assertEquals(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
    }
}
