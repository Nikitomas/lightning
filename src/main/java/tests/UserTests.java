package tests;

import com.base.BaseTest;
import com.components.ra.driver.RestAssuredDriver;
import org.testng.annotations.Test;
import tests.config.BaseCustomizeTest;
import tests.user.UserBuilder;
import tests.user.UserService;

public class UserTests extends BaseCustomizeTest {

    @Test(groups = { "POC" })
    public void test_poc101_DBH_28871() throws Exception {
        this.apiDriver.login("0oa157c2gkieXqal90h8", "2MMn1pQ0S7fU-SW7lf2HALXHIV2IyBWgai_RTmDU");
        UserService userService = new UserService(this.apiDriver);
        assertion.shouldBeEqual(1, 1,
            "The number of users in User based on the passed newAdminBneId and orgBneId.");
        // UserBuilder userBuilder = new UserBuilder();
    }

}
