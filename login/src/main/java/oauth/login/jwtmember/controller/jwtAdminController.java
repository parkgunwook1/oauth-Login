package oauth.login.jwtmember.controller;

import oauth.login.jwtmember.model.service.JoinService;
import oauth.login.jwtmember.model.vo.User;
import oauth.login.member.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class jwtAdminController {

    public jwtAdminController() {

    }
    private JoinService joinService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired // 생성자 주입방식 권장.
    public jwtAdminController(JoinService joinService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.joinService = joinService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/jwt/admin")
    public String adminP() {

        return "admin Controller";
    }

    @PostMapping("/jwt/join")
    public String joinProcess(User user) {
        String username = user.getUserName();
        String password = user.getPassword();
        int result = joinService.joinProcess(username, password);

        return "ok";
    }
}
