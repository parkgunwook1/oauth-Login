package OauthSession.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // Kakao에서는 id 대신 "id"를 사용
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        // Kakao에서는 email 대신 "kakao_account.email"을 사용
        return ((Map<String, Object>) attribute.get("kakao_account")).get("email").toString();
    }

    @Override
    public String getName() {
        // Kakao에서는 name 대신 "properties.nickname"을 사용
        return ((Map<String, Object>) attribute.get("properties")).get("nickname").toString();
    }
}