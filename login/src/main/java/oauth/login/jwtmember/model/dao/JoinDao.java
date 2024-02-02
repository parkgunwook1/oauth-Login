package oauth.login.jwtmember.model.dao;

import org.springframework.stereotype.Repository;


public interface JoinDao {

    void joinProcess(String username, String password);

    // 로그인 처리 후 userdetailSerivce에서 커스텀 구현 user

}
