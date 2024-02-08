package OauthSession.service;

import OauthSession.dao.UserEntityMapper;
import OauthSession.dto.*;
import OauthSession.vo.UserEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserEntityMapper userEntityMapper;

    public CustomOAuth2UserService(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { // userRequest 에 플랫폼 로그인 정보들이 넘어옴

        OAuth2User oAuth2User = super.loadUser(userRequest); // OAuth2 토큰을 사용하여 사용자 정보 가져온다.
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        }else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        }else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        }else {
            return null;
        }

        String username = oAuth2Response.getProvider()+ "" +oAuth2Response.getProviderId();
        UserEntity existData = userEntityMapper.findByUsername(username);

        String role = null;

        if(existData == null) { // 처음 로그인하는 경우 insert
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());
            user.setRole("ROLE_USER");

            userEntityMapper.save(user);
        }else { // 이미 로그인한 경우 update
            existData.setUsername(username);
            existData.setEmail(oAuth2Response.getEmail());
            role = existData.getRole();
            userEntityMapper.save(existData);

        }

        return new CustomOAuth2User(oAuth2Response , role);
    }
}
