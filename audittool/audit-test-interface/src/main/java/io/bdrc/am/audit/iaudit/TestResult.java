package io.bdrc.am.audit.iaudit;

import java.util.ArrayList;

/**
 * Persistent data about a test result
 */
public class TestResult {
    public TestResult(){
        _errors = new ArrayList<>();
    }

    public void AddError(Outcome outcome, String ... operand)
    {
        _errors.add(new TestMessage(outcome, operand));
    }

    public Outcome getOutcome() {
        return _outcome;
    }

    public void setOutcome(final Outcome outcome) {
        _outcome = outcome;
    }

    public Boolean Passed()
    {
        return getOutcome() == Outcome.PASS;
    }

    private Outcome _outcome;

    public ArrayList<TestMessage> getErrors() {
        return _errors;
    }
    private ArrayList<TestMessage> _errors;
}
