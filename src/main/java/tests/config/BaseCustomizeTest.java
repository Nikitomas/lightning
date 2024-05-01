
package tests.config;

import java.lang.reflect.Method;

import com.components.ra.driver.BaseAuthenticator;
import com.components.ra.driver.DriverManager;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.log4testng.Logger;

import com.base.BaseTest;
import com.components.ra.driver.RestAssuredDriver;
import com.enums.Component;
import com.enums.ContextConstant;

public class BaseCustomizeTest extends BaseTest {

    private static final Logger logger = Logger.getLogger(BaseCustomizeTest.class);

    protected RestAssuredDriver apiDriver;

    protected DriverManager driverManager;

    /**
     * @throws Exception
     *
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClassCustom() throws Exception {
        try {
            // this.controlCenter.getComponentManager().register(Component.SELENIUM);
            this.controlCenter.getComponentManager().register(Component.RESTASSURED);
        }
        catch (Exception e) {
            logger.error("@BeforeClass");
            logger.error(e);
            throw e;
        }
    }

    /**
     *
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethodCustom(Method m, ITestContext context, ITestResult testResult) throws Exception {
        try {
            // this.controlCenter.getComponentManager().initilize(Component.SELENIUM);
            this.controlCenter.getComponentManager().initialize(Component.RESTASSURED,
                    new BaseAuthenticator(context.getCurrentXmlTest().getAllParameters()));

            // this.controlCenter.getComponentManager().getWebDriver()
            // .get(controlCenter.getParameter(ContextConstant.APPLICATION_SERVER));

            // driver = this.controlCenter.getComponentManager().getWebDriver();
            apiDriver = this.controlCenter.getComponentManager().getRestAssuredDriver();
            driverManager = this.controlCenter.getComponentManager().getDriverManager();

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("@BeforeMethod");
            logger.error(e);
            // this.controlCenter.getComponentManager().destory(Component.SELENIUM);
            throw e;
        }
    }

    /**
     * @throws Exception
     *
     */
    // @AfterMethod(alwaysRun = true)
    // public void afterMethodCustom() throws Exception {
    // try {
    // this.controlCenter.getComponentManager().destory(Component.SELENIUM);
    // } catch (Exception e) {
    // logger.error("@AfterMethod");
    // logger.error(e);
    // throw e;
    // }
    // }

}