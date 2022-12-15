package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

// 토큰 관련 기능을 모아둘 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    MemberRepository memberRepository;

    // 토큰이 아직 유효한지 확인하고 유효한 경우 새로운 토큰 발급 - 추후에 refresh token으로 재발급하도록 변경
    @GetMapping("/getNewToken")
    public ResponseEntity<?> getNewToken(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            // 여기로 왔으면 인증 완료되었으므로 새로운 토큰 발급해 response
            MemberEntity memberEntity = memberRepository.findByMemberId(Integer.parseInt(memberId));
            String token = tokenProvider.create(memberEntity);
            return ResponseEntity.ok().body(token); // 토큰넣어서 response
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
