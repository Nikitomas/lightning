package com.control.assertion;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.components.se.gateways.BasicPageGateway;
import com.control.ControlCenter;
import com.reporting.ReportAdapter;

public class BaseAssertion {

    private static final String BREAK_LINE = "</br>";

    protected ReportAdapter reporter = ControlCenter.getInstance().getReporter();

    protected List<Throwable> verificationFailures = new ArrayList<Throwable>();

    protected boolean isSoft = false;

    private WebDriver webDriver = null;

    public BaseAssertion(Boolean isSoft) {
        this.webDriver = ControlCenter.getInstance().getComponentManager().getWebDriver();
        this.isSoft = isSoft;
    }

    public BaseAssertion(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public BaseAssertion(WebDriver webDriver, Boolean isSoft) {
        this.webDriver = webDriver;
        this.isSoft = isSoft;
    }

    public List<Throwable> getVerificationFailures() {
        return verificationFailures;
    }

    /**
     * Asserts that a condition if true.
     * @param condition:boolean
     * @return boolean
     */
    public boolean shouldBeTrue(final boolean condition) {
        return shouldBeTrue(condition, "");
    }

    /**
     * Asserts that a condition is true.
     * @param condition:boolean
     * @param stepDetail:String
     * @return boolean
     */
    public boolean shouldBeTrue(final boolean condition, final String stepDetail) {
        boolean result = false;
        String message = "verifyTrue: " + stepDetail + BREAK_LINE + " A: " + condition;
        try {
            Assert.assertTrue(condition);
            reporter.pass(message);
            result = true;
        }
        catch (AssertionError ae) {
            logFailure(message, ae);
            if (!isSoft) {
                throw ae;
            }
            verificationFailures.add(ae);
        }
        return result;
    }

    /**
     * Asserts that a condition is false.
     * @param condition:boolean
     * @return boolean
     */
    public boolean shouldBeFalse(final boolean condition) {
        return shouldBeFalse(condition, "");
    }

    /**
     * Asserts that a condition is false.
     * @param condition:boolean
     * @param stepDetail:String
     * @return boolean
     */
    public boolean shouldBeFalse(final boolean condition, final String stepDetail) {
        boolean result = false;
        String message = "verifyFalse: " + stepDetail + BREAK_LINE + " A: " + condition;
        try {
            Assert.assertFalse(condition);
            reporter.pass(message);
            result = true;
        }
        catch (AssertionError ae) {
            logFailure(message, ae);
            if (!isSoft) {
                throw ae;
            }
            verificationFailures.add(ae);
        }
        return result;
    }

    /**
     * Asserts that two objects are equal.
     * @param actual:Object
     * @param expected:Object
     * @return boolean
     */
    public boolean shouldBeEqual(final Object actual, final Object expected) {
        return shouldBeEqual(actual, expected, "");
    }

    /**
     * Asserts that two objects are equal.
     * @param actual:Object
     * @param expected:Object
     * @param stepDetail:String
     * @return boolean
     */
    public boolean shouldBeEqual(final Object actual, final Object expected, final String stepDetail) {
        boolean result = false;
        String message = "";

        if (actual != null && expected != null)
            message = "verifyEquals: " + stepDetail + BREAK_LINE + " A: " + actual.toString() + BREAK_LINE + " E: "
                    + expected.toString();
        else if (actual == null)
            message = "verifyEquals: " + stepDetail + BREAK_LINE + " A: " + "null" + BREAK_LINE + " E: "
                    + expected.toString();
        else if (expected == null)
            message = "verifyEquals: " + stepDetail + BREAK_LINE + " A: " + actual.toString() + BREAK_LINE + " E: "
                    + "null";

        try {
            validateExpectedActualValues(actual, expected);
            Assert.assertEquals(actual, expected);
            reporter.pass(message);
            result = true;
        }
        catch (AssertionError ae) {
            logFailure(message, ae);
            if (!isSoft) {
                throw ae;
            }
            verificationFailures.add(ae);
        }

        return result;
    }

    /**
     * Asserts that two objects are not equal.
     * @param actual:Object
     * @param expected:Object
     * @return boolean
     */
    public boolean shouldNotEqual(final Object actual, final Object expected) {
        return shouldNotEqual(actual, expected, "");

    }

    /**
     * Asserts that two objects are not equal.
     * @param actual:Object
     * @param expected:Object
     * @param stepDetail:String
     * @return boolean
     */
    public boolean shouldNotEqual(final Object actual, final Object expected, final String stepDetail) {
        boolean result = false;
        String message = "";

        if (actual != null && expected != null)
            message = "verifyNotEquals: " + stepDetail + BREAK_LINE + " A: " + actual.toString() + BREAK_LINE
                    + " Not E: " + expected.toString();
        else if (actual == null)
            message = "verifyNotEquals: " + stepDetail + BREAK_LINE + " A: " + "null" + BREAK_LINE + " Not E: "
                    + expected.toString();
        else if (expected == null)
            message = "verifyNotEquals: " + stepDetail + BREAK_LINE + " A: " + actual.toString() + BREAK_LINE
                    + " Not E: " + "null";

        try {
            validateExpectedActualValues(actual, expected);
            Assert.assertNotEquals(actual, expected);
            reporter.pass(message);
            result = true;
        }
        catch (AssertionError ae) {
            logFailure(message, ae);
            if (!isSoft) {
                throw ae;
            }
            verificationFailures.add(ae);
        }

        return result;

    }

    /**
     * Validate that two objects are not null,and throws IllegalArgumentException
     * @param expected:Object
     * @param actual:Object
     * @throws IllegalArgumentException throws a IllegalArgumentException
     */
    private void validateExpectedActualValues(Object expected, Object actual) {
        if (expected == null) {
            throw new AssertionError("IllegalArgumentException: Expected argument is null");
        }

        if (actual == null) {
            throw new AssertionError("IllegalArgumentException: Actual argument is null");
        }
    }

    /**
     * @param message
     * @param ae
     */
    private void logFailure(String message, AssertionError ae) {
        if (webDriver != null) {
            reporter.fail(message, ae.getCause().toString(), BasicPageGateway.saveAsScreenShot(webDriver));
        }
        else {
            reporter.fail(message, ae);
        }
    }

}
