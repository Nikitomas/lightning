package com.reporting.ureport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.components.ra.driver.RestAssuredDriver;
import com.components.ra.implement.BaseAPIService;
import com.components.ra.implement.BaseRestParameter;
import com.components.ra.json.APIResponse;
import com.control.ControlCenter;

public class UReportAPIService extends BaseAPIService {

    public static final String BUILD_PATH = "/api/build";

    public static final String BUILD_STATUS_PATH = "/api/build/status/{buildId}";

    public static final String TEST_PATH = "/api/test/multi";

    public UReportAPIService(RestAssuredDriver driver) {
        super(driver);
    }

    public String createBuild(Map<String, String> params) throws Exception {
        System.out.println("UREPORT: create build");
        String payload = "{";
        if (params.get("UREPORT_BUILD") != null) {
            payload += "\"build\": \"" + params.get("UREPORT_BUILD") + "\",";
        }
        else {
            payload += "\"build\": \"" + new SimpleDateFormat("YYYYMMddhhmmss").format(new Date()) + "\",";
        }
        if (params.get("UREPORT_DEVICE") != null) {
            payload += "\"device\": '" + params.get("UREPORT_DEVICE") + "\",";
        }
        if (params.get("UREPORT_VERSION") != null) {
            payload += "\"version\": '" + params.get("UREPORT_VERSION") + "\",";
        }
        if (params.get("UREPORT_TEAM") != null) {
            payload += "\"team\" : \"" + params.get("UREPORT_TEAM") + "\",";
        }
        if (params.get("UREPORT_PLATFORM") != null) {
            payload += "\"platform\" : \"" + params.get("UREPORT_PLATFORM") + "\",";
        }
        if (params.get("UREPORT_PLATFORM_VERSION") != null) {
            payload += "\"platfomr_version\": \"" + params.get("UREPORT_PLATFORM_VERSION") + "\",";
        }
        payload += "\"product\": \"" + params.get("UREPORT_PRODUCT") + "\",";
        payload += "\"type\": \"" + params.get("UREPORT_TYPE") + "\"";
        payload += "}";
        APIResponse res = this.driver.post(
                ControlCenter.getInstance().getParameter(UReportConstant.UREPORT_URL.getValue()) + BUILD_PATH,
                new BaseRestParameter(), payload);
        if (res.getStatusCode() == 200) {
            return res.getNodeValue("_id").toString();
        }
        else {
            return null;
        }
    }

    public void updateBuild(String buildId, String payload) throws Exception {
        System.out.println("UREPORT: update build");
        BaseRestParameter params = new BaseRestParameter();
        params.setPath("buildId", buildId);
        APIResponse res = this.driver.put(
                ControlCenter.getInstance().getParameter(UReportConstant.UREPORT_URL.getValue()) + BUILD_STATUS_PATH,
                params, payload);
    }

    public void createTests(String payload) throws Exception {
        System.out.println("UREPORT: create tests");
        this.driver.post(ControlCenter.getInstance().getParameter(UReportConstant.UREPORT_URL.getValue()) + TEST_PATH,
                new BaseRestParameter(), payload);
    }

}
