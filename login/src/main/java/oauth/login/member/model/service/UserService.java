package oauth.login.member.model.service;

import oauth.login.member.model.vo.User;

public interface UserService {
    User loginMember(String userEmail);

    int singupMember(User u);
}
