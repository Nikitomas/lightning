package com.enums;

public enum Component {

    SELENIUM("SELENIUM"), RESTASSURED("RESTASSURED");

    String value;

    private Component(String value) {
        this.value = value;
    }

    public String getMethodName() {
        return this.value;
    }

}
