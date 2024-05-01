package com.components.se.factory;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import com.components.se.implement.DefaultElementLocator;

public class DefaultElementLocatorFactory implements ElementLocatorFactory {

    private final SearchContext searchContext;

    private int timeout;

    final static Logger logger = LogManager.getLogger(DefaultElementLocatorFactory.class);

    /**
     * Constructs a DefaultElementLocatorFactory Object via searchContext and timeout with
     * the searchContext and timeout.
     * @param searchContext:SearchContext
     * @param timeout:int
     */
    public DefaultElementLocatorFactory(SearchContext searchContext, int timeout) {
        this.searchContext = searchContext;
        this.timeout = timeout;
    }

    /**
     * Create locator with field
     * @param field:Field
     * @return ElementLocator
     */
    public ElementLocator createLocator(Field field) {
        ElementLocator elementLocator = new DefaultElementLocator(searchContext, field, timeout);
        return elementLocator;
    }

}
