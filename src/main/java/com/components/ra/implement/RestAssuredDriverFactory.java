package com.components.ra.implement;

import com.components.ra.driver.IAuthentication;
import com.components.ra.driver.RestAssuredDriver;

import io.restassured.RestAssured;

public class RestAssuredDriverFactory {

    public static RestAssuredDriver initDriver(String baseURL, IAuthentication auth) {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.urlEncodingEnabled = true;
        // instantiate driver
        return new RestAssuredDriver(baseURL, auth);
    }

    public static RestAssuredDriver initDriver(String baseURL) {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.urlEncodingEnabled = true;
        // instantiate driver
        return new RestAssuredDriver(baseURL);
    }

}
