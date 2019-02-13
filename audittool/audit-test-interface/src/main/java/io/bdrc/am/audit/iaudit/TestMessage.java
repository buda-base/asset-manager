package io.bdrc.am.audit.iaudit;

import java.util.Hashtable;

public class TestMessage {
    private static Hashtable<Outcome, String> MessageDict;

    static {
        MessageDict = new Hashtable<Outcome, String>() {
            {
                put(Outcome.NOT_RUN, "Test %s awaiting execution");
                put(Outcome.PASS, "Test %s passed.");
                put(Outcome.ROOT_NOT_FOUND, "Path %s is not a directory or does not exist.");
                put(Outcome.FILES_IN_MAIN_FOLDER, "Root folder contains file %s");
                put(Outcome.DIR_IN_IMAGES_FOLDER, "Images folder %s  contains directory");
                put(Outcome.FILE_SEQUENCE, "Sequence %s not found");
                put(Outcome.DUP_SEQUENCE, "Duplicate Sequence %s and %s found");
                put(Outcome.FILE_COUNT, "Expected %d files in folder, found %d");
            }
        };
    }

    public TestMessage(Outcome outcome, String... messageBits)
    {
        _outcome = outcome;
        if (messageBits != null) {
            _message = String.format(TestMessage.MessageDict.get(outcome), messageBits);
        }
    }

    // region properties
    public Outcome getOutcome() {
        return _outcome;
    }

    public void setOutcome(final Outcome outcome) {
        _outcome = outcome;
    }

    public void setMessage(final String message) {
        _message = message;
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