package io.bdrc.am.audit.iaudit;

/**
 * Mnemonics for outcomes. See io.bdrc.am.audit.iaudit.TestMessages for message strings
 */
public enum Outcome {
    NOT_RUN,
    PASS,
    FAIL,
    SYS_EXC,
    ROOT_NOT_FOUND,
    FILE_SEQUENCE,
    FILES_IN_MAIN_FOLDER,
    DIR_IN_IMAGES_FOLDER,
    FILE_COUNT,
    DUP_SEQUENCE,
    DUP_SEQUENCE_FOLDER
}