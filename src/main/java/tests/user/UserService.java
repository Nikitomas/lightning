package tests.user;

import com.base.BaseRequest;
import com.components.ra.driver.RestAssuredDriver;
import com.components.ra.json.APIResponse;
import tests.services.BaseAPIService;

public class UserService extends BaseAPIService {

    private final static String CREATE_USER = "subscription-api/users";

    public UserService(RestAssuredDriver driver) {
        super(driver);
    }

    public APIResponse createUser(BaseRequest request) throws Exception {
        APIResponse response = driver.post(CREATE_USER, request.getParams(), request.getPayload());
        return response;
    }

}
