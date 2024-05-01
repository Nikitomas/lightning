package com.components.se.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.components.se.elements.BasePage;
import com.components.se.implement.BaseElementDecorator;
import com.exception.FrameworkException;

public class BasePageFactory extends PageFactory {

    /**
     * Initializes page
     * @param driver:WebDriver
     * @param seleniumPageObject:BasePage
     * @throws FrameworkException throws a UIException
     */
    public static void initPage(WebDriver driver, BasePage seleniumPageObject) throws FrameworkException {
        BaseElementDecorator decorator;
        try {
            decorator = new BaseElementDecorator(new DefaultElementLocatorFactory(driver, 30), seleniumPageObject);
        }
        catch (Exception e) {
            throw new FrameworkException("Page initilization failed :", e);
        }
        PageFactory.initElements(decorator, seleniumPageObject);
    }

}
