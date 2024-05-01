package com.reporting.extent.factory;

import com.relevantcodes.extentreports.ExtentTest;
import com.reporting.extent.model.CustomExtentTest;

public class CustomExtentTestFactory {

    public static CustomExtentTest create() {
        return new CustomExtentTest(new ExtentTest("", ""), 0, "", "", null);
    }

    public static CustomExtentTest create(String name, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods, String retryName, int retryCount, CustomExtentTest testAtClass) {

        return new CustomExtentTest(
                new ExtentTest(name + retryName,
                        generateTestCaseInformation(desc, groups, packageLocation, dependsOnMethods)),
                retryCount, name, desc, testAtClass);
    }

    public static CustomExtentTest create(String name, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods, CustomExtentTest testAtClass) {

        return new CustomExtentTest(
                new ExtentTest(name, generateTestCaseInformation(desc, groups, packageLocation, dependsOnMethods)), 0,
                name, desc, testAtClass);
    }

    private static String generateTestCaseInformation(String description, String[] groups, String packageLocation,
            String[] dependsOnMethods) {
        return "<div>Groups: <span style=\"font: italic bold 12px/30px Georgia, serif;\">"
                + listToCommaSeparatedString(groups) + "</span>" + "</div><div>"
                + "Location: <span style=\"font: italic bold 12px/30px Georgia, serif;\">" + packageLocation + "</span>"
                + "</div><div>" + "dependsOnMethods: <span style=\"font: italic bold 12px/30px Georgia, serif;\">"
                + listToCommaSeparatedString(dependsOnMethods) + "</span>" + "</div>" + "Description: " + description;
    }

    private static String listToCommaSeparatedString(String[] list) {
        String cssString = "";
        if (list != null)
            for (String g : list) {
                cssString = cssString + g + ", ";
            }
        if (cssString.endsWith(", "))
            cssString = cssString.substring(0, cssString.length() - 2);
        return cssString;
    }

}
