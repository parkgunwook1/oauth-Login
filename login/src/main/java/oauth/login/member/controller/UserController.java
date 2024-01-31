package oauth.login.member.controller;

import jakarta.servlet.http.HttpSession;
import oauth.login.member.model.service.UserService;
import oauth.login.member.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller // spring 빈스캐너가 자동으로 빈객체 만들어준다.
@RequestMapping("User")
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
    @Autowired // 생성자 주입방식 권장.
    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/login")
    public String login() {

        return "login"; // 로그인 경로
    }

    // 로그인
    @PostMapping("/login")
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
            mv.setViewName("에러페이지 작성."); //에러페이지이동
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

    @GetMapping("insert")
    public String insert() {

        return "insert"; // 회원가입 폼 보여주기.
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

        String url = "";

        if (singupMember > 0) {
            session.setAttribute("alert", "회원가입성공");
            url = "redirect:/login";
        } else {
            model.addAttribute("errorMsg", "회원가입실패");
            url = "에러페이지이동"; //에러페이지이동
        }

        return url;
    }

    // 아이디 중복 검사로직 짜야함.

    /* 아이디/ 비밀번호 정규식 처리
    * 아이디 자리수
    * 아이디의 특수문자 포함 불가
    * admin 같은 아이디 사용 불가
    * 비밀번호 자리수
    * 비밀번호 특수문자 포함 필수
    * */
}
