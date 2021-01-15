package io.bdrc.assetmanager.testrun;

/**
 * Emulates Audit tool test outcomes
 */
public enum TestRunOutcome {
    NOT_RUN,
    PASS,
    FAIL,
    SYS_EXC,
    ROOT_NOT_FOUND,
    FILE_SEQUENCE,
    DIR_FAILS_SEQUENCE,
    FILES_IN_MAIN_FOLDER,
    DIR_IN_IMAGES_FOLDER,
    DIR_FAILS_DIR_IN_IMAGES_FOLDER,
    FILE_COUNT,
    DUP_SEQUENCE,
    DUP_SEQUENCE_FOLDER,
    UNKNOWN;

}
