package io.bdrc.am.audit.iaudit;

import java.util.Hashtable;

import static java.util.Arrays.copyOf;


public class TestMessage {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static Hashtable<Integer, TestMessageFormat> MessageDict;

    final private static TestMessageFormat DefaultTestMessageFormat;

    static {
        MessageDict = new Hashtable<Integer, TestMessageFormat>() {
            {
                put(Outcome.NOT_RUN,  new TestMessageFormat(1, "Test %s awaiting execution"));
                put(Outcome.PASS, new TestMessageFormat(1,"Test %s passed."));
                put(Outcome.SYS_EXC, new TestMessageFormat(2,"Test %s threw exception %s."));
            }
        };

        // Should be able to handle arbitrary arguments here, but dont care
        DefaultTestMessageFormat = new TestMessageFormat(1, "Unknown outcome code _HERE_. args %s");
    }

    /**
     * Constructor
     * @param outcome code
     * @param messageBits varargs of message string arguments
     */
    TestMessage(Integer outcome, String... messageBits)
    {
        _outcome = outcome;
        TestMessageFormat tmf = GetMessage(outcome);
        if (messageBits != null) {
            _message = String.format(tmf.formatString, (Object[]) copyOf(messageBits, tmf.argCount));
        }
    }

    // region properties
    public Integer getOutcome() {
        return _outcome;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    private TestMessageFormat GetMessage(Integer outcome) {
        TestMessageFormat tmf = TestMessage.MessageDict.get(outcome);
        if (tmf == null ) {
            tmf = TestMessage.DefaultTestMessageFormat;
            tmf.formatString = tmf.formatString.replace("_HERE_",outcome.toString());
        }
        return tmf;
    }

    //endregion
    // region fields
    private Integer _outcome;

    private String _message;
    //endregion
}