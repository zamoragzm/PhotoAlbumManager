package photoalbum.photo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Represents an album having a set of photos.
public class Album {

	private Set<Photo> photos;
	private String name;

	// EFFECTS: constructs an album with the given name, having no photos
	public Album(String name) {
		  this.name =  name;
        photos = new HashSet<Photo>();
	}

	public String getName() {
		return name;
	}

	// MODIFIES: this
    // EFFECTS: adds photo to album, if album does not already contain it
	public void addPhoto(Photo photo) {
		if (!photos.contains(photo)) {
			photos.add(photo);
			photo.setAlbum(this);
		}
	}

	// EFFECTS: returns an unmodifiable set of photos in this album
	public Set<Photo> getPhotos() {
		return Collections.unmodifiableSet(photos);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        return !(name != null ? !name.equals(album.name) : album.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
	public String toString() {
		return name;
	}

}
