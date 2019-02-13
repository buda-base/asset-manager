package io.bdrc.am.audit.iaudit;

import java.util.Hashtable;

import static java.util.Arrays.copyOf;

class TestMessageFormat {
    int argCount;
    String formatString ;

    TestMessageFormat(int argc, String argFormat) {
        argCount = argc;
        formatString = argFormat;
    }
}


public class TestMessage {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Hashtable<Outcome, TestMessageFormat> MessageDict;

    static {
        MessageDict = new Hashtable<Outcome, TestMessageFormat>() {
            {
                put(Outcome.NOT_RUN,  new TestMessageFormat(1, "Test %s awaiting execution"));
                put(Outcome.PASS, new TestMessageFormat(1,"Test %s passed."));
                put(Outcome.ROOT_NOT_FOUND, new TestMessageFormat(1, "Path %s is not a directory or does not exist."));
                put(Outcome.FILES_IN_MAIN_FOLDER,  new TestMessageFormat(1,"Root folder contains file %s"));
                put(Outcome.DIR_IN_IMAGES_FOLDER,  new TestMessageFormat(2,"Images folder %s  contains directory %s"));
                put(Outcome.FILE_SEQUENCE, new TestMessageFormat(1, "Sequence %s not found"));
                put(Outcome.DUP_SEQUENCE,  new TestMessageFormat(2,"Duplicate Sequence %s and %s found"));
                put(Outcome.FILE_COUNT,  new TestMessageFormat(1,"Expected %d files in folder, found %d"));
            }
        };
    }

    /**
     * Constructor
     * @param outcome code
     * @param messageBits varags of message string arguments
     */
    TestMessage(Outcome outcome, String... messageBits)
    {
        _outcome = outcome;
        TestMessageFormat tmf = TestMessage.MessageDict.get(outcome);
        if (messageBits != null) {
            _message = String.format(tmf.formatString, (Object[]) copyOf(messageBits, tmf.argCount));
        }
    }

    // region properties
    public Outcome getOutcome() {
        return _outcome;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    //endregion
    // region fields
    private Outcome _outcome;

    private String _message;
    //endregion
}