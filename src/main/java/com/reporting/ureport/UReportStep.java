package com.reporting.ureport;

import java.util.Date;

public class UReportStep {

    private Date timestamp;

    private String detail;

    private String status;

    public UReportStep(Date timestamp, String detail, String status) {
        super();
        this.timestamp = timestamp;
        this.detail = detail;
        this.status = status;
    }

    public String generatePayload() {
        return "{ \"timestamp\": \"" + timestamp + "\", " + " \"detail\" : \"" + detail.replaceAll("\n", "") + "\","
                + " \"status\" : \"" + status + "\"}";
    }

}
