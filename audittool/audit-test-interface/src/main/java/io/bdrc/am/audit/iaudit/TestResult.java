package io.bdrc.am.audit.iaudit;

import io.bdrc.am.audit.iaudit.message.TestMessage;

import java.util.ArrayList;

/**
 * Persistent data about a test result
 */
public class TestResult {
    public TestResult(){
        _errors = new ArrayList<>();
    }

    public void AddError(Integer outcome, String ... operand)
    {
        _errors.add(new TestMessage(outcome, operand));
    }

    // No, this CANT be made package private.
    public Integer getOutcome() {
        return _outcome;
    }

    public void setOutcome(final Integer outcome) {
        _outcome = outcome;
    }

    public Boolean Passed()
    {
        return getOutcome().equals(Outcome.PASS);
    }

    private Integer _outcome;

    public ArrayList<TestMessage> getErrors() {
        return _errors;
    }

    final private ArrayList<TestMessage> _errors;
}
