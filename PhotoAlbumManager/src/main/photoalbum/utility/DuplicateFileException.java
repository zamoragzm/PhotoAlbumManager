package photoalbum.utility;

// Exception thrown when attempt is made to add a file (photo) that already exists to an album
class DuplicateFileException extends Exception {

    DuplicateFileException() {
        super();
    }

    DuplicateFileException(String msg) {
        super(msg);
    }
}
