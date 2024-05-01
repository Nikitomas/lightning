package com.components.ra.implement;

import com.components.ra.driver.RestAssuredDriver;
import com.control.ControlCenter;
import com.exception.FrameworkException;

public abstract class BaseAPIService {

    public static final String BASE_PATH = "";

    protected RestAssuredDriver driver;

    public BaseAPIService() throws FrameworkException {
        if (ControlCenter.getInstance().getComponentManager().getRestAssuredDriver() == null) {
            throw new FrameworkException("You need to instantiate rest assured driver first");
        }
        this.driver = ControlCenter.getInstance().getComponentManager().getRestAssuredDriver();
    }

    public BaseAPIService(RestAssuredDriver driver) {
        this.driver = driver;
    }

}