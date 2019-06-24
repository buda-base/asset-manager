package io.bdrc.am.audit.iaudit.message;

import java.util.Hashtable;

import static java.util.Arrays.copyOf;


public class TestMessage {

    final private static TestMessageFormat DefaultTestMessageFormat;

    static {

        // Should be able to handle arbitrary arguments here, but dont care
        DefaultTestMessageFormat = new TestMessageFormat(1, "Unknown outcome code _HERE_. args %s");
    }

    /**
     * Constructor
     * @param outcome code
     * @param messageBits varargs of message string arguments
     */
    public TestMessage(Integer outcome, String... messageBits)
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

    /**
     * GetMessage
     * @param outcome key into test messages
     * @return the test message object for the outcome
     */
    private TestMessageFormat GetMessage(Integer outcome) {
        TestMessageFormat tmf = LibTestMessages.getInstance().getMessage(outcome);
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