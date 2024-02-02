package oauth.login.jwtmember.model.service;

import oauth.login.jwtmember.model.dao.JoinDao;
import oauth.login.member.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    @Autowired
    private JoinDao joinDao;

    public int joinProcess(String username, String password) {
        return joinDao.joinProcess(username, password);
    }
}
