package com.coffeemantang.ZMT_BACK.security;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

//사용자 정보를 받아 JWT를 생성하는 클래스
@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "meteo123"; // secret key

    // JWT 라이브러리를 이용해 JWT 토큰을 생성
    public String create(MemberEntity memberEntity){
        // 기한은 지금부터 1일로 설정
        Date expiryDate = Date.from(
                Instant.now().plus(1, ChronoUnit.DAYS)
        );
        // JWT 토큰 생성
        return Jwts.builder()
                // header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // payload에 들어갈 내용
                .setSubject(String.valueOf(memberEntity.getMemberId())) //sub
                .setIssuer("demo app") //iss
                .setIssuedAt(new Date()) //iat
                .setExpiration(expiryDate) //exp
                .compact();
    }

    // 토큰을 디코딩 및 파싱하고 토큰의 위조 여부를 확인
    // 이후 우리가 원하는 subject, 즉 사용자의 아이디를 리턴한다.
    // 라이브러리 덕에 우리가 굳이 JSON을 생성, 서명, 인코딩, 디코딩, 파싱하는 작업을 하지 않아도 된다.
    public String validateAndGetUserId(String token){
        // parseClaimsJws 메서드가 Base64로 디코딩 및 파싱
        // 헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용해 서명한 후 token의 서명과 비교
        // 위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림
        // 그 중 우리는 userId가 필요하므로 getBody를 부른다.
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
