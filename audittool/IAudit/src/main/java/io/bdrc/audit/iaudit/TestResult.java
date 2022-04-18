package io.bdrc.audit.iaudit;

import io.bdrc.audit.iaudit.message.TestMessage;

import java.util.ArrayList;

/**
 * Persistent data about a test result
 */
public class TestResult {

    public TestResult() {
    }

    public TestResult(Integer outcome, String ... operand) {
        _outcome = outcome;
        AddError(outcome, operand);
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

    public Boolean Failed() {
        Integer outcome = getOutcome();
        return outcome.equals(Outcome.FAIL) || outcome.equals(Outcome.SYS_EXC);
    }

    public Boolean Skipped() {
        return getOutcome().equals(Outcome.NOT_RUN);
    }
    public Boolean Warnings() {
        return getOutcome().equals(Outcome.WARN);
    }

    private Integer _outcome;

    public ArrayList<TestMessage> getErrors() {
        return _errors;
    }

    final private ArrayList<TestMessage> _errors = new ArrayList<>();
}
