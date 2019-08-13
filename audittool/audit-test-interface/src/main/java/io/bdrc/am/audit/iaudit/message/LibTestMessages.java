package io.bdrc.am.audit.iaudit.message;

import io.bdrc.am.audit.iaudit.Outcome;

import java.util.Hashtable;

/**
 * Singelton message dictionary
 */
public class LibTestMessages {

    private static Hashtable<Integer, TestMessageFormat> _messageDictionary = new Hashtable<Integer,
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

    public void setMessages(Hashtable<Integer, TestMessageFormat> messageDict) {
        _messageDictionary.putAll(messageDict);
    }

    // caller handles null return
    public TestMessageFormat getMessage(Integer outcome) {
        return  _messageDictionary.get(outcome);
    }

    // region instance fields
    private static LibTestMessages _instance;

}
