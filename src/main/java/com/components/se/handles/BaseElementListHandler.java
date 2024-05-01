package com.components.se.handles;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.components.se.elements.BaseElement;
import com.components.se.elements.BasePage;
import com.components.se.elements.IBaseElement;

public class BaseElementListHandler implements InvocationHandler {

    private final ElementLocator locator;

    private final Class<?> classType;

    // for logging purpose
    private BasePage page;

    private String locatorKey;

    public <T> BaseElementListHandler(Class<?> interfaceType, Class<?> classType, String locatorKey,
            ElementLocator locator, BasePage page) {
        this.locator = locator;
        if (!IBaseElement.class.isAssignableFrom(interfaceType)) {
            throw new RuntimeException("interface not assignable to Element.");
        }
        this.classType = classType;
        this.page = page;
        this.locatorKey = locatorKey;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
        List<WebElement> elements;
        try {
            elements = locator.findElements();
        }
        catch (NoSuchElementException e) {
            if ("toString".equals(method.getName())) {
                return "Proxy element for: " + locator.toString();
            }
            else
                throw e;
        }

        if (elements.size() == 0) {
            throw new NoSuchElementException(locator.toString());
        }

        Constructor<?> cons = classType.getConstructor(WebDriver.class, WebElement.class, String.class, String.class,
                String.class);
        try {
            List<BaseElement> results = new ArrayList<BaseElement>();
            for (WebElement e : elements) {
                results.add((BaseElement) cons.newInstance(page.getDriver(), e, locatorKey, locator.toString(),
                        page.getFootPrint()));
            }
            return method.invoke(results, objects);
        }
        catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }

}
