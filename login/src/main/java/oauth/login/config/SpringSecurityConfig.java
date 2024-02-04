package oauth.login.config;


import oauth.login.jwt.JWTFilter;
import oauth.login.jwt.JWTUtil;
import oauth.login.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration // 스프링 시큐리티 등록.
@EnableWebSecurity // 스프링 시큐리티의 웹 보안을 활성화하는 어노테이션이다.
@EnableMethodSecurity // 메소드 레벨의 보안을 활성화하는 어노테이션
public class SpringSecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    // 생성자 주입
    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }


    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

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
                        .requestMatchers("/", "/user/login", "/user/insert", "/user/insert.me").permitAll()
                        // permitAll() 메소드는 모든 사용자에게 로그인을 하지않아도 접속할 수 있다.
                        // 즉, 게시판과 메인페이지는 사용자가 로그인을 안해도 사용할 수 있다.

                        // 마이페이지, 로그인 안했을시 접근을 불가능하게 하려면
                        // .requestMatchers("/mypage").hasRole("ADMIN") 사용하면 admin 경로는 로그인했을때만 사용가능하다.
//                        .anyRequest().authenticated()	// 로그인한 사용자만 접근할 수 있도록 설정.
                );

              /* 세션 방식
               http
                        .formLogin(login -> login	// form 방식 로그인 사용
                        .loginPage("/login")                // 사용자가 로그인하지 않은 상태에서 리다이렉트 경로지정
                        .loginProcessingUrl("/login.me")    // 로그인 폼 제출시 지정 URL이 POST 요청이 전송되어 로그인 처리한다.
                        .defaultSuccessUrl("/", true)//로그인 성공 후 기본으로 이동할 페이지, tire는 항상 리다이렉트 수행
                        .permitAll()	// 누구나 접근 허용
                )
                         .logout(withDefaults());	// 로그아웃은 기본설정으로 (/logout으로 인증해제)
                */



            //-------------------------------- jwt 방식 ----------------------------------

                http
                        .csrf((auth) -> auth.disable()); // 세션 로그인은 세션을 관리해야함으로 csrf 방어를 해야하는데 jwt 방식은 stateless 임으로 관리 안해줘도 된다.
                                                         // 즉, 서버는 클라이언트 기억하지 않으며 오직 클라이언트의 요청에 대한 응답만 준다.
                http
                        .formLogin((auth) -> auth.disable());   // JWT는 보통 HTTP Header나 url의 파라미터에 토큰을 포함시켜 전달하므로, form 기반의 로그인 방식 사용 X
                 http
                        .httpBasic((auth) -> auth.disable());

                //경로별 인가 작업
                http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/jwt/login", "/jwt", "/jwt/join").permitAll() // login, join , jwt 인증 받지 않고 허용
                        .requestMatchers("/jwt/admin").hasRole("ADMIN")     // admin 역할을 가진 사용자만 허용
                        .anyRequest().authenticated());

                //세션 설정
                http                                          // 세션 비활성화
                     .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                // JWTFilter 등록
                 http
                     .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

            //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
                http
                        .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtil), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }



    /*
    *  cors 는 프론트앤드와 백엔드 연동 설정해주는 방법.
    *
    * */
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
