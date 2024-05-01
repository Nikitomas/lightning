
package com.components.ra.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class APIResponse {

    static final Logger logger = LogManager.getLogger(APIResponse.class);

    private static final String BREAK_LINE = "</br>";

    protected final Response res;

    public APIResponse(Response res) {
        this.res = res;
    }

    /***
     * This method is for getting the session id from the response
     */
    public Response getResponse() {
        return res;
    }

    /**
     * This method is for getting the HTTP status code of the response.
     * @return: actualStatusCode:int
     * @throws Exception
     *
     */
    public int getStatusCode() throws Exception {
        return res.getStatusCode();
    }

    /**
     * This method is for getting the etag of the response.
     * @return: actualStatusCode:string
     * @throws Exception
     *
     */
    public String getEtag() throws Exception {
        return res.getHeader("etag");
    }

    /**
     * This method is for getting node(jsonPath) value in the request json.
     * @param jsonPath:String
     * @return Object:nodeValue
     * @throws Exception
     *
     */
    public Object getNodeValue(String jsonPath) {

        Object obj;
        try {
            obj = JsonPath.with(res.asString()).get(jsonPath);
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }
        return obj == null ? "" : obj;
    }

    /**
     * This method is for getting node values for the matching jsonPath.
     * @param jsonPath:String
     * @return list:List
     * @throws Exception
     *
     */
    public List<?> getNodeValues(String jsonPath) throws Exception {

        List<?> list = Collections.emptyList();
        try {
            String json = res.asString();
            list = JsonPath.with(json).get(jsonPath);
        }
        catch (Exception e) {
            throw e;
        }

        return list;
    }

    /**
     * This method verifies if presence of node(jsonPath) in the response json
     * @param jsonPath:String
     * @return boolean
     */
    @SuppressWarnings("finally")
    public boolean isNodePresent(String jsonPath) {
        JsonPath j = new JsonPath(res.asString());
        Boolean value = false;
        try {
            if (j.get(jsonPath).getClass().equals(ArrayList.class)) {
                List<Object> jsonList = j.getJsonObject(jsonPath);
                for (Object obj : jsonList) {
                    if (obj != null) {
                        value = true;
                        break;
                    }
                    else
                        value = false;
                }
            }
            else {
                value = (j.get(jsonPath).toString() != null);
            }
        }
        catch (Exception e) {
            logger.error(e);
        }
        finally {
            return value;
        }
    }

    /**
     * This method is for validating if response contains string .
     * @param valueToFind:String,that need to be searched in the response body
     * @return containsString:boolean
     * @throws Exception
     *
     */
    public boolean contains(String valueToFind) throws Exception {

        boolean containString = false;
        String infoMessage = "";
        try {
            String response = res.asString();
            if (StringUtils.contains(response, valueToFind) && StringUtils.containsIgnoreCase(response, valueToFind)) {
                infoMessage = "ContainsString " + BREAK_LINE + "Contains expected string in response : " + valueToFind;
                logger.info(infoMessage);
                containString = true;
            }
            else {
                infoMessage = "Containsstring " + BREAK_LINE + "Does not Contains string in response : " + valueToFind;
                logger.info(infoMessage);
            }
        }
        catch (Exception e) {
            logger.error(e);
            throw e;
        }

        return containString;
    }

}