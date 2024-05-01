package com.reporting.ureport;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.control.ControlCenter;
import com.reporting.ReporterUReport;

public class TestUReportResultListener implements ITestListener, IInvokedMethodListener {

    public static Map<String, String> testMethods = new HashMap<String, String>();

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    protected ReporterUReport reporter;

    @Override
    public void onStart(ITestContext context) {
        if (controlCenter.getParameter(UReportConstant.UREPORT_URL.getValue()).isEmpty()
                || controlCenter.getParameter(UReportConstant.REPORT_USER.getValue()).isEmpty()
                || controlCenter.getParameter(UReportConstant.REPORT_PWD.getValue()).isEmpty()
                || controlCenter.getParameter(UReportConstant.PRODUCT.getValue()).isEmpty()
                || controlCenter.getParameter(UReportConstant.BUILD.getValue()).isEmpty()) {
            System.err.println(
                    "Framework detect UReport server URL, but one or all mandatory parameters [Product, Type, and Build] are missing .");
        }
        else {
            // add UReport reporter
            try {
                reporter = new ReporterUReport(context.getCurrentXmlTest().getAllParameters());
                controlCenter.getReporter().addReporter(reporter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    @SuppressWarnings("unchecked")
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (reporter == null)
            return;
        if (method.isConfigurationMethod()) {
            try {
                Method m = result.getTestClass().getRealClass().getMethod(result.getName(), new Class[] {});
                if (m.getAnnotation(BeforeClass.class) != null) {
                    reporter.startTestAtClass();
                    reporter.info("<h6><strong> ========== Start Before Class [" + m.getName()
                            + "()] ========== </strong></h6>");
                }

                if (m.getAnnotation(BeforeMethod.class) != null) {
                    reporter.info("<h6><strong> ========== Start Before Method [" + m.getName()
                            + "()] ========== </strong></h6>");
                }

                if (m.getAnnotation(AfterMethod.class) != null && !"afterMethodAtBase".equalsIgnoreCase(m.getName())
                        && !"afterMethodCustom".equalsIgnoreCase(m.getName())) {
                    reporter.info("<h6><strong> ========== Start After Method [" + m.getName()
                            + "()] ========== </strong></h6>");
                }

                if (m.getAnnotation(AfterClass.class) != null) {
                    reporter.info("<h6><strong> ========== Start After Class [" + m.getName()
                            + "()]  ========== </strong></h6>");
                }
            }
            catch (NoSuchMethodException | SecurityException e) {
                // ignore exception
            }
        }
        else {
            Method m;
            try {
                m = result.getTestClass().getRealClass().getMethod(result.getName(), new Class[] {});
                if (m.getAnnotation(Test.class) != null) {
                    reporter.info("<h6><strong> ========== Start Test [" + m.getName() + "] ========== </strong></h6>");
                }
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (reporter == null)
            return;
        if (method.isConfigurationMethod()) {
            try {
                @SuppressWarnings("unchecked")
                Method m = testResult.getTestClass().getRealClass().getMethod(testResult.getName(), new Class[] {});
                if (m.getAnnotation(AfterClass.class) != null) {
                    reporter.endTestAtClass();
                }

                if (m.getAnnotation(AfterMethod.class) != null) {
                    reporter.cleanTest();
                }
            }
            catch (NoSuchMethodException | SecurityException e) {
                // ignore exception
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
