package com.components.se.implement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import com.components.se.elements.BaseElement;
import com.components.se.elements.BasePage;
import com.components.se.elements.IBaseElement;
import com.components.se.handles.BaseElementHandler;
import com.components.se.handles.BaseElementListHandler;

public class BaseElementDecorator implements FieldDecorator {

    protected ElementLocatorFactory factory;

    protected BasePage page;

    public BaseElementDecorator(ElementLocatorFactory factory, BasePage page) {
        this.factory = factory;
        this.page = page;
    }

    /**
     *
     */
    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if (!(IBaseElement.class.isAssignableFrom(field.getType()) || WebElement.class.isAssignableFrom(field.getType())
                || isDecoratableList(field))) {
            return null;
        }

        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        Class<?> fieldType = field.getType();
        if (IBaseElement.class.isAssignableFrom(fieldType) || WebElement.class.isAssignableFrom(fieldType)) {
            return proxyForLocator(loader, IBaseElement.class, BaseElement.class, field.getName(), locator);
        }
        else if (List.class.isAssignableFrom(fieldType)) {
            ParameterizedType fieldClassType = (ParameterizedType) field.getGenericType();
            Class<?> interfaceType = (Class<?>) fieldClassType.getActualTypeArguments()[0];

            return proxyForListLocator(loader, interfaceType, field.getName(), locator);
        }
        else {
            return null;
        }
    }

    protected boolean isDecoratableList(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        // Type erasure in Java isn't complete. Attempt to discover the generic type of
        // the list.
        Type genericType = field.getGenericType();

        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        if (field.getAnnotation(CustomFindBy.class) == null && field.getAnnotation(FindBy.class) == null
                && field.getAnnotation(FindBys.class) == null && field.getAnnotation(FindAll.class) == null) {
            return false;
        }

        return true;
    }

    protected <T> T proxyForLocator(ClassLoader loader, Class<T> baseInterface, Class<?> type, String fieldName,
            ElementLocator locator) {
        InvocationHandler handler = null;
        handler = new BaseElementHandler(type, fieldName, locator, page);
        return baseInterface.cast(Proxy.newProxyInstance(loader,
                new Class[] { baseInterface, WrapsElement.class, Locatable.class }, handler));

    }

    @SuppressWarnings("unchecked")
    protected <T> List<IBaseElement> proxyForListLocator(ClassLoader loader, Class<T> baseInterface, String fieldName,
            ElementLocator locator) {

        InvocationHandler handler = null;
        handler = new BaseElementListHandler(baseInterface, BaseElement.class, fieldName, locator, page);

        List<IBaseElement> proxy = (List<IBaseElement>) Proxy.newProxyInstance(loader, new Class[] { List.class },
                handler);
        return proxy;
    }

}
