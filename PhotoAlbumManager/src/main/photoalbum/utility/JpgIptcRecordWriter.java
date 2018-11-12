package photoalbum.utility;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.iptc.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Writes photo's description and list of keywords into IPTC record data of jpg file
class JpgIptcRecordWriter {

    // EFFECTS: updates description and list of keywords in metadata for given file;
    //          throws ImageReadException if jpg metadata cannot be read from given file;
    //          throws ImageWriteException if jpg metadata cannot be written to file;
    //          throws IOException if file cannot be read from or written to
    static void writeMetadata(File file, String description, List<String> keywords)
            throws IOException, ImageReadException, ImageWriteException {
        ImageMetadata metadata = Imaging.getMetadata(file);
        JpegPhotoshopMetadata photoshopMetadata;

        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            photoshopMetadata = jpegMetadata.getPhotoshop();
        }
        else {
            photoshopMetadata = new JpegPhotoshopMetadata(new PhotoshopApp13Data(new ArrayList<IptcRecord>(),
                    new ArrayList<IptcBlock>()));
        }

        photoshopMetadata = updateDescription(photoshopMetadata, description);
        photoshopMetadata = updateKeywords(photoshopMetadata, keywords);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new JpegIptcRewriter().writeIPTC(file, os, photoshopMetadata.photoshopApp13Data);

        FileOutputStream outStr = new FileOutputStream(file);
        outStr.write(os.toByteArray());
        outStr.close();
    }

    // EFFECTS: updates photo description in given metadata
    private static JpegPhotoshopMetadata updateDescription(JpegPhotoshopMetadata photoshopMetadata, String description) {
        List<IptcRecord> records = photoshopMetadata.photoshopApp13Data.getRecords();
        removeRecordType(records, IptcTypes.CAPTION_ABSTRACT);

        records.add(new IptcRecord(IptcTypes.CAPTION_ABSTRACT, description));

        return new JpegPhotoshopMetadata(new PhotoshopApp13Data(records,photoshopMetadata.photoshopApp13Data.getRawBlocks()));
    }

    // EFFECTS: updates photo's list of keywords in given metadata
    private static JpegPhotoshopMetadata updateKeywords(JpegPhotoshopMetadata photoshopMetadata, List<String> keywords) {
        List<IptcRecord> records = photoshopMetadata.photoshopApp13Data.getRecords();
        removeRecordType(records, IptcTypes.KEYWORDS);

        for (String keyword : keywords) {
            records.add(new IptcRecord(IptcTypes.KEYWORDS, keyword));
        }

        return new JpegPhotoshopMetadata(new PhotoshopApp13Data(records, photoshopMetadata.photoshopApp13Data.getRawBlocks()));
    }

    // EFFECTS: removes all records of specified type from given list of records
    private static void removeRecordType(List<IptcRecord> records, IptcType type) {
        for (IptcRecord record : new ArrayList<IptcRecord>(records)) {
            if (record.iptcType == type)
                records.remove(record);
        }
    }
}
