package com.reporting.ureport;

public class UReportTestFactory {

    public static UReportTest create() {
        return new UReportTest();
    }

    public static UReportTest create(String name, String desc, String[] groups, String packageLocation, int retryCount,
            UReportTest testAtClass) {
        return new UReportTest(retryCount, name, desc, groups, packageLocation, testAtClass);
    }

    public static UReportTest create(String name, String desc, String[] groups, String packageLocation,
            UReportTest testAtClass) {
        return new UReportTest(0, name, desc, groups, packageLocation, testAtClass);
    }

}
