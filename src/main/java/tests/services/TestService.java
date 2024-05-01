package tests.services;

import com.components.ra.driver.RestAssuredDriver;
import com.components.ra.implement.BaseRestParameter;
import com.components.ra.json.APIResponse;

public class TestService extends BaseAPIService {

    public TestService(RestAssuredDriver driver) {
        super(driver);
    }

    public APIResponse getTest() throws Exception {
        String URI = "/api/build/entity/producttype";
        BaseRestParameter params = new BaseRestParameter();
        params.setPath("id", 9).setQuery("test1", "testValue1").setQuery("test2", "testValue2");
        return this.driver.get(URI, params);
    }

}
