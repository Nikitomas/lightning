package tests.services;

import com.components.ra.driver.RestAssuredDriver;

public class BaseAPIService {

    protected RestAssuredDriver driver;

    public BaseAPIService(RestAssuredDriver driver) {
        this.setDriver(driver);
    }

    public RestAssuredDriver getDriver() {
        return driver;
    }

    public void setDriver(RestAssuredDriver driver) {
        this.driver = driver;
    }

}
