package com.components.se;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.components.ComponentAdapter;
import com.components.ra.driver.IAuthentication;
import com.components.se.factory.WebDriverFactory;
import com.control.ControlCenter;

public class SeleniumAdapter extends ComponentAdapter {

    private static final Logger logger = LogManager.getLogger(SeleniumAdapter.class);

    protected WebDriver driver;

    protected ControlCenter controlCenter = ControlCenter.getInstance();

    public SeleniumAdapter(Map<String, String> params) throws Exception {
        super(params);

    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void destory() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public void initilize() throws Exception {
        try {
            driver = WebDriverFactory.initWebDriver(controlCenter.getParameters());
            driver.manage().window().maximize();

        }
        catch (WebDriverException e) {
            if (driver != null) {
                driver.quit();
            }
            logger.error(e);
            throw e;
        }
        catch (Exception e) {
            if (driver != null) {
                driver.quit();
            }
            logger.error(e);
            throw e;
        }
        catch (Throwable t) {
            throw new Exception("execute beforeTest method failed due to some Error", t);
        }
    }

    @Override
    public void initilize(IAuthentication auth) throws Exception {

    }

    @Override
    public void refresh() throws Exception {

    }

}
