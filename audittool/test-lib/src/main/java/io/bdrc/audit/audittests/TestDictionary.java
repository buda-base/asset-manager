package io.bdrc.audit.audittests;

import io.bdrc.audit.iaudit.AuditTestConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import static io.bdrc.audit.audittests.TestArgNames.*;

/**
 * Moved from shell, so I can use class objects here, with names
 * placeholder for true dynamic linking:
 * See http://ronmamo.github.io/reflections/index.html?org/reflections/Reflections.html
 *
 */
@SuppressWarnings("unused")
public class TestDictionary {

    // Used also in naked constructors - see tests
    public static final String FILE_SEQUENCE_TEST_NAME = "FileSequence";
    public static final String NO_FILES_IN_FOLDER_TEST_NAME = "NoFilesInFolder";
    public static final String NO_FOLDERS_IN_IMAGE_GROUPS_TEST_NAME = "NoFoldersInImageGroups";
    public static final String WEB_IMAGE_ATTRIBUTES_TEST_NAME = "WebImageAttributes";
    public static final String FILE_SIZE_TESTS_NAME = "FileSizeTests";
    public static final String EXIF_ARCHIVE_TEST_NAME = "EXIFArchiveTest";
    public static final String EXIF_IMAGE_TEST_NAME = "EXIFImageTest";
    public static final String EXIF_ARCHIVE_THUMBNAIL_NAME = "EXIFArchiveThumbnail";
    public static final String EXIF_IMAGE_THUMBNAIL_NAME = "EXIFImageThumbnail";
    public static final String NO_IMAGES_TEST_NAME = "NoImageTests";
    public static final String IMAGE_FILENAME_FORMAT = "ImageFileNameFormat";

    public TestDictionary() {
    }


    public Hashtable<String, AuditTestConfig> getTestDictionary() {
        return _TestDictionary;
    }

    private final Hashtable<String, AuditTestConfig> _TestDictionary = new Hashtable<>() {
        {
            put(FILE_SEQUENCE_TEST_NAME, new AuditTestConfig("File Sequence Test",

                    // This statement asserts that the caller has to provide values for these
                    // arguments
                    Arrays.asList(
                            ARC_GROUP_PARENT, DERIVED_GROUP_PARENT),
                    FILE_SEQUENCE_TEST_NAME, FileSequence.class));

            //noinspection ArraysAsListWithZeroOrOneArgument
            put(NO_FILES_IN_FOLDER_TEST_NAME, new AuditTestConfig("No Files in Root Folder",
                    Arrays.asList(""),
                    NO_FILES_IN_FOLDER_TEST_NAME,
                    NoFilesInRoot.class));

            put(NO_FOLDERS_IN_IMAGE_GROUPS_TEST_NAME, new AuditTestConfig("No folders allowed in Image Group folders",
                    Arrays.asList(ARC_GROUP_PARENT, DERIVED_GROUP_PARENT), NO_FOLDERS_IN_IMAGE_GROUPS_TEST_NAME,
                    NoFoldersInImageGroups.class));

            put(WEB_IMAGE_ATTRIBUTES_TEST_NAME, new AuditTestConfig("Web Image Attributes",
                    Collections.singletonList(DERIVED_GROUP_PARENT), WEB_IMAGE_ATTRIBUTES_TEST_NAME,
                    ImageAttributeTests.class));

            put(FILE_SIZE_TESTS_NAME, new AuditTestConfig("File Size Test",
                    Arrays.asList(DERIVED_GROUP_PARENT, MAX_IMAGE_FILE_SIZE), FILE_SIZE_TESTS_NAME, ImageSizeTests.class));

            // jimk asset-manager-85 - run exif tests on both types of images
            put(EXIF_ARCHIVE_TEST_NAME, new AuditTestConfig("Archive Valid EXIF Test",
                    Arrays.asList(ARC_GROUP_PARENT, "ArchiveEXIF"), EXIF_ARCHIVE_TEST_NAME, EXIFTest.class));

            // repurposing the same class yields different LibOutcome values, so user
            // can fine-grain control
            put(EXIF_IMAGE_TEST_NAME, new AuditTestConfig("Image Valid EXIF Test",
                    Arrays.asList(DERIVED_GROUP_PARENT, "ImageEXIF"), EXIF_IMAGE_TEST_NAME, EXIFTest.class));

            // jimk asset-manager-125 - run exif thumbnail tests
            put(EXIF_ARCHIVE_THUMBNAIL_NAME, new AuditTestConfig("Archive EXIF Thumbnail Test",
                    Arrays.asList(ARC_GROUP_PARENT, "ArchiveThumbnailDetection"), EXIF_ARCHIVE_THUMBNAIL_NAME,
                    EXIFTest.class));

            put(EXIF_IMAGE_THUMBNAIL_NAME, new AuditTestConfig("Image EXIF Thumbnail Test",
                    Arrays.asList(DERIVED_GROUP_PARENT, "ImageThumbnailDetection"), EXIF_ARCHIVE_THUMBNAIL_NAME,
                    EXIFTest.class));

            put(IMAGE_FILENAME_FORMAT, new AuditTestConfig("Image file name format test",
                    Collections.singletonList(DERIVED_GROUP_PARENT),IMAGE_FILENAME_FORMAT,
                    ImageFileNameFormatTest.class));
        }
    };
}
