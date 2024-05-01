package com.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.components.ra.RestAssuredAdapter;
import com.components.ra.driver.DriverManager;
import com.components.ra.driver.IAuthentication;
import com.components.ra.driver.RestAssuredDriver;
import com.components.se.SeleniumAdapter;
import com.enums.Component;

public class ComponentManager {

    private static final Logger LOGGER = LogManager.getLogger(ComponentManager.class);

    protected List<ComponentAdapter> components;

    protected Map<String, ComponentAdapter> componentsMap;

    protected Map<String, String> parameters;

    public ComponentManager(Map<String, String> parameters) throws Exception {
        this.components = new ArrayList<ComponentAdapter>();
        this.componentsMap = new HashMap<String, ComponentAdapter>();
        this.parameters = parameters;
    }

    public void register(Component component) throws Exception {
        ComponentAdapter adapter;
        if (component.equals(Component.RESTASSURED)) {
            adapter = new RestAssuredAdapter(parameters);
        }
        else if (component.equals(Component.SELENIUM)) {
            adapter = new SeleniumAdapter(parameters);

        }
        else {
            throw new Exception("Cannot find Component " + component);
        }
        components.add(adapter);
        componentsMap.put(component.toString(), adapter);
        LOGGER.debug("component size: " + components.size());
    }

    public void initilizeAll() throws Exception {
        for (ComponentAdapter ta : components) {
            ta.initilize();
        }
    }

    public void initialize(Component component) throws Exception {
        if (componentsMap.containsKey(component.toString())) {
            componentsMap.get(component.toString()).initilize();
        }
    }

    public void initialize(Component component, IAuthentication auth) throws Exception {

        if (componentsMap.containsKey(component.toString())) {
            componentsMap.get(component.toString()).initilize(auth);
        }

    }

    public void destory(Component component) throws Exception {
        if (componentsMap.containsKey(component.toString())) {
            componentsMap.get(component.toString()).destory();
        }
    }

    public WebDriver getWebDriver() {
        if (componentsMap.containsKey(Component.SELENIUM.toString())) {
            return ((SeleniumAdapter) componentsMap.get(Component.SELENIUM.toString())).getDriver();
        }
        return null;
    }

    public RestAssuredDriver getRestAssuredDriver() {
        return ((RestAssuredAdapter) componentsMap.get(Component.RESTASSURED.toString())).getDriver();
    }

    public DriverManager getDriverManager() {
        return ((RestAssuredAdapter) componentsMap.get(Component.RESTASSURED.toString())).getDriverManager();
    }

}
