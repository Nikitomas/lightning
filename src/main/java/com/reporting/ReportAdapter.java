package com.reporting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportAdapter implements IReporter {

    private List<IReporter> reporters;

    public ReportAdapter() {
        reporters = new ArrayList<IReporter>();
    }

    public void addReporter(IReporter reporter) {
        this.reporters.add(reporter);
    }

    @Override
    public void pass(String msg) {
        for (IReporter r : reporters) {
            r.pass(msg);
        }
    }

    @Override
    public void pass(String msg, String details, File file) {
        for (IReporter r : reporters) {
            r.pass(msg, details, file);
        }
    }

    @Override
    public void info(String msg) {
        for (IReporter r : reporters) {
            r.info(msg);
        }
    }

    @Override
    public void warn(String msg) {
        for (IReporter r : reporters) {
            r.warn(msg);
        }
    }

    @Override
    public void fail(String msg) {
        for (IReporter r : reporters) {
            r.fail(msg);
        }
    }

    @Override
    public void info(String msg, String details, File file) {
        for (IReporter r : reporters) {
            r.info(msg, details, file);
        }
    }

    @Override
    public void warn(String msg, String details, File file) {
        for (IReporter r : reporters) {
            r.warn(msg, details, file);
        }
    }

    @Override
    public void fail(String msg, String details, File file) {
        for (IReporter r : reporters) {
            r.fail(msg, details, file);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        for (IReporter r : reporters) {
            r.warn(msg, t);
        }
    }

    @Override
    public void fail(String msg, Throwable t) {
        for (IReporter r : reporters) {
            r.fail(msg, t);
        }
    }

    @Override
    public void warn(String msg, Throwable t, File file) {
        for (IReporter r : reporters) {
            r.warn(msg, t, file);
        }
    }

    @Override
    public void fail(String msg, Throwable t, File file) {
        for (IReporter r : reporters) {
            r.fail(msg, t, file);
        }
    }

    @Override
    public void startTest(String testName, String desc, String[] groups, String packageLocation,
            String[] dependsOnMethods) {
        for (IReporter r : reporters) {
            r.startTest(testName, desc, groups, packageLocation, dependsOnMethods);
        }
    }

    @Override
    public void endReport() {
        for (IReporter r : reporters) {
            r.endReport();
        }
    }

    @Override
    public void endTest(String failure, String stackTrace) {
        for (IReporter r : reporters) {
            r.endTest(failure, stackTrace);
        }
    }

}
