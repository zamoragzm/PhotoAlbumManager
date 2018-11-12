package photoalbum.photo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Represents a tag having a name and collection of photos that are tagged with that tag
public class Tag {
	
	private String name;
	private Set<Photo> photos;

    // EFFECTS: constructs a tag with the given name having no associated photos
    // NOTE:    should be called only by TagManager - clients should use TagManager.createTag(name)
    Tag(String name) {
        setName(name);
        photos = new HashSet<Photo>();
    }

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

    // MODIFIES: this
    // EFFECTS:  adds photo to this tag, if photo does not already have it
    void addToPhoto(Photo photo){
		if(!photos.contains(photo)){
			photos.add(photo);
			photo.addTag(this);
		}
	}
	
	// MODIFIES: this
    // EFFECTS:  remove photo from this tag's photos
    void removeFromPhoto(Photo photo) {
		if(photos.contains(photo)){
			photos.remove(photo);
			photo.removeTag(this);
		}
	}

    // EFFECTS: returns an unmodifiable set of the photos tagged with this tag
	public Set<Photo> getPhotos(){
		return Collections.unmodifiableSet(photos);
	}

	@Override
	public String toString(){
		return name;
	}
}
