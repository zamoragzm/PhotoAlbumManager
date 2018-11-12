package photoalbum.photo;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


// Manages a collection of tags and enforces that only a single tag with a given name exists in the system
public class TagManager {

	private Map<String, Tag> tags;

    // EFFECTS: constructs a tag manager with an empty collection of tags
    public TagManager() {
        tags = new HashMap<String, Tag>();
    }

	// EFFECTS: returns tag with the given name or null if no such tag exists
	public Tag findTag(String name){
		return tags.get(name);
	}
	
	// REQUIRES: name != null
    // MODIFIES: this
    // EFFECTS:  if a tag with the given name exists, tag is returned, otherwise
    //           creates a new tag with the given name and returns it
	public Tag createTag(String name)  {
		if(tags.containsKey(name))
			return tags.get(name);
		
		Tag tag = new Tag(name);
		tags.put(name, tag);
		return tag;
	}

    // REQUIRES: newName != null
    // MODIFIES: this
    // EFFECTS:  if oldName is the same as newName, returns false;
    //           if no tag with oldName exists, returns false;
    //           if tag with newName already exists, returns false;
    //           otherwise, renames tag with oldName to newName and returns true;
	public boolean renameTag(String oldName, String newName) {

        // WARNING: this relies on the fact that Tag's
        // .equals, .hashCode, etc. do NOT use the tag name;
        // if they did, changing the name would mess up
        // existing collections of tags.
		
		if(oldName.equals(newName))
            return false;
		
		Tag tag = tags.get(oldName);
		if(tag == null)
            return false;
		
		if(tags.containsKey(newName))
			return false;
		
		tags.remove(oldName);
		tag.setName(newName);
		tags.put(newName, tag);
		return true;
	}
	
	// EFFECTS: returns an unmodifiable set of all the tags in this collection
	public Set<Tag> getTags(){
		Set<Tag> tagSet = new HashSet<Tag>(tags.values());

		return Collections.unmodifiableSet(tagSet);
	}

    // MODIFIES: this
    // EFFECTS:  removes tag with given name from collection and returns true;
    //           returns false if no such tag is found
	public boolean removeTag(String name){
		Tag tag = tags.get(name);
		if(tag == null) return false;

        Set<Photo> copyOfSet = new HashSet<Photo>(tag.getPhotos());

        for (Photo photo : copyOfSet)
			photo.removeTag(tag);

		tags.remove(name);
		return true;
	}
	
}
