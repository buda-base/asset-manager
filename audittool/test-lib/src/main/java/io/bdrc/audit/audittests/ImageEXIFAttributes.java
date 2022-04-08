package io.bdrc.audit.audittests;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.color.ICC_Profile;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ImageEXIFAttributes {

    static final int NO_EXIF_ROTATION_TAG = (0);
    static final int EXIF_ROTATION_TAG_UP = (1);
    static final int ORIENTATION_TAG = ExifIFD0Directory.TAG_ORIENTATION;

    public List<ImageExifBead> getExifAttributes() {
        return exifAttributes;
    }

    // Capture attributes we're interested in
    private final List<ImageExifBead> exifAttributes = new ArrayList<>();

    /**
     * Collect EXIF attributes for the named test
     *
     * @param fileObject image file to test
     * @param testName   a TestDictionary key
     * @throws IOException                when the file cannot be found or opened
     * @throws UnsupportedFormatException when the imaging library cant process
     */
    public ImageEXIFAttributes(File fileObject, String testName) throws IOException, UnsupportedFormatException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(fileObject);
            if (testName.equalsIgnoreCase(TestDictionary.EXIF_IMAGE_TEST_NAME)
                    || testName.equalsIgnoreCase(TestDictionary.EXIF_ARCHIVE_TEST_NAME)) {
                final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                if (exifIFD0Directory != null) {
                    if (exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {

                        // jimk-asset-manager-125 refactor ImageExifBead
                        exifAttributes.add(new ImageExifBead(exifIFD0Directory, ExifIFD0Directory.TAG_ORIENTATION));
                    }
                }
            }

            if (testName.equalsIgnoreCase(TestDictionary.EXIF_ARCHIVE_THUMBNAIL_NAME)
            || testName.equalsIgnoreCase(TestDictionary.EXIF_IMAGE_THUMBNAIL_NAME)
            )

            // Look for EXIF Thumbnails. Assume that both offset and length have to be > 0
            // to represent an actual thumbnail
            {
                final ExifThumbnailDirectory exifThumbnailDirectory =
                        metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
                if (exifThumbnailDirectory != null) {
                    if (exifThumbnailDirectory.containsTag(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET)
                            && exifThumbnailDirectory.containsTag(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH)) {
                        int tempOffset = exifThumbnailDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET);
                        int tempLength = exifThumbnailDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH);

                        // Assume these both need to be here to actually represent a thumbnail
                        if (tempOffset > 0 || tempLength > 0) {

                            exifAttributes.add(new ImageExifBead(exifThumbnailDirectory, ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
                            exifAttributes.add(new ImageExifBead(exifThumbnailDirectory, ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
                        }
                    }
                }

                // Finally, scan for a Photoshop Thumbnail directory This was empirically discovered by W1NLM2232,
                // which was processed by photoshop
                Optional<Directory> maybe = StreamSupport.stream(
                                metadata.getDirectories().spliterator(), false)
                        .filter(x -> x.getName().equalsIgnoreCase("PhotoShop"))
                        .findFirst();

                if (maybe.isPresent()) {
                    Directory photoshopExifDirectory = maybe.get();
                    if (photoshopExifDirectory.containsTag(0x040c)) {
                        exifAttributes.add(new ImageExifBead(photoshopExifDirectory, 0x40c));
                    }
                }
            }






        } catch (ImageProcessingException | MetadataException ipe) {
            throw new UnsupportedFormatException(MessageFormat.format("EXIF Image Processing Exception{0}",
                    ipe.getMessage()));
        }
    }

    /**
     * Use different libraries to get metadata
     *
     * @param fileObject analyze this
     */
    public void DumpCommonsMetadata(final File fileObject) {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        // just in case caller forgets
        if (logger.isDebugEnabled()) {
            try {
                // Pass1: apache commons
                BufferedInputStream bStream = new BufferedInputStream(FileUtils.openInputStream(fileObject), 4096);
                bStream.mark(1289987);
                logger.debug("----------- apache commons imaging   {} ImageMetadata BEGIN  -------------",
                        fileObject);
                ImageMetadata imageMetadata = Imaging.getMetadata(bStream, null);
                for (ImageMetadata.ImageMetadataItem imi : imageMetadata.getItems()) {
                    logger.debug(String.valueOf(imi));
                }

                bStream.reset();
                ICC_Profile iccProfile = Imaging.getICCProfile(bStream, null);
                if (iccProfile != null) {
                    logger.debug("-----------  apache commons imaging  {}  ICC Profile BEGIN  -------------", fileObject.getAbsolutePath());
                    logger.debug("Class {} /  numComponents: {} / colorspace type: {}", iccProfile.getProfileClass(),
                            iccProfile.getNumComponents(),
                            iccProfile.getColorSpaceType());
                    logger.debug("-----------  apache commons imaging   ICC Profile END  -------------");
                } else {
                    logger.debug("-----------  apache commons imaging No ICC Profile detected -----------");
                }

            } catch (IOException | ImageReadException ioe) {
                logger.error("{} file {}", ioe, fileObject);
            } finally {
                logger.debug("-----------  apache commons imaging  {}  ImageMetadata END  -------------", fileObject);
            }
        }
    }

    public void DumpEXIFDirectories(File fileObject) throws IOException, UnsupportedFormatException {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        // in case caller forgets
        if (logger.isDebugEnabled()) {
            try {
                Metadata metadata = ImageMetadataReader.readMetadata(fileObject);
                for (Directory directory : metadata.getDirectories()) {
                    String dirName = directory.getName();
                    for (Tag tag : directory.getTags()) {
                        try {
                            String tagName = tag.getTagName();
                            int tagType = tag.getTagType();
                            Object ttv = directory.getObject(tagType);
                            String tagTypeHex = tag.getTagTypeHex();
                            String tagDesc = tag.getDescription();
                            logger.debug("{} - {}  -type {} ({}) value: {} {}",
                                    dirName, tagName, tagType, tagTypeHex, ttv, tagDesc);
                        } catch (Exception eek) {
                            logger.error(eek.getMessage());
                        }
                    }
                    if (directory.hasErrors()) {
                        for (String error : directory.getErrors()) {
                            logger.error(error);
                        }
                    }
                }
            } catch (ImageProcessingException ipe) {
                throw new UnsupportedFormatException(MessageFormat.format("EXIF Image Processing Exception{0}",
                        ipe.getMessage()));

            }
        }
    }


}

// Some diagnostics
//            // Just save the value
//            switch (exifOrientation) {
//
//                case 6:
//                    mtx.postRotate(90);
//                    break;  // top left
//                case 3:
//                    mtx.postRotate(180);;
//                    break;  // top right
//                case 8:
//                    mtx.postRotate(270);
//                    break;  // bottom right
//
//            }
//            for (Directory directory : metadata.getDirectories()) {
//                for (Tag tag : directory.getTags()) {
//                    String dirName = directory.getName();
//                    String tagName = directory.getName();
//                    int tagType = tag.getTagType();
//                    int ttv = directory.getInt(tagType);
//                    String tagTypeHex = tag.getTagTypeHex();
//                    String tagDesc = tag.getDescription();
//                    System.out.format("[%s] - %s  -type %d (0x%s) value: %d %s\n",
//                            dirName, tagName, tagType, tagTypeHex, ttv, tagDesc);
//                }
//                if (directory.hasErrors()) {
//                    for (String error : directory.getErrors()) {
//                        System.err.format("ERROR: %s", error);
//                    }
//                }
//            }