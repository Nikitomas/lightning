package com.components.se.gateways;

import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.control.ControlCenter;
import com.exception.FrameworkException;
import com.reporting.ReportAdapter;

public class BasicBrowserHelper {

    protected static ReportAdapter reporter = ControlCenter.getInstance().getReporter();

    private BasicBrowserHelper() {
    }

    /**
     * Navigate back from current URL
     * @param driver WebDriver
     */
    public static void navigateBack(WebDriver driver) {
        driver.navigate().back();
        reporter.info("navigateBack: " + driver.getCurrentUrl());

    }

    /**
     * Navigate forward from current URL
     * @param driver WebDriver
     */
    public static void navigateForward(WebDriver driver) {
        driver.navigate().forward();
        reporter.info("navigateForward: " + driver.getCurrentUrl());
    }

    /**
     * Refresh current URL
     * @param driver WebDriver
     */
    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
        reporter.info("refreshPage: " + driver.getCurrentUrl());
    }

    /**
     * Find cookie's value corresponding to given cookie name
     * @param driver WebDriver
     * @param cookieName String
     * @return Cookie's value corresponding to given cookie name, <br>
     * null<br>
     * if cookie with given cookie name is not present
     */
    public static String getCookieValueByName(WebDriver driver, String cookieName) {
        String cookieValue = null;
        cookieValue = driver.manage().getCookieNamed(cookieName).getValue();
        reporter.info("getCookieValueByName: " + cookieName);
        return cookieValue;
    }

    /**
     * Store current window handle
     * @param driver WebDriver
     * @return String
     */
    public static String getCurrentWindowHandle(WebDriver driver) {
        String winHandle = driver.getWindowHandle();
        reporter.info("getCurrentWindowHandle: [" + winHandle + "]");
        return driver.getWindowHandle();
    }

    /**
     * Switch to window with given window handle
     * @param driver WebDriver
     * @return WindowHandle
     * @throws FrameworkException
     */
    public static String openWindow(WebDriver driver) throws FrameworkException {
        String infoMsg = "Open Window ";
        // handles before new open widonws
        Set<String> beforeWinHandles = driver.getWindowHandles();
        Set<String> afterWinHandles;

        ((JavascriptExecutor) driver).executeScript("window.open();");

        afterWinHandles = driver.getWindowHandles();
        // remove all the difference to leave only the newly opened window handle
        afterWinHandles.removeAll(beforeWinHandles);

        reporter.info(infoMsg);

        if (afterWinHandles.isEmpty()) {
            return null;
        }
        else {
            return (String) afterWinHandles.toArray()[0];
        }
    }

    /**
     * Switch to window with given window handle
     * @param driver WebDriver
     * @param windowHandle String
     * @throws FrameworkException
     */
    public static void closeWindow(WebDriver driver, String windowHandle) throws FrameworkException {
        String infoMsg = "Close Window: ";
        if (driver.getWindowHandles().contains(windowHandle)) {
            try {
                driver.close();
            }
            catch (Exception e) {
                reporter.fail(infoMsg, e);
                throw new FrameworkException(infoMsg, e);
            }
        }
        else {
            infoMsg = infoMsg + "Cannot find the window [" + windowHandle + "] which you are trying to close.";
            reporter.fail(infoMsg);
        }
    }

    /**
     * Switch to window with given window handle
     * @param driver WebDriver
     * @param windowHandle String
     * @throws FrameworkException
     */
    public static void switchToWindow(WebDriver driver, String windowHandle) throws FrameworkException {
        String infoMsg = "switchToWindow: ";
        if (driver.getWindowHandles().contains(windowHandle)) {
            try {
                driver.switchTo().window(windowHandle);
            }
            catch (Exception e) {
                reporter.fail(infoMsg, e);
                throw new FrameworkException(infoMsg, e);
            }
        }
        else {
            infoMsg = infoMsg + "Cannot find the window [" + windowHandle + "] which you are trying to switch to";
            reporter.fail(infoMsg);
        }
    }

    /**
     * Get alert object
     * @param driver WebDriver
     * @return Alert
     * @throws FrameworkException
     */
    public static Alert getAlert(WebDriver driver) throws FrameworkException {
        String infoMsg = "getAlert: ";
        try {
            Alert alert = driver.switchTo().alert();
            return alert;
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }
    }

    /**
     * Get Page title
     * @param driver WebDriver
     * @return String
     */
    public static String getTitle(WebDriver driver) {
        String title = null;
        title = driver.getTitle();
        reporter.info("getTitle: " + title);
        return title;
    }

    /**
     * Get Page url
     * @param driver WebDriver
     * @return String
     */
    public static String getPageUrl(WebDriver driver) {
        String title = null;
        title = driver.getCurrentUrl();
        reporter.info("getPageUrl: " + title);
        return title;
    }

}
