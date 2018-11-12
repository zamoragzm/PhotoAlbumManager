package photoalbum.utility;

import photoalbum.ui.MainFrame;
import photoalbum.ui.PopUps;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import photoalbum.photo.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

// File manager - handles interactions with file system
public class FileManager {
    private static final String PROJECT_DIRECTORY_PATH = System.getProperty("user.dir");
    private static final String PICTURES_DIRECTORY = "photos";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String PHOTO_FILE_TYPE = ".jpg";
    private static final String PATH_TO_PICS = PROJECT_DIRECTORY_PATH + FILE_SEPARATOR + PICTURES_DIRECTORY;

    private MainFrame mainFrame;
    private PhotoManager photoMan;
    private TagManager tagMan;

    // EFFECTS: constructs file manager associated with application's main frame
    public FileManager(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.photoMan = mainFrame.getPhotoManager();
        this.tagMan = mainFrame.getTagManager();
    }

    // MODIFIES: this
    // EFFECTS:  loads all JPG photos from all albums represented by sub-directories in PATH_TO_PICS directory
    public void loadLibrary() {
        try {
            loadAlbums();

            for (Album album : photoMan.getAlbums()) {
                loadPhotosForAlbum(album);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // EFFECTS:  writes description and keywords from all photos in all albums back to corresponding image files
    public void writeMetadataToFile() {
        for (Album album : photoMan.getAlbums()) {
            try {
                writeMetadataForAlbum(album);
            } catch (IOException e) {
                PopUps.errorPopup(mainFrame, "Couldn't write metadata for images in album: " + album.getName());
            }
        }
    }

    // MODIFIES: this, album
    // EFFECTS:  load all photos from given files to given album;
    //           shows error message if a particular file cannot be loaded or already exists in album
    public void loadFromFilesToAlbum(File[] files, Album album) {
        for (File file : files) {
            try {
                importPhotoFileToAlbumDirectory(file, album);
                addPhotoFromFileToAlbum(album, file);
            } catch (IOException e) {
                PopUps.errorPopup(mainFrame, "File could not be loaded: " + file.getName());
            } catch (DuplicateFileException e) {
                PopUps.errorPopup(mainFrame, "File already exists in album: " + file.getName());
            }
        }
    }

    // MODIFIES: this
    // EFFECTS:  load albums into library; each directory in PATH_TO_PICS is assumed to represent an album
    private void loadAlbums() throws IOException {
        Path dir = FileSystems.getDefault().getPath(PATH_TO_PICS);
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        for (Path entry : stream) {
            File f = entry.toFile();
            if (f.isDirectory()) {
                Album newAlbum = new Album(entry.getName(entry.getNameCount() - 1).toString());
                photoMan.addAlbum(newAlbum);
            }
        }
    }

    // MODIFIES: this, album
    // EFFECTS:  adds photos from all jpg files found in PATH_TO_PICS/album.getName() to album
    private void loadPhotosForAlbum(Album album) throws IOException {
        Path dir = FileSystems.getDefault().getPath(PATH_TO_PICS + FILE_SEPARATOR + album.getName());
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);

        for (Path entry : stream) {
            File f = entry.toFile();
            if (f.isFile() && isJPEG(f)) {
                addPhotoFromFileToAlbum(album, f);
            }
        }
    }

    // EFFECTS: returns true if file has a PHOTO_FILE_TYPE extension, false otherwise
    private boolean isJPEG(File file) {
        if (!file.isFile())
            return false;

        String fileName = file.getName();
        int indexOfDot = fileName.lastIndexOf('.');

        return indexOfDot >= 0 && fileName.substring(indexOfDot).equals(PHOTO_FILE_TYPE);
    }

    // MODIFIES: this, album
    // EFFECTS:  attempts to create photo from image found in given file;
    //           if successful, and adds photo to album;
    //           otherwise, silently returns
    private void addPhotoFromFileToAlbum(Album album, File file) {
        String fileName = file.getName();

        int indexOfDot = fileName.lastIndexOf('.');
        if (indexOfDot >= 0)
            fileName = fileName.substring(0, indexOfDot);

        Photo p = new Photo(fileName);
        if(p.loadPhoto(file)) {
            loadMetadata(file, p);
            album.addPhoto(p);
        }
    }

    // MODIFIES: this, photo
    // EFFECTS:  loads metadata (description, creation date and keywords) from given file and adds it to photo;
    //           sets description to "", creation date to current date/time and keywords to empty set, if
    //           metadata cannot be read
    private void loadMetadata(File f, Photo photo) {
        try {
            List<IptcRecord> records = JpgIptcRecordReader.readIptcRecords(f);
            photo.setDescription(findDescription(records));
            photo.setDateCreated(findCreationDate(records));
            addTags(records, photo);
        } catch (Exception e) {
            System.out.println("Could not load metadata for file: " + f.getName());
            photo.setDescription("");
            photo.setDateCreated(new Date());
        }
    }

    // EFFECTS: returns description from given list of IPTC records or
    //          empty string if corresponding record not found
    private String findDescription(List<IptcRecord> records) {
        for (IptcRecord record : records) {
            if (record.iptcType == IptcTypes.CAPTION_ABSTRACT)
                return record.getValue();
        }

        return "";
    }

    // EFFECTS: returns creation date from given list of IPTC records or
    //          current date/time if corresponding record not found
    private Date findCreationDate(List<IptcRecord> records) {
        for (IptcRecord record : records) {
            if (record.iptcType == IptcTypes.DATE_CREATED) {
                String dateString = record.getValue();
                int year = Integer.parseInt(dateString.substring(0, 4));
                int month = Integer.parseInt(dateString.substring(4, 6));
                int day = Integer.parseInt(dateString.substring(6));
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day);
                return calendar.getTime();
            }
        }

        return new Date();
    }

    // MODIFIES: this, photo
    // EFFECTS:  add tags (keywords) from given list of IPTC records to given photo
    private void addTags(List<IptcRecord> records, Photo photo) {
        for (IptcRecord record : records) {
            if (record.iptcType == IptcTypes.KEYWORDS) {
                Tag tag = tagMan.createTag(record.getValue());
                photo.addTag(tag);
            }
        }
    }

    // EFFECTS: write metadata for all photos found in given album back to corresponding files;
    //          displays error message if metadata cannot be written to a particular file
    private void writeMetadataForAlbum(Album album) throws IOException {
        Path dir = FileSystems.getDefault().getPath(PATH_TO_PICS + FILE_SEPARATOR + album.getName());
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);

        for (Path entry : stream) {
            File f = entry.toFile();
            if (f.isFile()) {
                String fileName = entry.getName(entry.getNameCount() - 1).toString();
                int indexOfDot = fileName.lastIndexOf('.');
                if (indexOfDot >= 0)
                    fileName = fileName.substring(0, indexOfDot);

                Photo photo = findPhotoNamed(album, fileName);

                if (photo != null) {
                    String description = photo.getDescription();
                    List<String> keywords = getKeywordsForPhoto(photo);
                    try {
                        JpgIptcRecordWriter.writeMetadata(f, description, keywords);
                    } catch (Exception e) {
                        PopUps.errorPopup(mainFrame, "Could not write metadata to file: " + fileName);
                    }
                }
            }
        }
    }

