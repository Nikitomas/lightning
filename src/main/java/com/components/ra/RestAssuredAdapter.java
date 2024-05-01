package com.components.ra;

import static com.enums.ContextConstant.API_SERVER;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.components.ComponentAdapter;
import com.components.ra.driver.DriverManager;
import com.components.ra.driver.IAuthentication;
import com.components.ra.driver.RestAssuredDriver;
import com.components.ra.implement.RestAssuredDriverFactory;
import com.control.ControlCenter;

public class RestAssuredAdapter extends ComponentAdapter {

    private static final Logger logger = LogManager.getLogger(RestAssuredAdapter.class);

    protected RestAssuredDriver driver;

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    protected DriverManager driverManager;

    public RestAssuredAdapter(Map<String, String> params) throws Exception {
        super(params);
        this.driverManager = new DriverManager();
    }

    public RestAssuredDriver getDriver() {
        return driver;
    }

    public DriverManager getDriverManager() {
        return driverManager;
    }

    public void setDriver(RestAssuredDriver driver) {
        this.driver = driver;
    }

    @Override
    public void destory() {
        this.driver = null;
    }

    @Override
    public void initilize(IAuthentication auth) throws Exception {
        try {
            for (String key : params.keySet()) {
                if (key.equalsIgnoreCase(API_SERVER)) {
                    driver = RestAssuredDriverFactory.initDriver(params.get(API_SERVER), auth);
                }
                else {
                    RestAssuredDriver driver = RestAssuredDriverFactory.initDriver(params.get(key), auth);
                    this.driverManager.setDriver(key, driver);
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
        catch (Throwable t) {
            throw new Exception("execute beforeTest method failed due to some Error", t);
        }
    }

    @Override
    public void initilize() throws Exception {
        try {
            for (String key : params.keySet()) {
                if (key.equalsIgnoreCase(API_SERVER)) {
                    driver = RestAssuredDriverFactory.initDriver(params.get(API_SERVER));
                }
                else {
                    RestAssuredDriver driver = RestAssuredDriverFactory.initDriver(params.get(key));
                    this.driverManager.setDriver(key, driver);
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
        catch (Throwable t) {
            throw new Exception("execute beforeTest method failed due to some Error", t);
        }
    }

    @Override
    public void refresh() throws Exception {

    }

}
