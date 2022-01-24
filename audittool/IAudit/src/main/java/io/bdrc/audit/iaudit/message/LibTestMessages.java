package io.bdrc.audit.iaudit.message;

import io.bdrc.audit.iaudit.Outcome;

import java.util.Hashtable;

/**
 * Singleton message dictionary
 */
public class LibTestMessages {

    private static final Hashtable<Integer, TestMessageFormat> _messageDictionary = new Hashtable<Integer,
            TestMessageFormat> () {
        {
            put(Outcome.NOT_RUN,  new TestMessageFormat(1, "Test %s awaiting execution"));
            put(Outcome.PASS, new TestMessageFormat(1,"Test %s passed."));
            put(Outcome.SYS_EXC, new TestMessageFormat(2,"Test %s threw exception %s."));
        }
    };
    public static LibTestMessages getInstance() {
        if (null == _instance) {
            _instance = new LibTestMessages();
        }
        return _instance;
    }

    private LibTestMessages()  {

    }

    /**
     * Load this class dictionary into an external dictionary
     * @param messageDict gets this class' message options
     */
    public void setMessages(Hashtable<Integer, TestMessageFormat> messageDict) {
      messageDict.forEach(_messageDictionary::put);
    }

    // caller handles null return
    public TestMessageFormat getMessage(Integer outcome) {
        return  _messageDictionary.get(outcome);
    }

    // region instance fields
    private static LibTestMessages _instance;

}
