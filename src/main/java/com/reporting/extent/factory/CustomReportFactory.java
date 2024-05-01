package com.reporting.extent.factory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.reporting.extent.model.CustomExtentReports;

public class CustomReportFactory {

    private static final Logger logger = LogManager.getLogger(CustomReportFactory.class);

    /**
     * Constructor of CustomReportFactory
     */
    private CustomReportFactory() {
    }

    public static CustomExtentReports createExtentReport(String file) {
        CustomExtentReports report = new CustomExtentReports(file, true);
        InetAddress iAddress = null;
        try {
            iAddress = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
        }

        String hostName = "NA";
        String canonicalHostName = "NA";

        if (iAddress != null) {
            hostName = iAddress.getHostName();
            canonicalHostName = iAddress.getCanonicalHostName();
        }

        report.addSystemInfo("Host Name", hostName);
        report.addSystemInfo("Host Name (Canonical)", canonicalHostName);

        logger.info("Host Name: " + hostName + " Address: " + canonicalHostName);

        return report;
    }

}
