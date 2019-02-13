package io.bdrc.am.audit.iaudit;

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
    DUP_SEQUENCE
}