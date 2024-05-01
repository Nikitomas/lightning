package com.components.ra.gateways;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtils {

    private RestAssuredUtils() {

    }

    public static Response post(String url, String payload, RequestSpecification spec) throws Exception {
        Response res = null;
        try {
            if (payload == null) {
                res = given().spec(spec).when().post(url);
            }
            else {
                res = given().spec(spec).body(payload).when().post(url);
            }
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response post(String url, RequestSpecification spec) throws Exception {
        Response res = null;
        try {

            res = given().spec(spec).when().post(url);

        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response get(String url, RequestSpecification spec) throws Exception {
        Response res = null;
        try {
            res = given().spec(spec).when().get(url);
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response put(String url, String payload, RequestSpecification spec) throws Exception {
        Response res = null;
        try {
            if (payload == null) {
                res = given().spec(spec).when().put(url);
            }
            else {
                res = given().spec(spec).body(payload).when().put(url);
            }
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response delete(String url, String payload, RequestSpecification spec) throws Exception {
        Response res = null;
        try {
            if (payload == null) {
                res = given().spec(spec).when().delete(url);
            }
            else {
                res = given().spec(spec).body(payload).when().delete(url);
            }
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response patch(String url, String payload, RequestSpecification spec) throws Exception {
        Response res = null;
        try {

            if (payload == null) {
                res = given().spec(spec).when().patch(url);
            }
            else {
                res = given().spec(spec).body(payload).when().patch(url);
            }
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }

    public static Response head(String url, RequestSpecification spec) throws Exception {
        Response res = null;
        try {
            res = given().spec(spec).when().head(url);
        }
        catch (Exception e) {
            throw e;
        }
        return res;
    }
}
