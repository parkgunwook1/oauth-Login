package oauth.login.member.model.service;


import oauth.login.member.model.dao.UserDao;
import oauth.login.member.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User loginMember(String userEmail) {
        return null;
    }

    @Override
    public int singupMember(User u) {
        return 0;
    }
}
