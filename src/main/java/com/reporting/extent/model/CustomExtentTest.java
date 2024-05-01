package com.reporting.extent.model;

import java.util.ArrayList;
import java.util.List;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Log;

public class CustomExtentTest {

    private String uid;

    private ExtentTest test;

    private int retryCounter;

    private String failureReason;

    private String stackTrace;

    private String description;

    private List<String> testMethodsInClass = new ArrayList<String>();

    // we need to know where the setup steps stops in the class test so that we can just
    // add the cleanup step to the method in the after class later
    private int setupMethodStopIndex;

    public CustomExtentTest(ExtentTest test, int retryCounter, String caseName, String description,
            CustomExtentTest testAtClass) {
        if (testAtClass == null) {
            this.test = test;
        }
        else if (testAtClass.getTest().getRunStatus() == LogStatus.UNKNOWN) {
            // this is the scenario where no global setup but global cleanup presents
            this.test = test;
            testAtClass.addTestMethodsInClass(caseName);
        }
        else {
            this.test = test;
            // If the list is not empty, it means some global setup has been performed
            // passing the setup log to current test case log
            if (!testAtClass.getTest().getTest().getLogList().isEmpty()) {
                List<Log> logs = new ArrayList<Log>();
                for (Log l : testAtClass.getTest().getTest().getLogList()) {
                    logs.add(l);
                }
                this.test.getTest().setLog(logs);
                testAtClass.setSetupMethodStopIndex(testAtClass.getTest().getTest().getLogList().size());
            }
            else {
                testAtClass.setSetupMethodStopIndex(0);
            }
            testAtClass.addTestMethodsInClass(caseName);
        }
        this.retryCounter = retryCounter;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ExtentTest getTest() {
        return test;
    }

    public void setTest(ExtentTest test) {
        this.test = test;
    }

    public int getRetryCounter() {
        return retryCounter;
    }

    public void setRetryCounter(int retryCounter) {
        this.retryCounter = retryCounter;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method should only be used when ExtentTest holds the BeforeClass test
     */
    public List<String> getTestMethodsInClass() {
        return testMethodsInClass;
    }

    public void addTestMethodsInClass(String testMethodsInClass) {
        if (!this.testMethodsInClass.contains(testMethodsInClass)) {
            this.testMethodsInClass.add(testMethodsInClass);
        }
    }

    public void setSetupMethodStopIndex(int setupMethodStopIndex) {
        this.setupMethodStopIndex = setupMethodStopIndex;
    }

    public int getSetupMethodStopIndex() {
        return setupMethodStopIndex;
    }

}
