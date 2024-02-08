package OauthSession.dao;

import OauthSession.vo.UserEntity;


public interface UserEntityMapper {
    UserEntity findByUsername(String username);
    void save(UserEntity userEntity);
}
