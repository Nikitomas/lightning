package com.components.se.elements;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.Select;

import com.components.se.enums.LocatorType;
import com.components.se.enums.Until;
import com.components.se.gateways.BasicElementGateway;
import com.components.se.gateways.BasicPageGateway;
import com.components.se.gateways.CustomSeleniumGateway;
import com.components.se.gateways.LocatorUtil;
import com.components.se.implement.DefaultElementLocator;
import com.control.ControlCenter;
import com.enums.ContextConstant;
import com.exception.FrameworkException;
import com.reporting.ReportAdapter;

public class BaseElement implements IBaseElement {

    public static final int TIMEOUT = Integer
            .parseInt(ControlCenter.getInstance().getParameter(ContextConstant.TIMEOUT));

    protected WebElement element;

    protected final WebDriver driver;

    // for logging purpose
    public final String fieldName;

    public final ElementLocator locator;

    public final String locatorString;

    public final String navigation;

    protected ReportAdapter reporter = ControlCenter.getInstance().getReporter();

    /**
     * @param driver
     * @param element
     * @param fieldName
     * @param locator
     * @param navigation
     */
    public BaseElement(WebDriver driver, final WebElement element, String fieldName, ElementLocator locator,
            String navigation) {
        this.locator = locator;

        this.element = element;
        this.driver = driver;
        this.locatorString = locator.toString();
        this.navigation = navigation;
        this.fieldName = fieldName;
    }

    /**
     * @param driver
     * @param element
     * @param fieldName
     * @param locatorString
     * @param navigation
     */
    public BaseElement(WebDriver driver, final WebElement element, String fieldName, String locatorString,
            String navigation) {
        this.locator = null;

        this.element = element;
        this.driver = driver;
        this.locatorString = locatorString;
        this.navigation = navigation;
        this.fieldName = fieldName;
    }

