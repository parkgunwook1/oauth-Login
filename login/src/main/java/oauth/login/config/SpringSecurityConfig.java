package oauth.login.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // 스프링 시큐리티 등록.
@EnableWebSecurity // 스프링 시큐리티의 웹 보안을 활성화하는 어노테이션이다.
@EnableMethodSecurity // 메소드 레벨의 보안을 활성화하는 어노테이션
public class SpringSecurityConfig {


    /**
     * 단방향 암호화 알고리즘
     * */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /**
     * 서블릿 컨테이너에 필터가 여러개 있는데 필터를 거쳐서 스프링부트 내부에 들어온다.
     * 필터는 특정한 경로는 모든 유저에게 오픈하고, 특정 경로는 admin 유저에게만 오픈시킬 수 있다.

     * "인가"
     *
     * 특정한 경로에 요청이 오면 Controller 클래스에 도달하기 전 필터에서 Spring Security가 검증을 함
     *  1. 해당 경로의 접근은 누구에게 열려 있는지
     *  2. 로그인이 완료된 사용자인지
     *  3. 해당되는 role을 가지고 있는지
     *
     * // 즉, SpringSecurityConfig는 인가 설정을 진행하는 클래스이다.
     *
     * 스프링 3.1 부터는 람다형식을 사용해야한다.
     *
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors
                   .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(request -> request // HTTP 요청에 대한 권한을 설정
                        .requestMatchers("/", "/login", "insert", "insert.me").permitAll()
                        // permitAll() 메소드는 모든 사용자에게 로그인을 하지않아도 접속할 수 있다.
                        // 즉, 게시판과 메인페이지는 사용자가 로그인을 안해도 사용할 수 있다.

                        // 마이페이지, 로그인 안했을시 접근을 불가능하게 하려면
                        // .requestMatchers("/mypage").hasRole("ADMIN") 사용하면 admin 경로는 로그인했을때만 사용가능하다.
                        .anyRequest().authenticated()	// 로그인한 사용자만 접근할 수 있도록 설정.
                );
               http
                        .formLogin(login -> login	// form 방식 로그인 사용
                        .loginPage("/login")                // 사용자가 로그인하지 않은 상태에서 리다이렉트 경로지정
                        .loginProcessingUrl("/login.me")    // 로그인 폼 제출시 지정 URL이 POST 요청이 전송되어 로그인 처리한다.
                        .defaultSuccessUrl("/", true)//로그인 성공 후 기본으로 이동할 페이지, tire는 항상 리다이렉트 수행
                        .permitAll()	// 누구나 접근 허용
                )
                .logout(withDefaults());	// 로그아웃은 기본설정으로 (/logout으로 인증해제)

                http
                        .csrf((auth) -> auth.disable());

        return http.build();
    }



    @Bean  //cors 설정 모든 헤더에 대해 허용
    protected CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;

    }
}
