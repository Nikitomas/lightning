package com.components.se.implement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.AbstractAnnotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.components.se.enums.Until;
import com.components.se.gateways.BasicElementGateway;

public class DefaultElementLocator implements ElementLocator {

    static final Logger logger = LogManager.getLogger(DefaultElementLocator.class);

    private final SearchContext searchContext;

    private final boolean shouldCache;

    private final By by;

    private WebElement cachedElement;

    private List<WebElement> cachedElementList;

    private Until until;

    private final Until definedUntil;

    private int timeout;

    public DefaultElementLocator(SearchContext searchContext, Field field, int timeout) {
        this(searchContext, new BaseAnnotations(field), timeout);
    }

    /**
     * The DefaultElementLocator which find element based on the specified annotations in
     * the search context within the given timeout
     * @param searchContext SearchContext
     * @param annotations AbstractAnnotations
     * @param timeout int
     */
    public DefaultElementLocator(SearchContext searchContext, AbstractAnnotations annotations, int timeout) {
        this.searchContext = searchContext;
        this.shouldCache = annotations.isLookupCached();
        this.by = annotations.buildBy();
        // get the via method name to call the proper method
        if (annotations instanceof BaseAnnotations) {
            this.definedUntil = ((BaseAnnotations) annotations).getSyncType();
            this.until = ((BaseAnnotations) annotations).getSyncType();
            this.timeout = ((BaseAnnotations) annotations).getTimeout();
            if (this.timeout == -1) {
                this.timeout = timeout;
            }
        }
        else {
            this.until = Until.Present;
            this.definedUntil = Until.Present;
            this.timeout = timeout;
        }
    }

    /**
     * Find element
     * @return WebElement
     */
    @Override
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }

        WebElement element = null;
        String methodName = until.getMethodName();
        Method method;
        try {
            method = BasicElementGateway.class.getDeclaredMethod(methodName,
                    new Class[] { WebDriver.class, By.class, int.class });
            element = (WebElement) method.invoke(null, (WebDriver) searchContext, by, timeout);
        }
        catch (Exception e) {
            logger.debug(e);
            // We will not do anything here as, exception will be thrown from class
            // Logging and reporting will be done inside BasicPageElementHelper class.
        }

        if (shouldCache && element != null)
            cachedElement = element;

        return element;
    }

    /**
     * Finds all elements on page for given locator
     * @return List
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<WebElement> findElements() {
        if (cachedElementList != null && shouldCache) {
            return cachedElementList;
        }

        List<WebElement> elements = new ArrayList<WebElement>();
        String methodName = until.getMethodName();
        Method method;
        try {
            method = BasicElementGateway.class.getDeclaredMethod(methodName,
                    new Class[] { WebDriver.class, By.class, int.class });
            elements = (List<WebElement>) method.invoke(null, (WebDriver) searchContext, by, timeout);
        }
        catch (Exception e) {
            logger.debug(e);
            // We will not do anything here as, exception will be thrown from class
            // Logging and reporting will be done inside BasicPageElementHelper class.
        }

        if (shouldCache) {
            cachedElementList = elements;
        }

        return elements;
    }

    @Override
    public String toString() {
        return "" + by + "";
    }

    public Until getUntil() {
        return this.until;
    }

    public void setUntil(Until until) {
        this.until = until;
    }

    public void setUntilToDefault() {
        this.until = this.definedUntil;
    }

    public void changeWaitUntilState(Until newState) {
        // Changing state from Visible/Clickable to Invisible
        if ((this.until == Until.Visible || this.until == Until.Clickable) && newState == Until.Invisible) {
            this.setUntil(newState);
        }
        if ((this.until == Until.Invisible || this.until == Until.NotPresent) && newState == Until.Visible) {
            this.setUntil(newState);
        }

        if ((this.until == Until.Visible || this.until == Until.Clickable) && newState == Until.NotPresent) {
            this.setUntil(newState);
        }
        // for disable scenario
        if (this.until == Until.Clickable && newState == Until.Visible) {
            this.setUntil(newState);
        }

        // for enable scenario
        if (this.until == Until.Visible && newState == Until.Clickable) {
            this.setUntil(newState);
        }
    }

}
