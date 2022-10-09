package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MemberDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.lang.reflect.Member;
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
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody MemberDTO memberDTO){
        // 로그인 성공 시에만 MemberEntity 가져옴
        MemberEntity member = memberService.getByCredentials(
                memberDTO.getEmail(),
                memberDTO.getPassword(),
                passwordEncoder
        );
        // MemberEntity 가져오기 성공 시
        if(member != null){
            // TokenProvider 클래스를 이용해 토큰을 생성한 후 MemberDTO에 넣어서 반환
            final String token = tokenProvider.create(member);
            final MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .memberId(member.getMemberId())
                    .type(member.getType()) // 멤버의 타입
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        }else{
            // MemberEntity 가져오기 실패 시 -> 로그인 실패
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 비밀번호 찾기 - 질문 가져가기
    @PostMapping("/getquestion")
    public ResponseEntity<?> getquestion(@AuthenticationPrincipal String memberId){
        MemberEntity memberEntity = memberService.getByMemberId(Integer.parseInt(memberId)); // 아이디로 회원정보 가져옴
        if(memberEntity != null){
            final MemberDTO responseMemberDTO = MemberDTO.builder()
                    .question(memberEntity.getQuestion())
                    .memberId(Integer.parseInt(memberId)).build();
            return ResponseEntity.ok().body(responseMemberDTO);
        }else{
            // MemberEntity 가져오기 실패 시
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("error").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    
    // 비밀번호 찾기 - 답변받고 비밀번호 랜덤으로 초기화해서 돌려주기
    @PostMapping("/findpw")
    public ResponseEntity<?> findPw(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO){
        MemberEntity memberEntity = memberService.checkAnswer(Integer.parseInt(memberId), memberDTO.getAnswer());
        if(memberEntity != null){
            final MemberDTO responseMemberDTO = MemberDTO.builder()
                    .password(memberEntity.getPassword())
                    .memberId(Integer.parseInt(memberId)).build();
            return ResponseEntity.ok().body(responseMemberDTO);
        }else{
            // MemberEntity 가져오기 실패 시
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("error").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
