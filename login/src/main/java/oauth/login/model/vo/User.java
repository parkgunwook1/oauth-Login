package oauth.login.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

                                    //세션 로그인  || 소셜 로그인
    private String id;              // 회원 아이디 || 소셜로그인 아이디
    private String username;        // 회원이름    || ?
    private String password;        // null허용? 소셜로그인 비밀번호 x
    private String passwordCheck;   // null 허용
    private String email;           // 세션이메일  || 소셜 이메일
    private String phone;           // null허용
    private String userNo;          // null 허용
    private String status;          // 0아님1

}
