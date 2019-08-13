package io.bdrc.am.audit.iaudit;

/**
 * Mnemonics for outcomes. See io.bdrc.am.audit.iaudit.TestMessages for message strings,
 * and individual test libraries for their implementation.
 *
 * Values can go to 100. Space after that reserved for library test messages
 */
 public class Outcome {
    public static final Integer NOT_RUN = 0;
    public static final Integer PASS = 1 ;
    public static final Integer FAIL = 2 ;
    public static final Integer SYS_EXC = 3 ;
}
