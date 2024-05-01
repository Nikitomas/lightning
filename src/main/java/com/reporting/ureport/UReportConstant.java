package com.reporting.ureport;

public enum UReportConstant {

    UREPORT_URL("UREPORT_URL"), REPORT_USER("REPORT_USER"), REPORT_PWD("REPORT_PWD"), PRODUCT("UREPORT_PRODUCT"),
    TYPE("UREPORT_TYPE"), DEVICE("UREPORT_DEVICE"), VERSION("UREPORT_VERSION"), TEAM("UREPORT_TEAM"),
    PLATFORM("UREPORT_PLATFORM"), PLATFORM_VERSION("UREPORT_PLATFORM_VERSION"), BUILD("UREPORT_BUILD");

    String value;

    UReportConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
