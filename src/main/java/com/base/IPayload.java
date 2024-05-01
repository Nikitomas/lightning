package com.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.exception.FrameworkException;

public interface IPayload {

    static final String QUOTE = "\"";

    /**
     * Methods that build the map
     */
    default <T> Map<String, Object> map(T... objects) throws FrameworkException {
        if (objects.length % 2 != 0) {
            throw new FrameworkException("You cannot give odd number of element");
        }
        else if (objects.length == 0) {
            throw new FrameworkException("You need to prepare at least two argument");
        }

        Map<String, Object> m = new LinkedHashMap<String, Object>();
        for (int i = 0; i < objects.length; i += 2) {
            if (objects[i] instanceof String)
                m.put((String) objects[i], objects[i + 1]);
            else
                throw new FrameworkException("Key " + objects[i] + " must be string");
        }
        return m;
    }

    /**
     * Methods that build the list
     */
    default List<Object> list(Object... objects) {
        return Arrays.asList(objects);
    }

    /**
     * Methods that build the payload string
     */
    default String build(Map<String, Object> dataMap) throws FrameworkException {
        try {
            return toJson(clearKeys(dataMap));
        }
        catch (Exception e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    /**
     * Methods that build the payload string
     */
    default String build(List<Object> dataList) throws FrameworkException {
        try {
            List<String> results = new ArrayList<String>();
            for (Object data : dataList) {
                if (data instanceof Map) {
                    results.add(toJson(clearKeys((Map<String, Object>) data)));
                }
                else {
                    results.add(genernateValueNode(data));
                }
            }
            return results.stream().collect(Collectors.joining(",", "[", "]"));
        }
        catch (Exception e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> clearKeys(Map<String, Object> map) throws FrameworkException {
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        for (String key : map.keySet()) {
            if (map.get(key) instanceof Map) {
                Map<String, Object> temp = clearKeys((Map<String, Object>) map.get(key));
                if (temp != null) {
                    newMap.put(key, temp);
                }
            }
            else if (map.get(key) instanceof List) {
                if (((List<?>) map.get(key)).size() == 0) {
                    continue;
                }
                if (((List<?>) map.get(key)).get(0) instanceof Map) {
                    List<Map<String, Object>> tempList = traverseList(map, key);
                    if (!tempList.isEmpty())
                        newMap.put(key, tempList);
                }
                else
                    newMap.put(key, map.get(key));
            }
            else {
                if (map.get(key) == null || map.get(key).toString().isEmpty()) {
                    continue;
                }
                newMap.put(key, getProperValue(map.get(key)));
            }
        }
        return newMap.size() == 0 ? null : newMap;
    }

    static Object getProperValue(Object value) {
        if ("\"<null>\"".equalsIgnoreCase(value.toString())) {
            return null;
        }
        else if ("\"<blank>\"".equalsIgnoreCase(value.toString())) {
            return "";
        }
        else {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> traverseList(Map<String, Object> map, String key) throws FrameworkException {
        List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        for (Object o : (List<?>) map.get(key)) {
            Map<String, Object> temp = clearKeys((Map<String, Object>) o);
            if (temp != null) {
                tempList.add(temp);
            }
        }
        return tempList;
    }

    @SuppressWarnings("unchecked")
    static String toJson(Map<String, Object> dataMap) throws FrameworkException {
        List<String> resultList = new ArrayList<String>();

        for (Entry<String, Object> entry : dataMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List || value instanceof Object[]) {
                List<String> temp = new ArrayList<String>();
                for (Object o : value instanceof Object[] ? Arrays.asList(value) : (List<?>) value) {
                    if (o instanceof Map) {
                        temp.add(toJson((Map<String, Object>) o));
                    }
                    else {
                        temp.add(genernateValueNode(o));
                    }
                }
                resultList.add(QUOTE + entry.getKey() + QUOTE + " : "
                        + temp.stream().collect(Collectors.joining(",", "[", "]")));
            }
            else if (value instanceof Map) {
                if (((Map<String, Object>) value).isEmpty()) {
                    resultList.add(QUOTE + entry.getKey() + QUOTE + " : {}");
                }
                else {
                    resultList.add(QUOTE + entry.getKey() + QUOTE + " : " + toJson((Map<String, Object>) value));
                }
            }
            else {
                resultList.add(genernateValueNode(entry.getKey(), value));
            }
        }
        return resultList.isEmpty() ? "" : resultList.stream().collect(Collectors.joining(",", "{", "}"));
    }

    static String genernateValueNode(Object value) throws FrameworkException {
        String rs = null;
        if (value instanceof String) {
            // just in case the string is already another pay load from another request
            if (((String) value).trim().startsWith("{") && ((String) value).trim().endsWith("}"))
                rs = (String) value;
            else {
                if (!((String) value).isEmpty())
                    rs = QUOTE + value + QUOTE;
                else
                    rs = (String) value;
            }
        }
        else {
            if (value != null) {
                rs = value.toString();
            }
        }
        return rs;
    }

    static String genernateValueNode(String key, Object value) throws FrameworkException {
        if (value instanceof String) {
            return QUOTE + key + QUOTE + " : " + QUOTE + value + QUOTE;
        }
        else {
            return QUOTE + key + QUOTE + " : " + value;
        }
    }

}
