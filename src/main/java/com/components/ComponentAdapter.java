package com.components;

import java.util.Map;

public abstract class ComponentAdapter implements IComponentAdapter {

    protected Map<String, String> params;

    public ComponentAdapter(Map<String, String> params) {
        this.params = params;
    }

}
