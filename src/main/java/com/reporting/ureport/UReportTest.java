package com.reporting.ureport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.relevantcodes.extentreports.LogStatus;

public class UReportTest {

    private String uid;

    private String buildId;

    private String name;

    private String description;

    private Date startTime;

    private Date endTime;

    private String path;

    private String file;

    private LogStatus status;

    private String failure;

    private int retryCounter;

    private List<String> testMethodsInClass = new ArrayList<String>();

    private List<UReportStep> steps = new ArrayList<UReportStep>();

    // we need to know where the setup steps stops in the class test so that we can
    // just add the cleanup step to the method in the after class later
    private int setupMethodStopIndex;

    public UReportTest() {

    }

    public UReportTest(int retryCounter, String name, String description, String[] groups, String packageLocation,
            UReportTest testAtClass) {
        if (testAtClass == null) {
        }
        else if (testAtClass.getStatus() == LogStatus.UNKNOWN) {
            // this is the scenario where no global setup but global cleanup presents
            testAtClass.addTestMethodsInClass(name);
        }
        else {
            // If the list is not empty, it means some global setup has been performed
            // passing the setup log to current test case log
            if (!testAtClass.getLogList().isEmpty()) {
                testAtClass.setSetupMethodStopIndex(testAtClass.getLogList().size());
            }
            else {
                testAtClass.setSetupMethodStopIndex(0);
            }
            testAtClass.addTestMethodsInClass(name);
        }

        // name and uid
        Matcher m = Pattern.compile("^test_([\\w\\d]+)_(T\\d*)_(.*)").matcher(name);
        if (m.find()) {
            this.uid = m.group(1) + "_" + m.group(2);
            this.name = m.group(0);
        }
        else {
            this.name = name;
        }

        this.startTime = new Date();
        this.description = description;

        List<String> temp = Arrays.asList(packageLocation.replace("class ", "").split("\\."));
        if (temp.size() != 0) {
            this.file = temp.get(temp.size() - 1);
            this.path = temp.subList(0, temp.size() - 1).stream().map(Object::toString)
                    .collect(Collectors.joining("."));
        }
        else {
            this.path = packageLocation.replace("class ", "");
        }

        this.status = LogStatus.UNKNOWN;
        this.retryCounter = retryCounter;
    }

    public LogStatus getStatus() {
        return status;
    }

    public void setFailure(String failure, String stackTrace) {
        this.status = LogStatus.FAIL;
        this.failure = "{ \"error_message\" : \"" + failure + "\", \"stack_trace\": \""
                + stackTrace.replaceAll("\n", "\\\\n") + "\"}";
    }

    public int getRetryCounter() {
        return retryCounter;
    }

    public void setRetryCounter(int retryCounter) {
        this.retryCounter = retryCounter;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    /**
     * This method should only be used holds the BeforeClass test
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

    // new method
    public void log(LogStatus status, String details) {
        if (status == LogStatus.FAIL && status == LogStatus.PASS) {
            this.status = status;
        }
        this.steps.add(new UReportStep(new Date(), details, status.toString()));
    }

    public List<UReportStep> getLogList() {
        return this.steps;
    }

    public void finishTest(String buildId) {
        this.endTime = new Date();
        this.buildId = buildId;
        if (this.status == LogStatus.UNKNOWN) {
            this.status = LogStatus.PASS;
        }
    }

    @Override
    public String toString() {
        return "UReportTest [uid=" + uid + ", build=" + buildId + ", name=" + name + ", description=" + description
                + ", startTime=" + startTime + ", endTime=" + endTime + ", path=" + path + ", file=" + file
                + ", status=" + status.toString().toUpperCase() + ", failureReason=" + failure + "]";
    }

    public String toPayload() {
        return "{ \"uid\" : \"" + uid + "\", \"build\":\"" + buildId + "\", \"name\":\"" + name
                + "\", \"description\":\"" + description + "\", \"start_time\":\"" + startTime + "\", \"end_time\":\""
                + endTime + "\"," + generateInfo() + ", \"body\":" + generateSteps() + ", \"failure\":" + this.failure
                + ", \"status\":\"" + status.toString().toUpperCase() + "\"}";
    }

    private String generateInfo() {
        return "\"info\" : { \"path\":\"" + path + "\", \"file\":\"" + file + "\"}";
    }

    private String generateSteps() {
        String payload = "";
        for (UReportStep s : steps) {
            payload += "," + s.generatePayload();
        }
        return "[" + payload.replaceFirst(",", "") + "]";
    }

}
