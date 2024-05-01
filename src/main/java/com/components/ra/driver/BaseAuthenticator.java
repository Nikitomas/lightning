package com.components.ra.driver;

import com.components.ra.gateways.RestAssuredUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseAuthenticator implements IAuthentication {

    protected Map<String, String> params;

    Response res = null;

    public BaseAuthenticator(Map<String, String> params) {
        this.params = params == null ? new HashMap<String, String>() : params;
    }

    @Override
    public String login(String username, String password) throws Exception {
        String encodedString = Base64.getEncoder().encodeToString(username.concat(":" + password).getBytes());
        String uri = params.get("AUTH_URL");
        RequestSpecification spec = given().accept(ContentType.JSON).contentType("application/x-www-form-urlencoded")
                .formParam("scope", "bne-services:subscription:create-user-subscription")
                .formParam("grant_type", "client_credentials").header("Authorization", "Basic " + encodedString);
        // System.out.println("Logging in to user creation token post");
        res = RestAssuredUtils.post(uri, spec);

        if (res.getStatusCode() == 200) {
            System.out.println("Log in successfully");
            // System.out.println(res.asString());
            return JsonPath.with(res.asString()).get("access_token");
        }
        else {
            System.out.println("Fail to authorize: " + res.getStatusCode());
            System.out.println(res.asString());

            return null;
        }

        // return "";

    }

    @Override
    public String login(String username, String password, RequestSpecification spec) throws Exception {
        String encodedString = Base64.getEncoder().encodeToString(username.concat(":" + password).getBytes());
        String uri = params.get("AUTH_URL");
        spec = spec.accept(ContentType.JSON).contentType("application/x-www-form-urlencoded")
            .formParam("grant_type", "client_credentials").header("Authorization", "Basic " + encodedString);
        // System.out.println("Logging in to user creation token post");
        res = RestAssuredUtils.post(uri, spec);

        if (res.getStatusCode() == 200) {
            System.out.println("Log in successfully");
            // System.out.println(res.asString());
            return JsonPath.with(res.asString()).get("access_token");
        }
        else {
            System.out.println("Fail to authorize: " + res.getStatusCode());
            System.out.println(res.asString());

            return null;
        }
    }

    @Override
    public String logout() throws Exception {
        return null;
    }

    @Override
    public RequestSpecification getAuthenticationReqSpec() {

        return RestAssured.given();
    }

    @Override
    public Map<String, String> getAuthenticationHeader() {
        return Collections.singletonMap("Authorization", "Bearer " + JsonPath.with(res.asString()).get("access_token"));
    }

}
