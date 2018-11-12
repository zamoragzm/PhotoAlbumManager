package photoalbum.utility;


import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;

import java.io.File;
import java.io.IOException;
import java.util.List;

// Reads IPTC records from jpeg files
class JpgIptcRecordReader {

    // EFFECTS: returns list of IPTC records read from given file's metadata;
    //          throws ImageReadException if jpg metadata cannot be read from file;
    //          throws IOException if file cannot be read
    static List<IptcRecord> readIptcRecords(File file) throws IOException, ImageReadException {
        final ImageMetadata metadata = Imaging.getMetadata(file);

        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            JpegPhotoshopMetadata photoshopMetadata = jpegMetadata.getPhotoshop();
            return photoshopMetadata.photoshopApp13Data.getRecords();
        }

        throw new ImageReadException("Could not read metadata from jpg file");
    }
}
