package oauth.login.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // 이 클래스가 스프링의 java 기반 구성 클래스 나타낸다.
@EnableWebSecurity // 스프링 시큐리티의 웹 보안을 활성화하는 어노테이션이다.
@EnableMethodSecurity // 메소드 레벨의 보안을 활성화하는 어노테이션
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors
                   .configurationSource(corsConfigurationSource())
                )

        .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(request -> request // HTTP 요청에 대한 권한을 설정
                        .requestMatchers("/", "/login/controller", "login/model/**").permitAll() //  모든 요청 허용
                        .anyRequest().authenticated()	// 어떠한 요청이라도 인증필요
                )
                .formLogin(login -> login	// form 방식 로그인 사용
                        .defaultSuccessUrl("/wook/home", true)	// 성공 시 리액트
                        .permitAll()	// 리액트 이동이 막히면 안되므로 얘는 허용
                )
                .logout(withDefaults());	// 로그아웃은 기본설정으로 (/logout으로 인증해제)

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
