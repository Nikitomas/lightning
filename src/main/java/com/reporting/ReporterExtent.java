package com.reporting;

import static com.enums.ContextConstant.RUN_MODE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Log;
import com.reporting.extent.factory.CustomExtentTestFactory;
import com.reporting.extent.factory.CustomReportFactory;
import com.reporting.extent.model.CustomExtentReports;
import com.reporting.extent.model.CustomExtentTest;

public class ReporterExtent implements IReporter {

    private static final Logger LOG = LogManager.getLogger(ReporterExtent.class);

    private CustomExtentReports report;

    private String imagePath;

    private static final String BREAK_LINE = "</br>";

    private static int STACK_TRACE_LIMIT = 15;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HHmmssSSS");

    protected static Map<Long, CustomExtentTest> currentTest = new LinkedHashMap<Long, CustomExtentTest>();

    protected static Map<Long, CustomExtentTest> currentTestAtClassLevel = new LinkedHashMap<Long, CustomExtentTest>();

    protected static Map<String, CustomExtentTest> testCases = new LinkedHashMap<String, CustomExtentTest>();

    public ReporterExtent(String reportRootPath, String imagePath, Map<String, String> params) throws Exception {
        this.imagePath = imagePath;
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        String reportPartialName = timestamp;

        if (params.containsKey(RUN_MODE)) {
            reportPartialName = "develop".equalsIgnoreCase(params.get(RUN_MODE)) ? "develop" : timestamp;
        }
        report = CustomReportFactory.createExtentReport(reportRootPath + "/report_" + reportPartialName + ".html");
        LOG.info("Local Report initialize Reporting Successfully. ");
    }

    private synchronized CustomExtentTest getTest() {
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
            getTest().getTest().log(LogStatus.INFO, msg);
    }

    @Override
    public void warn(String msg) {
        if (getTest() != null)
            getTest().getTest().log(LogStatus.WARNING, msg);
    }

    @Override
    public void pass(String msg) {
        if (getTest().getTest() != null)
            getTest().getTest().log(LogStatus.PASS, msg);
    }

    @Override
    public void fail(String msg) {
        if (getTest().getTest() != null)
            getTest().getTest().log(LogStatus.FAIL, msg);
    }

    @Override
    public void pass(String msg, String details, File file) {
        if (getTest().getTest() != null)
            getTest().getTest().log(LogStatus.PASS, msg, details);
    }

    @Override
    public void info(String msg, String details, File file) {
        this.info(msg + details + getScreenShotPath(file));
    }

    @Override
    public void warn(String msg, String details, File file) {
        this.warn(msg + details + getScreenShotPath(file));
    }

    @Override
    public void fail(String msg, String details, File file) {
        this.fail(msg + details + getScreenShotPath(file));
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (getTest().getTest() != null)
            getTest().getTest().log(LogStatus.WARNING,
                    msg + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    @Override
    public void fail(String msg, Throwable t) {
        if (getTest().getTest() != null)
            getTest().getTest().log(LogStatus.FAIL,
                    msg + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));
    }

    @Override
    public void warn(String msg, Throwable t, File file) {
        if (getTest() != null)
            getTest().getTest().log(LogStatus.WARNING,
                    msg + getScreenShotPath(file) + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    @Override
    public void fail(String msg, Throwable t, File file) {
        if (getTest() != null)
            getTest().getTest().log(LogStatus.FAIL,
                    msg + getScreenShotPath(file) + BREAK_LINE + t.getMessage() + BREAK_LINE + getFormatedStack(t));

    }

    public synchronized void startTestAtClass() {
        CustomExtentTest test = null;
        test = CustomExtentTestFactory.create();
        currentTestAtClassLevel.put((long) (Thread.currentThread().getId()), test);
    }

    public synchronized void endTestAtClass() {
        CustomExtentTest test = currentTestAtClassLevel.get((long) (Thread.currentThread().getId()));
        if (test != null) {
            List<Log> globalCleanupList = test.getTest().getTest().getLogList().subList(test.getSetupMethodStopIndex(),
                    test.getTest().getTest().getLogList().size());

            for (String s : test.getTestMethodsInClass()) {
                if (testCases.containsKey(s)) {
                    for (Log l : globalCleanupList) {
                        testCases.get(s).getTest().getTest().getLogList().add(l);
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
        CustomExtentTest test = null;
        if (testCases == null)
            testCases = new LinkedHashMap<String, CustomExtentTest>();

        if (testCases.containsKey(testName)) {
            // executing this block of code will mean the retry is enabled
            int retryCount = testCases.get(testName).getRetryCounter() + 1;
            String retryName = "RERUN: [" + (retryCount + 1) + "]";
            // set the previous run result aside
            // testCases.put(retryCount == 1 ? testName : testName + retryName,
            // testCases.get(testName));
            test = CustomExtentTestFactory.create(testName, desc, groups, packageLocation, dependsOnMethods, retryName,
                    retryCount, currentTestAtClassLevel.get((long) (Thread.currentThread().getId())));
            testCases.put(testName + retryName, test);
        }
        else {
            test = CustomExtentTestFactory.create(testName, desc, groups, packageLocation, dependsOnMethods,
                    currentTestAtClassLevel.get((long) (Thread.currentThread().getId())));
            testCases.put(testName, test);
        }

        currentTest.put((long) (Thread.currentThread().getId()), test);

    }

    @Override
    public synchronized void endTest(String failureReason, String stackTrace) {
        if (failureReason != null && !failureReason.isEmpty())
            currentTest.get((long) (Thread.currentThread().getId())).setFailureReason(failureReason);
        if (stackTrace != null && !stackTrace.isEmpty())
            currentTest.get((long) (Thread.currentThread().getId())).setStackTrace(stackTrace);
        getTest().getTest().getTest().setEndedTime(Calendar.getInstance().getTime());

    }

    public synchronized void cleanTest() {
        currentTest.put((long) (Thread.currentThread().getId()), null);
    }

    @Override
    public void endReport() {
        try {

            for (Entry<String, CustomExtentTest> e : testCases.entrySet()) {
                ExtentTest t = e.getValue().getTest();
                report.startTest(t);

                t.getTest().setName(t.getTest().getName() + "</BR><B><font color='blue'> ("
                        + t.getTest().getRunDuration() + ")</font></B>");
                report.endTest(t);
            }
            testCases.clear();
            testCases = null;
            report.close();

            LOG.info("Local Report closed successfully. ");
        }
        catch (Exception e) {
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

    private String getScreenShotPath(File file) {
        String htmlTagPath = "";
        if (file != null) {
            try {
                String screenShotName = "/_" + FORMAT.format(new Date()) + ".png";
                FileUtils.copyFile(file, new File(imagePath + screenShotName));
                htmlTagPath = BREAK_LINE + getTest().getTest().addScreenCapture("./sc/" + screenShotName);
            }
            catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        else {
            LOG.error("Fail: Screenshot file is null, cannot add screenshot");
        }
        return htmlTagPath;
    }

}
