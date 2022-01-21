package io.bdrc.am.audit.audittests;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
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
import java.util.Properties;

public class ImageEXIFAttributes {

    static final int NO_EXIF_ROTATION_TAG = (0);
    static final int EXIF_ROTATION_TAG_UP = (1);
    static final int ORIENTATION_TAG = ExifIFD0Directory.TAG_ORIENTATION ;
    int ExifRotation = NO_EXIF_ROTATION_TAG;

    public List<ImageEXIFBead> getExifAttributes() {
        return exifAttributes;
    }

    // Capture attributes we're interested in
    private List<ImageEXIFBead> exifAttributes = new ArrayList<>();

    public ImageEXIFAttributes(File fileObject) throws IOException, UnsupportedFormatException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(fileObject);
            final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0Directory == null) {
                return;
            }

            if (exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {

                Tag exifTag = new Tag(ExifIFD0Directory.TAG_ORIENTATION, exifIFD0Directory);


                // Get some descriptive data about the rotation, for diagnostics
                ImageEXIFBead ieb = new ImageEXIFBead();
                ieb.setTagNumber(ExifIFD0Directory.TAG_ORIENTATION);
                ieb.setTagValue(exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION));
                ieb.setTagDescription(String.format("[%s] - %s  value: %d desc: %s",
                        exifIFD0Directory.getName(), exifTag.getTagName(), ieb.getTagValue(),exifTag.getDescription()
                ));
                exifAttributes.add(ieb);
            }
        } catch (ImageProcessingException | MetadataException ipe) {
            throw new UnsupportedFormatException(MessageFormat.format("EXIF Image Processing Exception{0}",
                    ipe.getMessage()));
        }

    }

    /**
     * Use different libraries to get metadata
     * @param fileObject analyze this
     */
    public void DumpCommonsMetadata(final File fileObject)  {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        // just in case caller forgets
        if (logger.isDebugEnabled()) {
            try {
                // Pass1: apache commons
                BufferedInputStream bStream = new BufferedInputStream(FileUtils.openInputStream(fileObject), 4096);
                bStream.mark(1289987);
                logger.debug("----------- apache commons imaging   {} ImageMetadata BEGIN  -------------",
                        fileObject.getAbsolutePath());
                ImageMetadata imageMetadata = Imaging.getMetadata(bStream, null);
                for (ImageMetadata.ImageMetadataItem imi : imageMetadata.getItems()) {
                    logger.debug(String.valueOf(imi));
                }
                logger.debug("-----------  apache commons imaging    ImageMetadata END  -------------");
                bStream.reset();
                logger.debug("-----------  apache commons imaging  {}  ICC Profile BEGIN  -------------", fileObject.getAbsolutePath());
                ICC_Profile iccProfile = Imaging.getICCProfile(bStream, null);
                logger.debug("Class {} /  numComponents: {} / colorspace type: {}", iccProfile.getProfileClass(),
                        iccProfile.getNumComponents(),
                        iccProfile.getColorSpaceType());
                logger.debug("-----------  apache commons imaging   ICC Profile END  -------------");

            } catch (IOException | ImageReadException ioe) {
                logger.error("{} file {}", ioe, fileObject);
            }
        }
    }

    public void DumpEXIFDirectories(File fileObject) throws IOException, UnsupportedFormatException {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        // in case caller forgets
        if (logger.isDebugEnabled()) {
            Properties p = System.getProperties();
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
//                        System.out.format("[%s] - %s  -type %d (0x%s) value: %s %s\n",
//                                dirName, tagName, tagType, tagTypeHex, ttv, tagDesc);
                            logger.debug("{} - {}  -type {} (0x{}) value: {} {}",
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