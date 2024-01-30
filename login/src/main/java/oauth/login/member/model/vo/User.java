package oauth.login.member.model.vo;

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
    private String userNo;          // null 허용
    private String username;        // 회원이름    || ?
    private String userEmail;       // 세션이메일  || 소셜 이메일 => 아이디라고 생각
    private String loginType;       // 1. 세션로그인 , 2.카카오, 3. 구글 , 4.네이버
    private String password;        // null허용? 소셜로그인 비밀번호 x
    private String passwordCheck;   // null 허용
    private String userType;        // 계정유형 m: 회원 / a: 관리자
    private String address;         // 주소
    private String phone;           // null허용
    private String userSsn;         // 주민등록 번호
    private String status;          // 0아님1

}
