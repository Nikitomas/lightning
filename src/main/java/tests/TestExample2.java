package tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.components.ra.driver.RestAssuredDriver;
import com.components.ra.json.APIResponse;

import tests.config.BaseUITest;
import tests.services.TestService;

public class TestExample2 extends BaseUITest {

    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        reporter.info("before class test 2");
    }

    @AfterClass(alwaysRun = true)
    public void cleanupClass() {
        reporter.info("after class test 2");
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        RestAssuredDriver driver1 = this.driverManager.getDriver("API_SERVER_TEST_FRIST");
        RestAssuredDriver driver2 = this.driverManager.getDriver("API_SERVER_TEST_SECOND");
        RestAssuredDriver driver3 = this.driverManager.getDriver("API_SERVER_TEST_THIRD");
        RestAssuredDriver driver4 = this.driverManager.getDriver("API_SERVER_TEST_FOUR");

        System.out.println(this.apiDriver.getURL());
        System.out.println(driver1.getURL());
        System.out.println(driver2.getURL());
        System.out.println(driver3.getURL());
        System.out.println(driver4.getURL());

        TestService ts = new TestService(this.apiDriver);
        APIResponse res = ts.getTest();
        assertion.shouldBeEqual(200, res.getStatusCode());
        reporter.info("before method test 2");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        reporter.info("after method test 2");
    }

    // @Test(groups="Example", enabled = true)
    // public void UID1111_testExample2() throws FrameworkException {
    // /**
    // * Example of page object using annotation
    // */
    // LoginPageWithAnnotation pageWithAnno = new LoginPageWithAnnotation(driver);
    // reporter.pass("Enter email info");
    // pageWithAnno.email.setText("test@email.com");
    //
    // reporter.pass("Enter password");
    // pageWithAnno.password.setText("password");
    //
    // assertion.shouldBeEqual(pageWithAnno.email.getText(), "test@email.com");
    // /**
    // * Example of page object without using annotation
    // */
    // LoginPageWithoutAnnotation pageWithoutAnno = new
    // LoginPageWithoutAnnotation(driver);
    // pageWithoutAnno.getEmail().clear();
    // pageWithoutAnno.getEmail().setText("test2@email.com");
    // pageWithoutAnno.getPassword().setText("password");
    // assertion.shouldBeEqual(pageWithoutAnno.getEmail().getText(), "test2@email.com");
    //
    //
    // // or create your method to group actions
    // pageWithoutAnno.login("test3@email.com","password");
    // assertion.shouldBeEqual(pageWithoutAnno.getEmail().getText(), "test3@email.com");
    //
    // }

    @Test(groups = "Example", enabled = true)
    public void UID1112_testExample22() {
        reporter.pass("testExample22");
        reporter.pass("testExample22 22");
        reporter.pass("testExample22 23");
        reporter.pass("testExample22 24");
        assertion.shouldBeEqual(1, 2);
    }

}
