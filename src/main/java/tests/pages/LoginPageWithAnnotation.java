package tests.pages;

import org.openqa.selenium.WebDriver;

import com.components.se.elements.IBaseElement;
import com.components.se.enums.Until;
import com.components.se.implement.CustomFindBy;
import com.exception.FrameworkException;

public class LoginPageWithAnnotation extends YourProjectBasePage {

    @CustomFindBy(id = "email", waitFor = Until.Visible)
    public IBaseElement email;

    @CustomFindBy(id = "pass", waitFor = Until.Visible)
    public IBaseElement password;

    @CustomFindBy(id = "u_0_h_PL", waitFor = Until.Clickable)
    public IBaseElement loginBtn;

    public LoginPageWithAnnotation(WebDriver driver) throws FrameworkException {
        super(driver);
    }

}
