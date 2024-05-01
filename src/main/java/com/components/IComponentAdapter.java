package com.components;

import com.components.ra.driver.IAuthentication;

public interface IComponentAdapter {

    public void initilize() throws Exception;

    public void initilize(IAuthentication auth) throws Exception;

    public void destory() throws Exception;

    public void refresh() throws Exception;

}
