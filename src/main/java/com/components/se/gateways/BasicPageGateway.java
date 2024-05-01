package com.components.se.gateways;

import java.io.File;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.log4testng.Logger;

import com.exception.FrameworkException;

public class BasicPageGateway {

    static final Logger logger = Logger.getLogger(BasicPageGateway.class);

    private BasicPageGateway() {
    }

    /**
     * Scroll to the element. Brings element into view
     * @param driver WebDriver
     * @param elt WebElement
     * @param isAlignTop
     */
    public static void scrollIntoView(WebDriver driver, WebElement elt, boolean isAlignTop) {
        if (isAlignTop) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elt);
        }
        else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", elt);
        }
    }

    /**
     * Capture screenshot as a file.
     * @param driver WebDriver
     * @return File. screenshot file
     */
    public static File saveAsScreenShot(WebDriver driver) {
        File file = null;
        if (driver != null) {
            try {
                file = CustomSeleniumGateway.takeScreenShot(driver);
                logger.info("saveAsScreenShot: ");
            }
            catch (Exception e) {
                logger.error("saveAsScreenShot: ", e);
            }
        }
        return file;
    }

    /**
     * Wait for frame until it is present in the DOM and switch to it
     * @param driver WebDriver driver
     * @param locator locator defined in the properties file specific to the current page
     * for the the driver to find the element based on
     * @param timeout:int Timeout either from user or context file parameter
     * @throws FrameworkException
     */
    public static void waitForFrameToLoadAndSwitch(WebDriver driver, String locator, int timeout)
            throws FrameworkException {
        try {
            CustomSeleniumGateway.frameToBeAvailableAndSwitchToIt(driver, LocatorUtil.determineByType(locator),
                    timeout);
        }
        catch (Exception e) {
            throw new FrameworkException("waitForFrameToLoadAndSwitch: " + "[ " + locator + " ]", e);
        }
    }

    /**
     * Wait for page to be loaded based on the HTML document.readyState attributes
     * @param driver: SeleniumWebDriver instance
     * @param timeout int
     * @throws FrameworkException
     */
    public static void waitForPageToLoad(WebDriver driver, int timeout) throws FrameworkException {
        CustomSeleniumGateway.waitForDocumentStateToBeComplete(driver, timeout);
    }

    /**
     * Wait until the the given locator element goes invisible on the page.
     * @param driver SeleniumWebDriver instance
     * @param locator locator defined in the properties file specific to the current page
     * for the the driver to find the element based on
     * @param timeout:int Timeout either from user or context file parameter
     * @throws FrameworkException
     *
     */
    public static void waitForContentToLoad(WebDriver driver, String locator, int timeout) throws FrameworkException {

        try {
            CustomSeleniumGateway.waitUntilElementIsInvisible(driver, LocatorUtil.determineByType(locator), timeout);
        }
        catch (Exception e) {
            throw new FrameworkException("waitForContentToLoad: " + "[ " + locator + " ]", e);
        }
    }

}