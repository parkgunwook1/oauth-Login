package OauthSession.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {

    private Long id;
    private String username;
    private String email;
    private String role;
}
