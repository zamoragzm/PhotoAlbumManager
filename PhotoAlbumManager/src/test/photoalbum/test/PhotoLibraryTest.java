package photoalbum.test;

import photoalbum.photo.Photo;
import photoalbum.photo.Tag;
import photoalbum.photo.TagManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** 
 * Tests functionality that lies between classes in the photo library system.
 */

public class PhotoLibraryTest {

	private Photo photo1, photo2;
	private TagManager aTagManager;
	
	@BeforeEach
	public void setUp() {
		photo1 = new Photo("1");
		photo2 = new Photo("2");
		
		aTagManager = new TagManager();
		aTagManager.createTag("birthday");
		aTagManager.createTag("AuntBetty");
	}
	
	@Test
	public void testEmptyInitialTagsForPhoto() {
		assertTrue(photo1.getTags().isEmpty());
	}
	
	@Test
	public void testAddSingleTagToPhoto() {
		assertEquals(0, photo1.getTags().size());
		photo1.addTag(aTagManager.findTag("birthday"));
		assertEquals(1, photo1.getTags().size());
		Tag theTag = (Tag) photo1.getTags().toArray()[0];
		assertEquals("birthday", theTag.getName());
	}

    @Test
    public void testAddDuplicateTagToPhoto() {
        photo1.addTag(aTagManager.findTag("birthday"));
        photo1.addTag(aTagManager.findTag("birthday"));
        assertEquals(1, photo1.getTags().size());
        Tag theTag = (Tag) photo1.getTags().toArray()[0];
        assertEquals("birthday", theTag.getName());
    }
	
	@Test
	public void testAddMultipleTagsToPhoto() {
		assertEquals(0, photo1.getTags().size());
		photo1.addTag(aTagManager.findTag("birthday"));
		photo1.addTag(aTagManager.findTag("AuntBetty"));
		assertEquals(2, photo1.getTags().size());
		Set<Tag> tagsOnPhoto = photo1.getTags();
		Set<Tag> expectedTags = new HashSet<Tag>();
		expectedTags.add(aTagManager.findTag("birthday"));
		expectedTags.add(aTagManager.findTag("AuntBetty"));
		assertEquals(expectedTags, tagsOnPhoto);
	}
	
	@Test
	public void testRemoveTagFromPhoto() {
		assertEquals(0, photo1.getTags().size());
		photo1.addTag(aTagManager.findTag("birthday"));
		photo1.addTag(aTagManager.findTag("AuntBetty"));
		assertEquals(2, photo1.getTags().size());
		photo1.removeTag(aTagManager.findTag("birthday"));
		// Tag should be removed from photo
		assertEquals(1, photo1.getTags().size());
		Tag theTag = (Tag) photo1.getTags().toArray()[0];
		assertEquals("AuntBetty", theTag.getName());
		// Tag should still exist
		assertNotNull(aTagManager.findTag("birthday"));
	}
	
	@Test
	public void testRemoveTagNotOnPhoto() {
		assertEquals(0, photo1.getTags().size());
		photo1.addTag(aTagManager.findTag("birthday"));
		assertEquals(1, photo1.getTags().size());
		photo1.removeTag(aTagManager.findTag("AuntBetty"));
		// Existing tag on photo should not be altered
		assertEquals(1, photo1.getTags().size());
		Tag theTag = (Tag) photo1.getTags().toArray()[0];
		assertEquals("birthday", theTag.getName());
	}

    @Test
    public void testRemoveTagFromManager() {
        aTagManager.createTag("birthday");
        Tag birthdayTag = aTagManager.findTag("birthday");
        photo1.addTag(birthdayTag);
        photo2.addTag(birthdayTag);
        assertTrue(aTagManager.removeTag("birthday"));
        assertEquals(1, aTagManager.getTags().size());
        assertTrue(photo1.getTags().isEmpty());
        assertTrue(photo2.getTags().isEmpty());
    }
	
	@Test
	public void testCheckPhotosForTag() {
		photo1.addTag(aTagManager.findTag("birthday"));
		photo1.addTag(aTagManager.findTag("AuntBetty"));
		photo2.addTag(aTagManager.findTag("birthday"));
		Set<Photo> photosForTag = aTagManager.findTag("birthday").getPhotos();
		assertEquals(2, photosForTag.size());
		Set<Photo> expectedPhotos = new HashSet<Photo>();
		expectedPhotos.add(photo1);
		expectedPhotos.add(photo2);
		assertEquals(expectedPhotos, photosForTag);
	}
	
	@Test
	public void testRenameTagWithPhotos() {
		Tag birthdayTag = aTagManager.findTag("birthday");
		photo1.addTag(birthdayTag);
		aTagManager.renameTag("birthday", "newBirthday");
		assertNull(aTagManager.findTag("birthday"));
		assertNotNull(aTagManager.findTag("newBirthday"));
		assertEquals(birthdayTag, aTagManager.findTag("newBirthday"));
		Set<Tag> tagsForPhoto = photo1.getTags();
		assertEquals(1, tagsForPhoto.size());
		Tag theTag = (Tag) tagsForPhoto.toArray()[0];
		assertEquals("newBirthday", theTag.getName());
	}

}
