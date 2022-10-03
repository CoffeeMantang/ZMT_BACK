package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MemberDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(@RequestBody MemberDTO memberDTO){
        try {
            // 요청을 이용해 저장할 사용자 만들기
            MemberEntity member = MemberEntity.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .name(memberDTO.getName())
                    .nickname(memberDTO.getNickname())
                    .tel(memberDTO.getTel())
                    .birthDay(memberDTO.getBirthDay())
                    .joinDay(LocalDateTime.now()) // 현재 시간
                    .gender(memberDTO.getGender())
                    .type(2) // 2: 일반회원
                    .question(memberDTO.getQuestion())
                    .answer(memberDTO.getAnswer()).build();
            // 서비스를 이용해 Repository에 사용자 저장
            MemberEntity registeredMember = memberService.create(member);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 로그인

    // 아이디 중복 검사
}
