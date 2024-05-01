package com.components.se.gateways;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import com.components.se.enums.LocatorType;

public class LocatorUtil {

    private static final Pattern patternXPath = Pattern.compile("^\\s*\\/\\/?.*");

    private static final Pattern patternId = Pattern.compile("^[\\w_:\\d]+$");

    /**
     * Determine locator type XPath pattern : "^\\s*\\/\\/?.*" ID pattern :
     * "^\\s*\\/\\/?.*" Any pattern does not match the above two will be considered as CSS
     * @param locator String
     * @return By
     */
    public static By determineByType(String locator) {
        if (patternId.matcher(locator.trim()).find()) {
            return By.id(locator.trim());
        }
        else if (patternXPath.matcher(locator.trim()).find()) {
            return By.xpath(locator.trim());
        }
        else {
            return By.cssSelector(locator.trim());
        }
    }

    /**
     * Determine locator type
     * @param locator String
     * @param type Locator
     * @return By
     */
    public static By determineByType(String locator, LocatorType type) {
        switch (type) {
            case ID:
                return By.id(locator);
            case XPATH:
                return By.xpath(locator);
            case CSS:
                return By.cssSelector(locator);
            case NAME:
                return By.name(locator);
            case TAG_NAME:
                return By.tagName(locator);
            case CLASS_NAME:
                return By.className(locator);
            default:
                return By.name(locator);
        }
    }

}
