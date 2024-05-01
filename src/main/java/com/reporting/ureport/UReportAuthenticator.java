package com.reporting.ureport;

import static io.restassured.RestAssured.given;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.components.ra.driver.IAuthentication;
import com.components.ra.gateways.RestAssuredUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UReportAuthenticator implements IAuthentication {

    protected Map<String, String> params;

    Response res = null;

    public UReportAuthenticator(Map<String, String> params) {
        this.params = params == null ? new HashMap<String, String>() : params;
    }

    /**
     * Login at openAM
     * @param username: String
     * @param password : String
     * @return cookie in String
     * @throws Exception
     */
    @Override
    public String login(String username, String password) throws Exception {
        String uri = params.get("UREPORT_URL") + "/api/login";

        RequestSpecification spec = given().accept(ContentType.JSON).contentType(ContentType.JSON);
        res = RestAssuredUtils.post(uri, "{\n" + "      \"username\" : \"" + username + "\",\n"
                + "      \"password\" : \"" + password + "\"\n" + "    }", spec);

        if (res.getStatusCode() == 200) {
            System.out.println("Log in successfully");
            return res.header("set-cookie").split(";")[0];
        }
        else {
            System.out.println("Cannot connect to UReport server " + res.getStatusCode());
            return null;
        }
    }

    @Override
    public String login(String username, String password, RequestSpecification spec) throws Exception {
        return null;
    }

    @Override
    public String logout() throws Exception {
        return "";
    }

    @Override
    public Map<String, String> getAuthenticationHeader() {
        return Collections.singletonMap("Cookie", res.header("set-cookie").split(";")[0]);
    }

    @Override
    public RequestSpecification getAuthenticationReqSpec() {
        return RestAssured.given();
    }

}
