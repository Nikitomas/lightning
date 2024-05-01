package com.base;

import com.components.ra.implement.BaseRestParameter;
import com.exception.FrameworkException;

public class BaseRequest {

    private IBuilder builder;

    private BaseRestParameter params;

    private int code;

    public BaseRequest() {
        super();
        this.params = new BaseRestParameter();
    }

    public BaseRequest(BaseRestParameter params) {
        super();
        this.params = params;
    }

    public BaseRequest(IBuilder builder, BaseRestParameter params, int code) {
        super();
        this.builder = builder;
        this.code = code;
        this.params = params;
    }

    public BaseRequest(IBuilder builder, int code) {
        super();
        this.builder = builder;
        this.code = code;
        this.params = new BaseRestParameter();
    }

    public BaseRequest(IBuilder builder) {
        super();
        this.builder = builder;
        this.params = new BaseRestParameter();
    }

    public IBuilder getBuilder() {
        return builder;
    }

    public String getPayload() throws FrameworkException {
        return builder.getPayload();
    }

    public void setBuilder(IBuilder builder) {
        this.builder = builder;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public BaseRestParameter getParams() {
        return this.params;
    }

    public void setParams(BaseRestParameter params) {
        this.params = params;
    }

}