    /**
     * A alternative constructor in case of IBaseElement is passed instead of WebElement
     * @param driver WebDriver
     * @param element Element on page
     * @param fieldName String
     * @param locator String
     * @param navigation String
     */
    public BaseElement(WebDriver driver, final IBaseElement element, String fieldName, ElementLocator locator,
            String navigation) {
        this.element = ((BaseElement) element).getWrappedElement();
        this.locator = locator;
        this.driver = driver;
        this.locatorString = locator.toString();
        this.navigation = navigation;
        this.fieldName = fieldName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IBaseElement> findElements(By by) throws FrameworkException {

        List<IBaseElement> webElements = new ArrayList<IBaseElement>();
        List<WebElement> temp = null;

        String infoMsg = "findElements: " + by;
        try {
            temp = element.findElements(by);
            for (WebElement e : temp) {
                webElements.add(new BaseElement(driver, e, fieldName, locatorString, navigation));
            }
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }
        return webElements;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IBaseElement> findElements(String locator, LocatorType type) throws FrameworkException {

        List<IBaseElement> webElements = new ArrayList<IBaseElement>();
        List<WebElement> temp = null;

        String infoMsg = "findElements: " + locator;
        try {
            temp = element.findElements(LocatorUtil.determineByType(locator, type));
            for (WebElement e : temp) {
                webElements.add(new BaseElement(driver, e, fieldName, this.locatorString, navigation));
            }
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }

        return webElements;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBaseElement findElement(By by) throws FrameworkException {
        WebElement webElement = null;
        String infoMsg = "findElement: " + by;
        try {
            webElement = element.findElement(by);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }

        return new BaseElement(driver, webElement, fieldName, locatorString, navigation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisplayed() {
        boolean isDisplayed = false;
        if (element != null) {
            isDisplayed = element.isDisplayed();
        }
        String infoMsg = "isDisplayed: " + isDisplayed + " [ " + fieldName + " : " + locatorString + " ]";
        reporter.info(infoMsg);

        return isDisplayed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPresent() {
        return element != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelected() {
        boolean isSelected = element.isSelected();
        String infoMsg = "isSelected: " + isSelected + " [ " + fieldName + " : " + locatorString + " ]";
        reporter.info(infoMsg);

        return isSelected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnable() {
        boolean isEnabled = element.isEnabled();
        /*
         * some button rely on the css for disable instead of disable attribute if
         * isEnable is true, we further look for css property cursor.
         *
         */
        if (isEnabled && "not-allowed".equalsIgnoreCase(element.getCssValue("cursor"))) {
            isEnabled = false;
        }
        else if (isEnabled && element.getAttribute("class").contains("disabled")) {
            isEnabled = false;
        }
        else if (isEnabled) {
            if (element.getAttribute("readOnly") != null && element.getAttribute("readOnly").equals("true")) {
                isEnabled = false;
            }
        }

        reporter.info("isEnabled: " + isEnabled + " [ " + fieldName + " : " + locatorString + " ]");
        return isEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForEnable() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), TIMEOUT);
            BasicElementGateway.waitForElementToEnable(driver, this.element, TIMEOUT);
        }
        else {
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.Clickable);
            }
            BasicElementGateway.waitForElementToEnable(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForEnabled:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForClickable() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), TIMEOUT);
            BasicElementGateway.waitForElementToClickable(driver, this.element, TIMEOUT);
        }
        else {
            BasicElementGateway.waitForElementToClickable(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForEnabled:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForDisable() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), TIMEOUT);
            BasicElementGateway.waitForElementToDisable(driver, this.element, TIMEOUT);
        }
        else {
            if (locator instanceof DefaultElementLocator) {
                // we will use visible to indicate the next state change.
                // we assume that if a element is disabled, it should be visible to be
                // located
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.Visible);
            }
            BasicElementGateway.waitForElementToDisable(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForDisable:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForVisible() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), TIMEOUT);
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.Visible);
            }
            BasicElementGateway.waitForElementToDisplay(driver, this.element, TIMEOUT);
        }
        else {
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.Visible);
            }
            BasicElementGateway.waitForElementToDisplay(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForVisible:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForInvisible() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), TIMEOUT);
            BasicElementGateway.waitForElementToDisappear(driver, this.element, TIMEOUT);
        }
        else {
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.Invisible);
            }
            BasicElementGateway.waitForElementToDisappear(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForInvisible:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForNotPresent() throws FrameworkException {
        if (this.element == null) {
            this.element = BasicElementGateway.findElementByNotPresence(driver,
                    LocatorUtil.determineByType(locatorString.split(":")[1]), 1);
        }
        else {
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).changeWaitUntilState(Until.NotPresent);
            }
            BasicElementGateway.waitForElementNotPresent(driver, this.element, TIMEOUT);
        }
        reporter.info("waitForNotPresent:" + " [ " + fieldName + " : " + locatorString + " ]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void click() throws FrameworkException {
        String infoMsg = "click: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            element.click();
            reporter.info(infoMsg);
        }
        catch (WebDriverException e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clickAsJScript() throws FrameworkException {
        String infoMsg = "clickAsJScript: " + "[ " + fieldName + " ]";
        try {
            CustomSeleniumGateway.clickOnInvisible(driver, element);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rightClick() throws FrameworkException {
        String infoMsg = "rightClick: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            new Actions(driver).moveToElement(element).contextClick(element).perform();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doubleClick() throws FrameworkException {
        String infoMsg = "doubleClick: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            new Actions(driver).doubleClick(element).perform();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendText(CharSequence... keysToSend) throws FrameworkException {
        String infoMsg = "appendText: " + asString(keysToSend) + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            String preText = getText();
            element.clear();
            element.sendKeys(preText + asString(keysToSend));
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(CharSequence... text) throws FrameworkException {
        String infoMsg = "setText: " + asString(text) + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            element.sendKeys(text);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCheck(boolean check) throws FrameworkException {

        String infoMsg = "setCheck: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            if (check != element.isSelected())
                element.click();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() throws FrameworkException {
        String infoMsg = "clear: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            element.clear();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagName() {
        reporter.info("getTagName:  [ " + fieldName + " : " + locatorString + " ]");
        return element.getTagName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttribute(String name) {
        String attributeValue = null;
        attributeValue = element.getAttribute(name);
        String infoMsg = "getAttribute: " + name + " [ " + fieldName + " : " + locatorString + " ] ";
        reporter.info(infoMsg);

        return attributeValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() throws FrameworkException {
        String text = null;
        String infoMsg = "getText: %s" + " [ " + fieldName + " : " + locatorString.toString() + " ]";
        if (element.getTagName().equalsIgnoreCase("input") || element.getTagName().equalsIgnoreCase("textarea")) {
            text = getAttribute("value");
        }
        else {
            try {
                text = element.getText();
            }
            catch (Exception e) {
                throw new FrameworkException(String.format(infoMsg, text), e);
            }
        }
        reporter.info(String.format(infoMsg, text));
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getLocation() {
        reporter.info("getLocation:  [ " + fieldName + " : " + locatorString + " ]");
        return element.getLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getSize() {
        reporter.info("getSize: [ " + fieldName + " : " + locatorString + " ]");
        return element.getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getRect() {
        reporter.info("getRect: [ " + fieldName + " : " + locatorString + " ]");
        return element.getRect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssValue(String propertyName) {
        reporter.info("getCssValue: " + propertyName + " [ " + fieldName + " : " + locatorString + " ]");
        return element.getCssValue(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebElement getWrappedElement() {
        return this.element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollIntoView(boolean isAlignTop) throws FrameworkException {
        BasicPageGateway.scrollIntoView(driver, element, isAlignTop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollIntoView() throws FrameworkException {
        BasicPageGateway.scrollIntoView(driver, element, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeScript(String javascriptToExecute, Object... parameters) throws FrameworkException {
        String infoMsg = "executeScript: " + javascriptToExecute + " with parameter: " + parameters;
        Object retValue = null;
        try {
            retValue = ((JavascriptExecutor) driver).executeScript(javascriptToExecute, element, parameters);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }
        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hover() throws FrameworkException {
        Actions builder = null;
        String infoMsg = "hover: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            builder = new Actions(driver);
            builder.moveToElement(element).build().perform();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            try {
                builder.moveToElement(element).build().perform();
            }
            catch (Exception e1) {
                reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
                throw new FrameworkException(infoMsg, e1);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hover(int xOffset, int yOffset) throws FrameworkException {
        Actions builder = null;
        String infoMsg = "hover: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            builder = new Actions(driver);
            builder.moveToElement(element, xOffset, yOffset).build().perform();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            try {
                builder.moveToElement(element, xOffset, yOffset).build().perform();
            }
            catch (Exception e1) {
                reporter.fail(infoMsg, e1, BasicPageGateway.saveAsScreenShot(driver));
                throw new FrameworkException(infoMsg, e1);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void highlight() throws FrameworkException {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "border: 5px solid red;");
        }
        catch (Exception e) {
            reporter.fail("Highlight fail", e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException("Highlight fail", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragTo(IBaseElement target) throws FrameworkException {
        String infoMsg = "Drag and drop: Drag element with locator [" + locatorString + "] to element";
        try {
            if (target instanceof Proxy) {
                WebElement elt = (WebElement) Proxy.getInvocationHandler(target).invoke(target,
                        BaseElement.class.getMethod("getWrappedElement", new Class[] {}), null);
                dragAndDrop(driver, this.getWrappedElement(), elt);

            }
            else {
                dragAndDrop(driver, this.getWrappedElement(), ((BaseElement) target).getWrappedElement());
            }

            reporter.info(infoMsg);
        }
        catch (Throwable e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragTo(Point point) throws FrameworkException {
        String infoMsg = "Drag and drop: Drag element with locator [" + locatorString + "] to element";
        try {
            Actions act = new Actions(driver);
            act.moveToElement(this.getWrappedElement()).perform();
            act.dragAndDropBy(this.getWrappedElement(), point.getX(), point.getY()).build().perform();
            reporter.info(infoMsg);
        }
        catch (Throwable e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }

    }

    private void dragAndDrop(WebDriver driver, WebElement source, WebElement target) throws FrameworkException {
        String infoMsg = "Drag and Drop: element";
        try {
            Actions act = new Actions(driver);
            act.moveToElement(source).perform();
            act.clickAndHold(source).moveToElement(target).release(target).build().perform();
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e, BasicPageGateway.saveAsScreenShot(driver));
            throw new FrameworkException(infoMsg, e);
        }
    }

    private String asString(CharSequence... charSequence) {
        String text = charSequence == null ? null : "";
        if (charSequence != null)
            for (int i = 0; i < charSequence.length; i++) {
                text = text + charSequence[i];
            }
        return text;
    }

    /**
     * Select
     */

    /**
     * Get Select drop down object
     * @return Select
     */
    private Select getSelect() {
        String infoMsg = "getSelect: " + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            element.getTagName();
        }
        catch (StaleElementReferenceException e) {
            if (locator instanceof DefaultElementLocator) {
                ((DefaultElementLocator) locator).findElement();
            }
        }
        Select select = new Select(element);
        reporter.info(infoMsg);
        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectedItemValue() throws FrameworkException {
        String text = null;
        String infoMsg = "getSelectedItemValue:" + " [ " + locatorString + " :   ]";
        try {
            text = getSelect().getFirstSelectedOption().getText();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectByValue(String value) throws FrameworkException {
        String infoMsg = "select: " + value + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().selectByValue(value);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectByIndex(int index) throws FrameworkException {
        String infoMsg = "select: " + index + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().selectByIndex(index);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectByText(String text) throws FrameworkException {
        String infoMsg = "selectByVisibleText: " + text + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().selectByVisibleText(text);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deselectByValue(String value) throws FrameworkException {
        String infoMsg = "deselect: " + value + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().deselectByValue(value);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deselectByIndex(int index) throws FrameworkException {
        String infoMsg = "deselect: " + index + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().deselectByIndex(index);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deselectByText(String text) throws FrameworkException {
        String infoMsg = "deselectByVisibleText: " + text + " [ " + fieldName + " : " + locatorString + " ]";
        try {
            getSelect().deselectByVisibleText(text);
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBaseElement getSelectedItem() throws FrameworkException {
        WebElement webElement = null;
        String infoMsg = "getSelectedItem: " + " [ " + fieldName + " : " + locator + " ]";
        try {
            webElement = getSelect().getFirstSelectedOption();
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }
        return new BaseElement(driver, webElement, fieldName, locator, navigation);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public List<IBaseElement> getSelectedItems() throws FrameworkException {
        List<IBaseElement> rs = new ArrayList<IBaseElement>();
        String infoMsg = "getSelectedItem: " + " [ " + fieldName + " : " + locator + " ]";
        try {
            List<WebElement> webElements = getSelect().getAllSelectedOptions();
            for (WebElement el : webElements) {
                rs.add(new BaseElement(driver, el, fieldName, locator, navigation));
            }
            reporter.info(infoMsg);
        }
        catch (Exception e) {
            reporter.fail(infoMsg, e);
            throw new FrameworkException(infoMsg, e);
        }

        return rs;
    }

}
