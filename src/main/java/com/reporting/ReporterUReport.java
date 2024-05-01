package com.reporting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.components.ra.driver.RestAssuredDriver;
import com.relevantcodes.extentreports.LogStatus;
import com.reporting.ureport.UReportAPIService;
import com.reporting.ureport.UReportAuthenticator;
import com.reporting.ureport.UReportConstant;
import com.reporting.ureport.UReportStep;
import com.reporting.ureport.UReportTest;
import com.reporting.ureport.UReportTestFactory;

import io.restassured.RestAssured;

public class ReporterUReport implements IReporter {

    private static final Logger LOG = LogManager.getLogger(ReporterUReport.class);

    RestAssuredDriver driver;

    UReportAPIService reportService;

    private String buildID;

    private static final String BREAK_LINE = "</br>";

    private static int STACK_TRACE_LIMIT = 15;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HHmmssSSS");

    protected static Map<Long, UReportTest> currentTest = new LinkedHashMap<Long, UReportTest>();

    protected static Map<Long, UReportTest> currentTestAtClassLevel = new LinkedHashMap<Long, UReportTest>();

    protected static Map<String, UReportTest> testCases = new LinkedHashMap<String, UReportTest>();

    public ReporterUReport(Map<String, String> params) throws Exception {
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());

        LOG.info("UReport initialize Reporting Successfully. ");

