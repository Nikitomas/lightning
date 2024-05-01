package com.reporting.console;

import java.util.HashMap;
import java.util.Map;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.control.ControlCenter;

public class TestConsoleResultListener implements ITestListener, IInvokedMethodListener {

    public static Map<String, String> testMethods = new HashMap<String, String>();

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    protected ReporterConsole reporter;

    @Override
    public void onStart(ITestContext context) {
        // add extent reporter
        reporter = new ReporterConsole();
        controlCenter.getReporter().addReporter(reporter);
    }

    @Override
    public void onTestStart(ITestResult result) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onFinish(ITestContext context) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        /**
         * Won't implement
         */
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        /**
         * Won't implement
         */
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        /**
         * Won't implement
         */
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        /**
         * Won't implement
         */
    }

}
