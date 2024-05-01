package com.base;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.control.assertion.BaseAssertion;
import com.components.ComponentManager;
import com.control.ControlCenter;
import com.control.listener.CustomTestResultListener;
import com.enums.ContextConstant;
import com.exception.FrameworkException;
import com.reporting.ReportAdapter;

@Listeners(CustomTestResultListener.class)
public abstract class BaseTest implements IPayload {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    protected ReportAdapter reporter = controlCenter.getReporter();

    protected WebDriver driver;

    protected BaseAssertion assertion;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteAtBase(ITestContext context) throws Exception {
        try {
            logger.info("Start Suite: " + context.getSuite().getName());
            logger.info(context.getCurrentXmlTest().getAllParameters());
            logger.info(" ============================================================================= ");
            controlCenter.initialize(context.getCurrentXmlTest().getAllParameters());
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteAtBase(ITestContext context) throws Exception {

    }

    @BeforeClass(alwaysRun = true)
    public void beforeClassAtBase(ITestContext context) throws Exception {
        controlCenter.setComponentManager(new ComponentManager(context.getCurrentXmlTest().getAllParameters()));
        assertion = new BaseAssertion(Boolean.valueOf(controlCenter.getParameter(ContextConstant.IS_SOFT)));
    }

    /**
     * Before method, initialization.
     * @param m: Method
     * @param context: ITestContext
     * @throws FrameworkException : customized core common exception
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethodAtBase(Method m, ITestContext context, ITestResult testResult) throws Exception {
        try {
            reporter.startTest(m.getName(), m.getAnnotation(Test.class).description(),
                    m.getAnnotation(Test.class).groups(), m.getDeclaringClass().toString(),
                    m.getAnnotation(Test.class).dependsOnMethods());

            assertion = new BaseAssertion(Boolean.valueOf(controlCenter.getParameter(ContextConstant.IS_SOFT)));
            // set the assertion that will be used by listener
            testResult.setAttribute("assertion", assertion);
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
        catch (Throwable t) {
            throw new Exception("execute beforeTest method failed due to some Error", t);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodAtBase() {
    }

    protected void waitImplicitly(int seconds) throws Exception {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e) {
            throw new Exception("Something is wrong when try to wait for operations in Thread.sleep");
        }
    }

}
