package com.reporting.console;

import java.io.File;

import com.reporting.IReporter;

public class ReporterConsole implements IReporter {

    public ReporterConsole() {
    }

    @Override
    public void info(String msg) {
        System.out.println(msg.replaceAll("</br>", ""));
    }

    @Override
    public void pass(String msg) {
        this.info(msg.replaceAll("</br>", ""));
    }

    @Override
    public void warn(String msg) {
        System.out.println(msg.replaceAll("</br>", ""));
    }

    @Override
    public void fail(String msg) {
        System.err.println(msg.replaceAll("</br>", ""));
    }

    @Override
    public void info(String msg, String details, File file) {
        System.out.println(msg.replaceAll("</br>", "") + "\n" + details);
    }

    @Override
    public void warn(String msg, String details, File file) {
        System.out.println(msg.replaceAll("</br>", "") + "\n" + details);
    }

    @Override
    public void pass(String msg, String details, File file) {
        System.out.println(msg.replaceAll("</br>", "") + "\n" + details);
    }

    @Override
    public void fail(String msg, String details, File file) {
        System.err.println(msg.replaceAll("</br>", "") + "\n" + details);
    }

    @Override
    public void warn(String msg, Throwable t) {
        System.out.println(msg.replaceAll("</br>", ""));
        t.printStackTrace();
    }

    @Override
    public void fail(String msg, Throwable t) {
        System.err.println(msg.replaceAll("</br>", ""));
        t.printStackTrace();
    }

    @Override
    public void warn(String msg, Throwable t, File file) {
        System.out.println(msg.replaceAll("</br>", ""));
        t.printStackTrace();
    }

    @Override
    public void fail(String msg, Throwable t, File file) {
        System.err.println(msg.replaceAll("</br>", ""));
        t.printStackTrace();
    }

    @Override
    public void startTest(String testName, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods) {
        /**
         * Won't Implement
         */
    }

    @Override
    public void endReport() {
        /**
         * Won't Implement
         */
    }

    @Override
    public void endTest(String failureReason, String stackTrace) {
        /**
         * Won't Implement
         */
    }

}
