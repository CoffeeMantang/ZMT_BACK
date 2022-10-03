package com.coffeemantang.ZMT_BACK.config;

import com.coffeemantang.ZMT_BACK.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http.cors() //WebMvcConfig에서 이미 설정 했으므로 기본 cors 설정
                .and().csrf() // csrf 사용하지 않음
                .disable().httpBasic() // basic 사용하지 않음
                .disable().sessionManagement() // session 기반 아님
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests() // /와 /auth/** 경로는 인증 안해도 됨
                .antMatchers("/", "/member/**", "/error").permitAll().anyRequest() // /와 /auth/** 경로는 인증 안해도 됨
                // "/error" 추가해야 403오류와 json 함께 반환
                .authenticated();
        //filter 등록
        //매 요청마다
        //CorsFilter 실행한 후에
        //jwtAuthenticationFilter 실행
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // 회원가입 시 패스워드를 암호화하기 위한 객체
        return new BCryptPasswordEncoder();
    }
}
