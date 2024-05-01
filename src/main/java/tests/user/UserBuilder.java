package tests.user;

import com.base.IBuilder;
import com.exception.FrameworkException;

import java.util.Map;

public class UserBuilder implements IBuilder {

    // mandatory fields
    private String createdByUserBneId;

    private String organizationBncId;

    private String preferredLanguage;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    public UserBuilder(String createdByUserBneId, String organizationBncId, String preferredLanguage, String email) {
        this.createdByUserBneId = createdByUserBneId;
        this.organizationBncId = organizationBncId;
        this.email = email;
    }

    public String getPayload() throws FrameworkException {
        return build(this.getPayloadAsMap());
    }

    public Map<String, Object> getPayloadAsMap() throws FrameworkException {
        return map("createdByUserBneId", this.createdByUserBneId, "organizationBncId", this.organizationBncId,
                "preferredLanguage", this.preferredLanguage, "email", this.email);
    }

}
