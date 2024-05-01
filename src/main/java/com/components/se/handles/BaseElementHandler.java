package com.components.se.handles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.components.se.elements.BasePage;
import com.components.se.enums.Until;
import com.components.se.implement.DefaultElementLocator;

public class BaseElementHandler implements InvocationHandler {

    private final ElementLocator locator;

    private final Class<?> classType;

    // for logging purpose
    private BasePage page;

    private String locatorKey;

    /**
     * Constructs a BaseElementHandler Object
     * @param interfaceType Class
     * @param classType Class
     * @param locatorKey String
     * @param locator ElementLocator
     * @param page BasePage
     * @param <T> Type for the BaseElement output
     */
    public <T> BaseElementHandler(Class<?> classType, String locatorKey, ElementLocator locator, BasePage page) {
        this.locator = locator;
        this.classType = classType;
        this.page = page;
        this.locatorKey = locatorKey;
    }

    /**
     * Execute a method and return Object
     * @param object object
     * @param method Method
     * @param objects Object[]
     * @return Object
     * @throws Throwable throws throwable
     */
    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        WebElement element;
        try {
            element = locator.findElement();
        }
        catch (NoSuchElementException e) {
            return handleFindException(method, e);
            // in case of staleElementReference, try to find the element again
        }
        catch (StaleElementReferenceException e) {
            Thread.sleep(3000);
            try {
                element = locator.findElement();
            }
            catch (Exception e1) {
                return handleFindException(method, e1);
            }
        }

        if ("getWrappedElement".equals(method.getName())) {
            return element;
        }
        if (element == null && (((DefaultElementLocator) locator).getUntil() != Until.NotPresent
                && ((DefaultElementLocator) locator).getUntil() != Until.Invisible)) {
            throw new RuntimeException(
                    "Cannot find element :[" + locatorKey + "] locator:[" + locator.toString() + "]");
        }

        Constructor<?> cons = null;
        cons = classType.getConstructor(WebDriver.class, WebElement.class, String.class, ElementLocator.class,
                String.class);
        try {
            return method.invoke(cons.newInstance(page.getDriver(), element, locatorKey, locator, page.getFootPrint()),
                    objects);
        }
        catch (InvocationTargetException e) {
            throw e.getCause(); // Unwrap the underlying exception
        }
    }

    private Object handleFindException(Method method, Exception e) throws Exception {
        if ("toString".equals(method.getName())) {
            return "Proxy element for: " + locator.toString();
        }
        else {
            throw e;
        }
    }

}
