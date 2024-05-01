package com.components.ra.driver;

import java.util.Map;

import io.restassured.specification.RequestSpecification;

public interface IAuthentication {

    public String login(String username, String password) throws Exception;

    public String login(String username, String password, RequestSpecification spec) throws Exception;

    public String logout() throws Exception;

    public RequestSpecification getAuthenticationReqSpec();

    public Map<String, String> getAuthenticationHeader();

}
