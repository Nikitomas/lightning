package tests.pages;

import org.openqa.selenium.WebDriver;

import com.components.se.elements.IBaseElement;
import com.components.se.enums.Until;
import com.exception.FrameworkException;

public class LoginPageWithoutAnnotation extends YourProjectBasePage {

    private String email = "email";

    private String password = "pass";

    private String loginBtn = "u_0_h_PL";

    public LoginPageWithoutAnnotation(WebDriver driver) throws FrameworkException {
        super(driver);
    }

    public IBaseElement getEmail() throws FrameworkException {
        return findElement(email, Until.Visible);
    }

    public IBaseElement getPassword() throws FrameworkException {
        return findElement(password, Until.Visible, 5000);
    }

    public IBaseElement getLoginBtn() throws FrameworkException {
        return findElement(loginBtn, Until.Clickable);
    }

    public void login(String username, String password) throws FrameworkException {
        this.getEmail().clear();
        this.getEmail().setText(username);
        this.getPassword().setText(password);
    }

}
