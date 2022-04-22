package io.bdrc.audit.iaudit;

/**
 * Outcomes specific to this test library. Namespace > 100,
 *  to disambiguate between  io.bdrc.am.audit.iaudit.TestMessages values
 */
public class LibOutcome {
    public static final Integer ROOT_NOT_FOUND                  = 101 ;
    public static final Integer FILES_IN_MAIN_FOLDER            = 102 ;
    public static final Integer DIR_IN_IMAGES_FOLDER            = 103 ;
    public static final Integer DIR_FAILS_DIR_IN_IMAGES_FOLDER  = 104 ;
    public static final Integer FILE_SEQUENCE                   = 105 ;
    public static final Integer DIR_FAILS_SEQUENCE              = 106 ;
    public static final Integer DUP_SEQUENCE                    = 107 ;
    public static final Integer DUP_SEQUENCE_FOLDER             = 108 ;
    public static final Integer FILE_COUNT                      = 109 ;
    public static final Integer NO_IMAGE_READER                 = 110 ;
    public static final Integer INVALID_TIFF                    = 111 ;
    public static final Integer FILE_SIZE                       = 112 ;
    public static final Integer BAD_FILE_SIZE_ARG               = 113;
    public static final Integer UNKNOWN                         = 114;
    public static final Integer INVALID_ARCHIVE_EXIF            = 115 ;
    public static final Integer INVALID_IMAGE_EXIF              = 116 ;
    public static final Integer INVALID_ARCHIVE_THUMBNAIL       = 117 ;
    public static final Integer INVALID_IMAGE_THUMBNAIL         = 118 ;
    public static final Integer INVALID_IMAGE_FILENAME_FORMAT   = 119 ;
}

