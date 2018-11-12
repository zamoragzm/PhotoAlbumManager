package photoalbum.photo;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// Manages a collection of albums (and therefore photos)
public class PhotoManager {
	
	private Set<Album> albums;

    // EFFECTS: constructs a photo manager having an empty collection of photos
    public PhotoManager() {
        albums = new HashSet<Album>();
    }

	// EFFECTS: returns an unmodifiable set of albums in this collection
	public Set<Album> getAlbums(){
		return Collections.unmodifiableSet(albums);
	}

	// MODIFIES: this
    // EFFECTS: adds an album to this collection, if it's not already there
	public void addAlbum(Album album){

	    albums.add(album);
	}

	// EFFECTS: returns album with the given name (album names are assumed to be unique);
    //          returns null if no album found with the given name
	public Album findAlbum(String name) {
		for (Album anAlbum: albums) {
            String albumName = anAlbum.getName();
            if (albumName.equals(name)) {
                return anAlbum;
            }
        }
		return null;
	}
	
	// EFFECTS: returns an unmodifiable set of all the photos in all the albums in this collection
    //          whose creation date is between start and end (inclusive)
	public Set<Photo> findPhotosInDateRange(Date start, Date end){
		Set<Photo> photos = new HashSet<Photo>();

		for(Photo photo: getPhotos()) {
            Date dateCreated = photo.getDateCreated();
			if(!dateCreated.before(start) && !dateCreated.after(end))
				photos.add(photo);
		}

		return Collections.unmodifiableSet(photos);
	}

    // EFFECTS: returns an unmodifiable set of all the photos in all the albums in this collection
    private Set<Photo> getPhotos(){
        Set<Photo> photos = new HashSet<Photo>();
        for(Album album : albums)
            photos.addAll(album.getPhotos());

        return Collections.unmodifiableSet(photos);
    }
}
