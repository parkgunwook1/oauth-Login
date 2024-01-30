package oauth.login.member.controller;

import jakarta.servlet.http.HttpSession;
import oauth.login.member.model.service.UserService;
import oauth.login.member.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller // spring 빈스캐너가 자동으로 빈객체 만들어준다.
public class UserController {

    // DI(Dependency Injection) -> 객체를 개발자가 생성하는게 아니라, 스프링이 생성한 객체를 주입받아서 사용하느 방식
    // new 연산자를 쓰지 않고, 선언만 하고 @Autowird 어노테이션을 붙여서 주입을 받는다.
    // 필드 주입방식은 권장하지 않고, 생성자 주입방식은 권장한다.

    //@Autowired 권장하지 않는 필드방식.
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    // 생성자 주입방식은 의존성 주입시 권장하는 방식이다.
    // 생성자에 침조할 클래스를 인자(매개변수)로 받아서 매핑시킨다.
    // 현재 클래스에 내가 주입시킬 객체를 모아서 관리할 수 있기 때문에 한눈에 알아보기 편하다.

    public UserController() {

    }
    @Autowired
    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 로그인
    @PostMapping(value = "login.me")
    public ModelAndView loginMember(
            User u,
            HttpSession session,
            Model model,
            ModelAndView mv
    ) {

        User loginUser = userService.loginMember(u.getUserEmail());
        // 회원가입된 회원의 비밀번호는 암호화처리 되어있음.

        // matches(평문,암호문)을 작성시 내부적으로 두 값이 일치하는지 검사후 일치하면 true, 일치하지 않으면 false
        if(loginUser != null && bCryptPasswordEncoder.matches(u.getPassword(), loginUser.getPassword())) {
            model.addAttribute("loginUser" , loginUser);

            mv.setViewName("redirect:/");
        }else {
            mv.addObject("errorMsg", "오류발생");
            mv.setViewName("에러페이지 작성.");
        }
        return mv;
    }

    @GetMapping("logout.me")
    public String logoutMember(HttpSession session) {
        // 새션으로 이관된것은 session으로 지울 수 없다.
        // SessionStatus 사용해야함.
        session.invalidate();

        return "redirect:/"; //main url로 이동
    }

    @PostMapping("/insert.me")
    public String Singup(
            User u,
            HttpSession session,
            Model model
    ) {
        int singupMember = 0;

        if (u.getPassword().equals(u.getPasswordCheck())) {
            String encPwd = bCryptPasswordEncoder.encode(u.getPassword());
            u.setPassword(encPwd);

            singupMember = userService.singupMember(u);

        }

    }



}
