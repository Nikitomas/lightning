package com.components.ra.driver;

import java.util.HashMap;
import java.util.Map;

public class DriverManager {

    protected Map<String, RestAssuredDriver> drivers;

    public DriverManager() {
        drivers = new HashMap<String, RestAssuredDriver>();
    }

    public RestAssuredDriver getDriver(String application_name) {
        return drivers.get(application_name);
    }

    public void setDriver(String application_name, RestAssuredDriver driver) {
        drivers.put(application_name, driver);
    }

}
