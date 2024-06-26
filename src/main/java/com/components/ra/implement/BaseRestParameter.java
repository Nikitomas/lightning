package com.components.ra.implement;

import java.util.HashMap;
import java.util.Map;

public class BaseRestParameter {

    private Map<String, Object> header;

    private Map<String, Object> query;

    private Map<String, Object> path;

    public BaseRestParameter() {
        this.header = new HashMap<String, Object>();
        this.query = new HashMap<String, Object>();
        this.path = new HashMap<String, Object>();
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    @SuppressWarnings("unchecked")
    public <T> BaseRestParameter setHeader(T... objects) throws Exception {
        validateArguments(objects);

        for (int i = 0; i < objects.length; i += 2) {
            if (objects[i] instanceof String)
                header.put((String) objects[i], objects[i + 1]);
            else
                throw new Exception("Header key " + objects[i] + " must be string");
        }
        return this;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public BaseRestParameter setQuery(Map<String, Object> query) {
        this.query = query;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> BaseRestParameter setQuery(T... objects) throws Exception {
        validateArguments(objects);

        for (int i = 0; i < objects.length; i += 2) {
            if (objects[i] instanceof String)
                query.put((String) objects[i], objects[i + 1]);
            else
                throw new Exception("Query key " + objects[i] + " must be string");
        }
        return this;
    }

    public Map<String, Object> getPath() {
        return path;
    }

    public BaseRestParameter setPath(Map<String, Object> path) {
        this.path = path;
        return this;
    }

    public <T> BaseRestParameter setPath(@SuppressWarnings("unchecked") T... objects) throws Exception {
        validateArguments(objects);

        for (int i = 0; i < objects.length; i += 2) {
            if (objects[i] instanceof String)
                path.put((String) objects[i], objects[i + 1]);
            else
                throw new Exception("Path key " + objects[i] + " must be string");
        }
        return this;
    }

    public BaseRestParameter clear() {
        this.header.clear();
        this.query = new HashMap<String, Object>();
        this.path.clear();
        return this;
    }

    @Override
    public String toString() {
        String queryContent = query.size() == 0 ? "" : ", query : " + query;
        String pathContent = path.size() == 0 ? "" : ", path : " + path;
        return "{ header : " + header + queryContent + pathContent + "}";
    }

    private void validateArguments(Object[] objects) throws Exception {
        if (objects.length % 2 != 0) {
            throw new Exception("You cannot give odd number of element");
        }
        else if (objects.length == 0) {
            throw new Exception("You need to prepare at least two argument");
        }
    }

}
