package com.control.listener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.control.assertion.BaseAssertion;
import com.control.ControlCenter;
import com.reporting.ReportAdapter;

public class CustomTestResultListener implements ITestListener, IInvokedMethodListener {

    public static Map<String, String> testMethods = new HashMap<String, String>();

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    protected ReportAdapter reporter = ControlCenter.getInstance().getReporter();

    @Override
    public void onStart(ITestContext context) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onTestStart(ITestResult result) {
        Method m = result.getMethod().getConstructorOrMethod().getMethod();

        String testFullName = result.getTestClass().getRealClass().getName() + "." + m.getName();
        String msg = testFullName;

        // if(result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class)
        // != null) {
        // if(m.getAnnotation(Test.class).invocationCount()>1) {
        // ITestNGMethod currentMethod =
        // Arrays.asList(result.getTestContext().getAllTestMethods()).stream().filter(x->
        // x.getMethodName().equals(m.getName())).collect(Collectors.toList()).get(0);
        // System.out.println(currentMethod.getMethodName());
        // System.out.println(m.getName());
        // System.out.println(currentMethod.getCurrentInvocationCount());
        // testFullName += " Invocation " + currentMethod.getCurrentInvocationCount();
        // }
        // }

        reporter.info("Start Test: " + msg);
    }

    /**
     * If retry is triggered, the test case will be considered as skip, but this skip test
     * actually count in the total test case. For example, 2 test cases are executed, one
     * failed and got retried for 3 times, and at the end the total run will be 5 test
     * cases but in reality, we only run 2 test cases. This code will remove the retry
     * from the total run count.
     */
    @Override
    public void onFinish(ITestContext context) {
        if (context.getAllTestMethods().length > 0) {
            reporter.endReport();
        }

        Set<ITestResult> failedResultSet = context.getFailedTests().getAllResults();
        for (ITestResult failedResult : failedResultSet) {
            ITestNGMethod failedMethod = failedResult.getMethod();
            if (context.getFailedTests().getResults(failedMethod).size() > 1) {
                failedResultSet.remove(failedResult);
            }
            if (!context.getPassedTests().getResults(failedMethod).isEmpty()) {
                failedResultSet.remove(failedResult);
            }
        }

        Set<ITestResult> skippedResultSet = context.getSkippedTests().getAllResults();
        for (ITestResult skippedResult : skippedResultSet) {
            ITestNGMethod skippedMethod = skippedResult.getMethod();
            if (!context.getSkippedTests().getResults(skippedMethod).isEmpty()) {
                skippedResultSet.remove(skippedResult);
            }
            if (!context.getPassedTests().getResults(skippedMethod).isEmpty()) {
                skippedResultSet.remove(skippedResult);
            }
            if (!context.getFailedTests().getResults(skippedMethod).isEmpty()) {
                skippedResultSet.remove(skippedResult);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        String reason = null;
        String stackTrace = null;
        if (tr != null) {
            if (tr.getThrowable() != null) {
                reason = tr.getThrowable().toString();
                stackTrace = Arrays.asList(tr.getThrowable().getStackTrace()).stream().map(x -> x.toString())
                        .collect(Collectors.joining("\n"));
                reporter.fail("Finish test case with failure : <br>"
                        + "<a style=\"cursor:pointer\" onclick=\"$(this).next('xmp').toggle()\"> Exceptions: (Click to Expand)</a>"
                        + "<xmp style=\"display:none\">\n" + "Failure at [" + reason + "]\n" + stackTrace + " </xmp>");
            }
            else {
                reporter.fail("Skipped test case: " + tr.getName());
            }
        }
        reporter.endTest(reason, stackTrace);
    }

    @Override
    public synchronized void onTestFailure(ITestResult tr) {
        String reason = null;
        String stackTrace = null;
        if (tr.getThrowable() != null) {
            reason = tr.getThrowable().toString();
            stackTrace = Arrays.asList(tr.getThrowable().getStackTrace()).stream().map(x -> x.toString())
                    .collect(Collectors.joining("\n"));
            reporter.fail("Finish test case with failure : <br>"
                    + "<a style=\"cursor:pointer\" onclick=\"$(this).next('xmp').toggle()\"> Exceptions: (Click to Expand)</a>"
                    + "<xmp style=\"display:none\">\n" + "Failure at [" + reason + "]\n" + stackTrace + " </xmp>");
        }
        else {
            reporter.fail("Finish test case with failure");
        }

        reporter.endTest(reason, stackTrace);
    }

    /***
     * Invoked each time a test fails.
     * @param tr :ITestResult
     */
    @Override
    public void onTestSuccess(ITestResult tr) {
        reporter.pass("Finish test case with success");
        reporter.endTest("", "");
        /*
         * All the previous retry attempts of current test would have status as SKIP (if
         * any). Check and remove them.
         */
        ITestContext context = tr.getTestContext();
        Set<ITestResult> skippedResultSet = context.getSkippedTests().getAllResults();
        for (ITestResult skippedResult : skippedResultSet) {
            if (tr.getMethod().getMethodName() == skippedResult.getMethod().getMethodName())
                skippedResultSet.remove(skippedResult);
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        /**
         * Won't implement
         */
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            BaseAssertion assertion = (BaseAssertion) testResult.getAttribute("assertion");
            if (assertion != null) {
                if (!assertion.getVerificationFailures().isEmpty() && testResult.getStatus() == ITestResult.SUCCESS) {
                    // set the SUCCESS test to FAILURE
                    testResult.setStatus(ITestResult.FAILURE);
                }

                int size = assertion.getVerificationFailures().size();
                if (size == 1) {
                    // if there's only one user assertion failure just set that
                    testResult.setThrowable((Throwable) assertion.getVerificationFailures().get(0));
                }
                else if (size != 0) {
                    // create a failure message with all failures and stack traces (except
                    // last failure)
                    StringBuilder failureMessage = new StringBuilder("Multiple validation failures (").append(size)
                            .append("): Please refer to the report for exact location of the failure\n")
                            .append(assertion.getVerificationFailures().stream().map(x -> x.getMessage())
                                    .collect(Collectors.joining("\n")));
                    // set merged throwable
                    Throwable merged = new Throwable(failureMessage.toString());
                    testResult.setThrowable(merged);
                }
            }
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        /**
         * Won't implement
         */
    }

}