        if (!params.get(UReportConstant.UREPORT_URL.getValue()).isEmpty()
                && !params.get(UReportConstant.REPORT_USER.getValue()).isEmpty()
                && !params.get(UReportConstant.REPORT_PWD.getValue()).isEmpty()) {
            RestAssured.useRelaxedHTTPSValidation();
            RestAssured.urlEncodingEnabled = true;
            driver = new RestAssuredDriver(params.get("UREPORT_URL"), new UReportAuthenticator(params));
            reportService = new UReportAPIService(driver);
            driver.login(params.get("UREPORT_USERNAME"), params.get("UREPORT_PASSWORD"));
            buildID = reportService.createBuild(params);
        }
    }

    private synchronized UReportTest getTest() {
        if (currentTest.get((long) (Thread.currentThread().getId())) == null) {
            if (currentTestAtClassLevel.get(((long) (Thread.currentThread().getId()))) == null)
                return null;
            else
                return currentTestAtClassLevel.get((long) (Thread.currentThread().getId()));
        }
        return currentTest.get((long) (Thread.currentThread().getId()));
    }

    @Override
    public void info(String msg) {
        if (getTest() != null)
            getTest().log(LogStatus.INFO, msg);
    }

    @Override
    public void warn(String msg) {
        if (getTest() != null)
            getTest().log(LogStatus.WARNING, msg);
    }

    @Override
    public void pass(String msg) {
        if (getTest() != null)
            getTest().log(LogStatus.PASS, msg);
    }

    @Override
    public void fail(String msg) {
        if (getTest() != null)
            getTest().log(LogStatus.FAIL, msg);
    }

    @Override
    public void pass(String msg, String details, File file) {
        if (getTest() != null)
            getTest().log(LogStatus.PASS, msg + details);
    }

    @Override
    public void info(String msg, String details, File file) {
        this.info(msg + details + file);
    }

    @Override
    public void warn(String msg, String details, File file) {
        this.warn(msg + details + file);
    }

    @Override
    public void fail(String msg, String details, File file) {
        this.fail(msg + details + file);
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (getTest() != null)
            getTest().log(LogStatus.WARNING, msg + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    @Override
    public void fail(String msg, Throwable t) {
        if (getTest() != null)
            getTest().log(LogStatus.FAIL, msg + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));
    }

    @Override
    public void warn(String msg, Throwable t, File file) {
        if (getTest() != null)
            getTest().log(LogStatus.WARNING, msg + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    @Override
    public void fail(String msg, Throwable t, File file) {
        if (getTest() != null)
            getTest().log(LogStatus.FAIL, msg + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    public synchronized void startTestAtClass() {
        UReportTest test = null;
        test = UReportTestFactory.create();
        currentTestAtClassLevel.put((long) (Thread.currentThread().getId()), test);
    }

    public synchronized void endTestAtClass() {
        UReportTest test = currentTestAtClassLevel.get((long) (Thread.currentThread().getId()));
        if (test != null) {
            List<UReportStep> globalCleanupList = test.getLogList().subList(test.getSetupMethodStopIndex(),
                    test.getLogList().size());

            for (String s : test.getTestMethodsInClass()) {
                if (testCases.containsKey(s)) {
                    for (UReportStep l : globalCleanupList) {
                        testCases.get(s).getLogList().add(l);
                    }
                }
            }
        }
        // reset both map at the end of class
        currentTest.put((long) (Thread.currentThread().getId()), null);
        currentTestAtClassLevel.put((long) (Thread.currentThread().getId()), null);
    }

    @Override
    public synchronized void startTest(String testName, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods) {
        UReportTest test = null;

        if (testCases == null)
            testCases = new LinkedHashMap<String, UReportTest>();

        if (testCases.containsKey(testName)) {
            // executing this block of code will mean the retry is enabled
            int retryCount = testCases.get(testName).getRetryCounter() + 1;
            String retryName = "RERUN: [" + (retryCount + 1) + "]";
            // set the previous run result aside
            test = UReportTestFactory.create(testName, desc, groups, packageLocation, retryCount,
                    currentTestAtClassLevel.get((long) (Thread.currentThread().getId())));
            testCases.put(testName + retryName, test);
        }
        else {
            test = UReportTestFactory.create(testName, desc, groups, packageLocation,
                    currentTestAtClassLevel.get((long) (Thread.currentThread().getId())));
            testCases.put(testName, test);
        }
        currentTest.put((long) (Thread.currentThread().getId()), test);

    }

    @Override
    public synchronized void endTest(String failureReason, String stackTrace) {
        if (failureReason != null && !failureReason.isEmpty())
            currentTest.get((long) (Thread.currentThread().getId())).setFailure(failureReason, stackTrace);
        getTest().finishTest(buildID);
    }

    public synchronized void cleanTest() {
        currentTest.put((long) (Thread.currentThread().getId()), null);
    }

    @Override
    public void endReport() {
        try {
            int pass = 0;
            int fail = 0;
            int skip = 0;
            int total = testCases.entrySet().size();
            int index = 0;
            int max = 30;
            String payload = "{ \"tests\": [";
            for (Entry<String, UReportTest> e : testCases.entrySet()) {
                if (e.getValue().getStatus() == LogStatus.FAIL) {
                    fail++;
                }
                else if (e.getValue().getStatus() == LogStatus.SKIP) {
                    skip++;
                }
                else if (e.getValue().getStatus() == LogStatus.PASS) {
                    pass++;
                }
                System.out.println("payload: " + e.getValue().toPayload());

                index++;
                if (index % max == 0) {
                    payload += e.getValue().toPayload();
                    reportService.createTests(payload + "]}");
                    payload = "{ \"tests\": [";
                }
                else {
                    payload += e.getValue().toPayload() + ",";
                }
            }
            if (!payload.equalsIgnoreCase("{ \"tests\": [")) {
                reportService.createTests(payload.substring(0, payload.length() - 1) + "]}");
            }

            reportService.updateBuild(buildID, "{" + "\"pass\":" + pass + "," + "\"fail\":" + fail + "," + "\"skip\":"
                    + skip + "," + "\"total\":" + total + "}");

            testCases.clear();
            testCases = null;

            LOG.info("Local Report closed successfully. ");
        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
    }

    private String getFormatedStack(Throwable e) {
        StringBuilder sb = new StringBuilder();
        int nline = 1;
        if (e != null)
            for (StackTraceElement element : e.getStackTrace()) {
                if (nline <= STACK_TRACE_LIMIT && element != null)
                    sb.append(element.toString() + BREAK_LINE);
                else
                    break;
                nline++;
            }
        return sb.toString();
    }

}
