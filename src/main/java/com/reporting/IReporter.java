package com.reporting;

import java.io.File;

public interface IReporter {

    public void info(String msg);

    public void pass(String msg);

    public void warn(String msg);

    public void fail(String msg);

    public void info(String msg, String details, File file);

    public void pass(String msg, String details, File file);

    public void warn(String msg, String details, File file);

    public void fail(String msg, String details, File file);

    public void warn(String msg, Throwable t);

    public void fail(String msg, Throwable t);

    public void warn(String msg, Throwable t, File file);

    public void fail(String msg, Throwable t, File file);

    public void startTest(String testName, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods);

    public void endReport();

    // public void endTest();

    public void endTest(String failureReason, String stackTrace);

}