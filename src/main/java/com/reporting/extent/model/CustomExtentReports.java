package com.reporting.extent.model;

import java.util.ArrayList;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class CustomExtentReports extends ExtentReports {

    public CustomExtentReports(String filePath) {
        super(filePath);
    }

    public CustomExtentReports(String filePath, boolean replaceExisting) {
        super(filePath, replaceExisting);
    }

    public synchronized void startTest(ExtentTest test) {
        if (testList == null) {
            testList = new ArrayList<ExtentTest>();
        }
        testList.add(test);
    }

}
