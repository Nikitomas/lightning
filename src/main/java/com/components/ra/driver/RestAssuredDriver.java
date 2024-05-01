package com.components.ra.driver;

import static io.restassured.RestAssured.given;

import java.io.File;

import com.components.ra.Operations;
import com.components.ra.gateways.RestAssuredUtils;
import com.components.ra.implement.BaseRestParameter;
import com.components.ra.json.APIResponse;
import com.control.ControlCenter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reporting.ReportAdapter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredDriver {

    protected IAuthentication auth;

    private static final String CHARSET_UTF_8 = "charset=UTF-8";

    private static final String HTTP_INFO_STYLE = "<span style=\"font: bold 12px/30px Georgia, serif;margin-right:5px\" >";

    private static final String BREAK_LINE = "</br>";

    private static ReportAdapter reporter = ControlCenter.getInstance().getReporter();

    private String baseURL;

    public RestAssuredDriver(String baseURL, IAuthentication auth) {
        this.auth = auth;
        this.baseURL = baseURL;
    }

    public RestAssuredDriver(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setAuth(IAuthentication auth) {
        this.auth = auth;
    }

    public IAuthentication getAuth() {
        return this.auth;
    }

    public String getURL() {
        return this.baseURL;
    }

    /**
     * This method is for login and set cookies It will also get access_token if API
     * gateway URL is given in textNG context
     * @param username: String
     * @param password: String
     */
    public void login(String username, String password) throws Exception {
        if (auth != null) {
            auth.login(username, password);
        }
        else {
            throw new Exception("You need to provide a authenticator to use this function.");
        }
    }

    public void login(String username, String password, RequestSpecification specification) throws Exception {
        if (auth != null) {
            auth.login(username, password, specification);
        }
        else {
            throw new Exception("You need to provide a authenticator to use this function.");
        }
    }

    public void logout() throws Exception {
        if (auth != null) {
            auth.logout();
        }
        else {
            throw new Exception("You need to provide a authenticator to use this function.");
        }
    }

    private void reportingBeforeSend(Operations op, String url, BaseRestParameter params, String payload) {
        reporter.info("Operation: " + op);
        reporter.info("URI: " + url);

        if (auth != null) {
            reporter.info("Auth Header:" + this.auth.getAuthenticationHeader());
        }

        reporter.info("Parameters: " + params.toString());

        if (payload != null)

            reporter.info("Request Payload: " + this.generateFormatedPayload(payload));
    }

    private void reportingAfterSend(Response res) {
        reporter.info(this.generateFormatedResponse(res));
    }

    private String generateFormatedResponse(Response res) {
        return "Response: </br><a style=\"cursor:pointer\" onclick=\"$(this).next('div').toggle()\"> Info (Click to Expand)</a>"
                + "<div style=\"display:none\">" + HTTP_INFO_STYLE + " Status: </span><span> " + res.getStatusLine()
                + "</span>" + BREAK_LINE + HTTP_INFO_STYLE + " Content Type: </span><span>" + res.getContentType()
                + "</span>" + BREAK_LINE + HTTP_INFO_STYLE + " Content Length: </span><span>"
                + res.getHeader("Content-Length") + "</span>" + BREAK_LINE + HTTP_INFO_STYLE
                + " etag: </span><span>" + res.getHeader("etag") + "</span>" + BREAK_LINE + HTTP_INFO_STYLE +
                " Date: </span><span>"
                + res.getHeader("Date") + "</span>" + BREAK_LINE + HTTP_INFO_STYLE + " Response Time (ms):</span><span>"
                + res.getTime() + "</span>" + BREAK_LINE + "</div></>" + generateFormatedPayload(res.asString());

    }

    private String generateFormatedPayload(String payload) {
        try {
            String prettyPayload = "";
            if (payload == null)
                prettyPayload = "No Payload Body";
            else if (payload.trim().isEmpty())
                prettyPayload = "No Payload Body";
            else if (payload.trim().startsWith("{") || payload.trim().startsWith("["))
                prettyPayload = new ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsString(new ObjectMapper().readTree(payload));
            else
                prettyPayload = payload;

            return BREAK_LINE
                    + "<a style=\"cursor:pointer\" onclick=\"$(this).next('xmp').toggle()\"> Payload (Click to Expand)</a><xmp style=\"display:none\">"
                    + prettyPayload + "</xmp></>";
        }
        catch (Exception e) {
            return BREAK_LINE
                    + "<a style=\"cursor:pointer\" onclick=\"$(this).next('xmp').toggle()\"> Invalid JSON Payload (Click to Expand)</a><xmp style=\"display:none\">"
                    + payload + "</xmp></>";
        }
    }

    /**
     * This method is for setting cookie for JSON specific content type for File
     * @param file: String
     * @param params: BaseRestParameter
     * @return RequestSpecification
     */
    private RequestSpecification generateCommonReqSpec(File file, BaseRestParameter params) {
        if (this.auth != null) {
            return this.auth.getAuthenticationReqSpec().headers(this.auth.getAuthenticationHeader())
                    .queryParams(params.getQuery()).multiPart(file).contentType("multipart/form-data");
        }
        return given().queryParams(params.getQuery()).multiPart(file).contentType("multipart/form-data");
    }

    /**
     * @return RequestSpecification
     */
    private RequestSpecification generateCommonReqSpec() {
        if (this.auth != null) {
            return this.auth.getAuthenticationReqSpec().headers(this.auth.getAuthenticationHeader())
                    .contentType(CHARSET_UTF_8).accept(ContentType.JSON).contentType(ContentType.JSON);
        }
        return given().contentType(CHARSET_UTF_8).accept(ContentType.JSON).contentType(ContentType.JSON);
    }

    /**
     * This method is for setting Request specification with query parameter
     * @param params: BaseRestParameter
     * @return RequestSpecification
     */
    private RequestSpecification generateCommonReqSpec(BaseRestParameter params) {
        if (params == null)
            return generateCommonReqSpec();
        return generateCommonReqSpec().headers(params.getHeader()).queryParams(params.getQuery());
    }

    /**
     * This method is for setting Request specification with minimal parameter settings,
     * no content type or accept type are set
     * @param params: BaseRestParameter
     * @return RequestSpecification
     */
    private RequestSpecification generateReqSpecWithNoPayload(BaseRestParameter params) {
        if (params == null && this.auth != null)
            return this.auth.getAuthenticationReqSpec().headers(this.auth.getAuthenticationHeader());

        if (this.auth != null)
            return this.auth.getAuthenticationReqSpec().headers(this.auth.getAuthenticationHeader())
                    .headers(params.getHeader()).queryParams(params.getQuery());

        return given().headers(params.getHeader()).queryParams(params.getQuery());
    }

    private String generateURL(String uri) {
        String finalURI = "";
        finalURI = this.baseURL + uri;
        String pattern = "(?<!https?:)//";
        finalURI = finalURI.replaceAll(pattern, "/");
        return finalURI;
    }

    private String generateURL(String uri, BaseRestParameter params) {
        if (params == null)
            return generateURL(uri);
        if (params.getPath().isEmpty())
            return generateURL(uri);

        for (String key : params.getPath().keySet()) {
            if (uri.contains("{" + key + "}")) {
                uri = uri.replace("{" + key + "}", String.valueOf(params.getPath().get(key)));
            }
        }
        return generateURL(uri);
    }

    /**
     * This method is to perform POST with the specified uri. Input JsonRequest will be
     * treated as request body
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse post(String uri, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.POST, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.post(generateURL(uri, params), null, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse post(String uri, BaseRestParameter params, String payload) throws Exception {
        reportingBeforeSend(Operations.POST, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.post(generateURL(uri, params), payload, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse post(String uri, BaseRestParameter params, String payload, RequestSpecification specification)
            throws Exception {
        reportingBeforeSend(Operations.POST, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.post(generateURL(uri, params), payload,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with the specified uri. Specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @return apiResponse: APIResponse
     * @throws Exception
     *
     */
    public APIResponse post(String uri, BaseRestParameter params, RequestSpecification specification) throws Exception {
        reportingBeforeSend(Operations.POST, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.post(generateURL(uri, params), null,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with a file as attachment and the specified URI.
     * @param : String
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse post(String uri, File file, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.POST, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.post(generateURL(uri, params), null, generateCommonReqSpec(file, params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform GET with a specified URI.
     * @param uri: String
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse get(String uri, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.GET, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.get(generateURL(uri, params), generateCommonReqSpec(params));
        reportingAfterSend(res);
        return new APIResponse(res);
    }

    /**
     * This method is to perform GET with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param : Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse get(String uri, BaseRestParameter params, RequestSpecification specification) throws Exception {
        reportingBeforeSend(Operations.GET, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.get(generateURL(uri, params),
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform HTTP PUT with the specified uri and query and path
     * parameters.
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse put(String uri, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.PUT, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.put(generateURL(uri, params), null, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body
     * @param uri: String
     * @param params: BaseRestParameter
     * @param payload: Request
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse put(String uri, BaseRestParameter params, String payload) throws Exception {
        reportingBeforeSend(Operations.PUT, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.put(generateURL(uri, params), payload, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform PUT with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse put(String uri, BaseRestParameter params, String payload, RequestSpecification specification)
            throws Exception {
        reportingBeforeSend(Operations.PUT, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.put(generateURL(uri, params), payload,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform PUT with the specified uri. Specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @return apiResponse: APIResponse
     * @throws Exception
     *
     */
    public APIResponse put(String uri, BaseRestParameter params, RequestSpecification specification) throws Exception {
        reportingBeforeSend(Operations.PUT, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.put(generateURL(uri, params), null,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform DELETE with query and/or path parameters and the
     * specified URI.
     * @param : String
     * @param : BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse delete(String uri, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.DELETE, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.delete(generateURL(uri, params), null, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform DELETE with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body
     * @param uri: String
     * @param params: BaseRestParameter
     * @param payload: Request
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse delete(String uri, BaseRestParameter params, String payload) throws Exception {
        reportingBeforeSend(Operations.DELETE, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.delete(generateURL(uri, params), payload, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform DELETE with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse delete(String uri, BaseRestParameter params, String payload, RequestSpecification specification)
            throws Exception {
        reportingBeforeSend(Operations.DELETE, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.delete(generateURL(uri, params), payload,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform DELETE with the specified uri. Specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @return apiResponse: APIResponse
     * @throws Exception
     *
     */
    public APIResponse delete(String uri, BaseRestParameter params, RequestSpecification specification)
            throws Exception {
        reportingBeforeSend(Operations.DELETE, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.delete(generateURL(uri, params), null,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform PATCH with the specified uri. Input JsonRequest will be
     * treated as request body
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse patch(String uri, BaseRestParameter params) throws Exception {
        reportingBeforeSend(Operations.PATCH, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.patch(generateURL(uri, params), null, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform POST with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse patch(String uri, BaseRestParameter params, String payload) throws Exception {
        reportingBeforeSend(Operations.PATCH, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.patch(generateURL(uri, params), payload, generateCommonReqSpec(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform PATCH with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param payload: Request
     * @param uri: String
     * @param params: BaseRestParameter
     * @param specification: RequestSpecification
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse patch(String uri, BaseRestParameter params, String payload, RequestSpecification specification)
            throws Exception {
        reportingBeforeSend(Operations.PATCH, generateURL(uri, params), params, payload);
        Response res = RestAssuredUtils.patch(generateURL(uri, params), payload,
                generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    /**
     * This method is to perform HEAD with a json request body with the specified uri.
     * Input JsonRequest will be treated as request body, specification will be rest
     * assured RequestSpecification object in case the provided method does not satisfy
     * your need.
     * @param uri: String
     * @param params: BaseRestParameter
     * @throws Exception
     * @return apiResponse: APIResponse
     */
    public APIResponse head(String uri, BaseRestParameter params)
        throws Exception {
        reportingBeforeSend(Operations.HEAD, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.head(generateURL(uri, params),
            generateReqSpecWithNoPayload(params));
        reportingAfterSend(res);

        return new APIResponse(res);
    }

    public APIResponse head(String uri, BaseRestParameter params, RequestSpecification specification) throws Exception {
        reportingBeforeSend(Operations.HEAD, generateURL(uri, params), params, null);
        Response res = RestAssuredUtils.head(generateURL(uri, params), generateReqSpecWithNoPayload(params).spec(specification));
        reportingAfterSend(res);

        return new APIResponse(res);
    }
}