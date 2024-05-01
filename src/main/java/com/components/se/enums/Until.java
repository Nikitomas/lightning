package com.components.se.enums;

public enum Until {

    Clickable("findElementByClickable"), Visible("findElementByVisible"), Present("findElementByPresence"),
    Invisible("findElementByInvisible"), NotPresent("findElementByNotPresence"),
    ElementsVisible("findElementsByPresence"), ElementsPresent("findElementsByPresence");

    String value;

    private Until(String value) {
        this.value = value;
    }

    public String getMethodName() {
        return this.value;
    }

}