    // EFFECTS: returns photo in album with given name or null if no such photo found
    private Photo findPhotoNamed(Album album, String fileName) {
        Set<Photo> photos = album.getPhotos();
        for (Photo photo : photos) {
            if (photo.getName().equals(fileName))
                return photo;
        }

        return null;
    }

    // EFFECTS: returns list of keywords (tags) for given photo;
    //          returns empty list of photo is null
    private List<String> getKeywordsForPhoto(Photo photo) {
        List<String> keywords = new ArrayList<String>();

        if (photo != null)
            addKeywordsFromTags(photo, keywords);

        return keywords;
    }

    // MODIFIES: this, photo
    // EFFECTS:  adds all tags from given photo to given list of keywords
    private void addKeywordsFromTags(Photo photo, List<String> keywords) {
        for (Tag tag : photo.getTags()) {
            keywords.add(tag.getName());
        }
    }

    // EFFECTS:  copies given file to directory PATH_TO_PICS/album.getName()/;
    //           throws DuplicateFileException if file of given name already exists in target directory
    private void importPhotoFileToAlbumDirectory(File file, Album album) throws IOException, DuplicateFileException {
        File destination = new File("photos" + FILE_SEPARATOR + album.getName()
                + FILE_SEPARATOR + file.getName());

        // If the file isn't in the destination folder with the expected
        // filename, copy it there
        if (!file.getCanonicalPath().equals(destination.getCanonicalPath())) {
            if (!destination.exists()) {
                Path sourcePath = Paths.get(file.getCanonicalPath());
                Path destPath = Paths.get(destination.getCanonicalPath());
                Files.copy(sourcePath, destPath);
            }
            else {
                throw new DuplicateFileException(file.getName());
            }
        }
    }
}

