package photoalbum.test;

import photoalbum.photo.Album;
import photoalbum.photo.Photo;
import photoalbum.photo.PhotoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basic operations of PhotoManager
 */

public class PhotoManagerTest {
	
	private PhotoManager aPhotoManager;
	private Album album1, album1a, album2;
	private Photo photo1, photo2, photo3, photo4, photo5;

	@BeforeEach
	public void setUp() {
		aPhotoManager = new PhotoManager();
		album1 = new Album("One");
		album1a = new Album("One");
		album2 = new Album("Two");
		photo1 = new Photo("1");
		photo2 = new Photo("2");
		photo3 = new Photo("3");
		photo4 = new Photo("4");
		photo5 = new Photo("5");
		
		album1.addPhoto(photo1);
		album1.addPhoto(photo2);
		album2.addPhoto(photo3);
	}

	@Test
	public void testAddAlbum() {
		aPhotoManager.addAlbum(album1);
		assertEquals(1, aPhotoManager.getAlbums().size());
		assertNotNull(aPhotoManager.findAlbum("One"));
	}
	
	@Test
	public void testTwoAlbumAddition() {
		aPhotoManager.addAlbum(album1);
		aPhotoManager.addAlbum(album2);
		assertEquals(2, aPhotoManager.getAlbums().size());
		assertNotNull(aPhotoManager.findAlbum("One"));
		assertNotNull(aPhotoManager.findAlbum("Two"));
	}

    @Test
    public void testFindAlbumDoesntExist() {
        Album notFound = aPhotoManager.findAlbum("NoSuchAlbum");
        assertNull(notFound);
    }

	@Test
	public void testAddingDuplicateAlbums() {
		aPhotoManager.addAlbum(album1);
		aPhotoManager.addAlbum(album1);
		assertEquals(1, aPhotoManager.getAlbums().size());
		
		aPhotoManager.addAlbum(album1a);
		assertEquals(1, aPhotoManager.getAlbums().size());	
	}
	
	@Test
	public void testGetPhotos() {
		Set<Photo> allPhotos = album1.getPhotos();
		assertTrue(allPhotos.contains(photo1));
		assertTrue(allPhotos.contains(photo2));
		assertFalse(allPhotos.contains(photo3));
		
		allPhotos = album2.getPhotos();
		assertTrue(allPhotos.contains(photo3));
		assertFalse(allPhotos.contains(photo1));
		assertFalse(allPhotos.contains(photo2));
	}
	
	@Test
	public void testGetPhotosInDateRange()  {
		aPhotoManager.addAlbum(album1);
		aPhotoManager.addAlbum(album2);
		photo1.setDateCreated(new Date(100000));
		photo2.setDateCreated(new Date(200000));
		photo3.setDateCreated(new Date(300000));
		photo4.setDateCreated(new Date(500000));
		photo5.setDateCreated(new Date(600000));
		
		album2.addPhoto(photo4);
		album2.addPhoto(photo5);
		
		Set<Photo> expected = new HashSet<Photo>();
		expected.add(photo2);
		expected.add(photo3);
		expected.add(photo4);
		assertEquals(expected, aPhotoManager
				.findPhotosInDateRange(new Date(200000), new Date(500000)));
	}
	
	@Test
	public void testNoPhotosInDateRange()  {
		aPhotoManager.addAlbum(album1);
		aPhotoManager.addAlbum(album2);
		photo1.setDateCreated(new Date(100000));
		photo2.setDateCreated(new Date(200000));
		photo3.setDateCreated(new Date(300000));
		photo4.setDateCreated(new Date(500000));
		photo5.setDateCreated(new Date(600000));
		
		album2.addPhoto(photo4);
		album2.addPhoto(photo5);
		
		Set<Photo> expected = new HashSet<Photo>();
		assertEquals(expected, aPhotoManager
				.findPhotosInDateRange(new Date(600001), new Date(700000)));
	
	}
}
	
