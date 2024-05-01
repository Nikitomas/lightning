package com.base;

import java.util.Map;

import com.exception.FrameworkException;

public interface IBuilder extends IPayload {

    public String getPayload() throws FrameworkException;

    public Map<String, Object> getPayloadAsMap() throws FrameworkException;

}
