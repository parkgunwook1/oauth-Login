package oauth.login.controller;

import oauth.login.model.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private UserService uService;

    @GetMapping("/")
    public String index() {
        return "index";
    }



}
