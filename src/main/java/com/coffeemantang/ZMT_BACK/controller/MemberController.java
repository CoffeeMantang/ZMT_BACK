package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.FindPwDTO;
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
    public ResponseEntity<?> registerMember(@RequestBody MemberDTO memberDTO) {
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
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody MemberDTO memberDTO) {
        // 로그인 성공 시에만 MemberEntity 가져옴
        MemberEntity member = memberService.getByCredentials(
                memberDTO.getEmail(),
                memberDTO.getPassword(),
                passwordEncoder
        );
        // MemberEntity 가져오기 성공 시
        if (member != null) {
            // TokenProvider 클래스를 이용해 토큰을 생성한 후 MemberDTO에 넣어서 반환
            final String token = tokenProvider.create(member);
            final MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(member.getEmail())
                    .memberId(member.getMemberId())
                    .type(member.getType()) // 멤버의 타입
                    .nickname(member.getNickname())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        } else {
            // MemberEntity 가져오기 실패 시 -> 로그인 실패
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("login failed").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 비밀번호 찾기 - 질문 가져가기
    @PostMapping("/getquestion")
    public ResponseEntity<?> getquestion(@AuthenticationPrincipal String memberId) {
        try {
            String question = memberService.getQuestion(Integer.parseInt(memberId)); // 아이디로 회원정보 가져옴
            if (question != null && !question.equals("")) {
                final MemberDTO responseMemberDTO = MemberDTO.builder()
                        .question(question)
                        .memberId(Integer.parseInt(memberId)).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                // MemberEntity 가져오기 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);

        }
    }

    // 비밀번호 찾기 - 답변받고 비밀번호 랜덤으로 초기화해서 돌려주기
    @PostMapping("/findpw")
    public ResponseEntity<?> findPw(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {
        try {
            String pw = memberService.checkAnswer(Integer.parseInt(memberId), memberDTO.getAnswer(), passwordEncoder);
            if (pw != null || pw == "") {
                final MemberDTO responseMemberDTO = MemberDTO.builder()
                        .password(pw)
                        .memberId(Integer.parseInt(memberId)).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                // MemberEntity 가져오기 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);

        }
    }

    // 비밀번호 변경하기 - 기존 비밀번호 체크 후 원하는 비밀번호로 변경하기
    @PostMapping("/changepw")
    public ResponseEntity<?> chgPw(@AuthenticationPrincipal String memberId, @RequestBody FindPwDTO findPwDTO) {
        try {
            if (memberService.changePw(Integer.parseInt(memberId), findPwDTO.getCurPw(), findPwDTO.getChgPw(), passwordEncoder)) {
                // 변경 성공 시
                ResponseDTO responseDTO = ResponseDTO.builder().error("success").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                // 변경 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 회원정보 가져오기
    @PostMapping("/getinfo")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal String memberId) {
        try {
            MemberEntity memberEntity = memberService.getMemberEntity(Integer.parseInt(memberId));
            if (memberEntity != null) {
                // 성공 시
                MemberDTO responseMemberDTO = MemberDTO.builder().memberId(memberEntity.getMemberId())
                        .nickname(memberEntity.getNickname())
                        .email(memberEntity.getEmail())
                        .birthDay(memberEntity.getBirthDay())
                        .joinDay(memberEntity.getJoinDay())
                        .name(memberEntity.getName())
                        .gender(memberEntity.getGender())
                        .tel(memberEntity.getTel())
                        .exp(memberEntity.getExp())
                        .type(memberEntity.getType()).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                // 실패 시
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 닉네임 수정 - 이미 해당 닉네임이 있는 경우 수정 실패
    @PostMapping("/updatenickname")
    public ResponseEntity<?> updateInfo(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {
        try {
            String nickname = memberService.updateNickname(Integer.parseInt(memberId), memberDTO.getNickname());
            if (nickname != null || !nickname.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(nickname).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 전화번호 수정 - 같은 전화번호가 이미 있는 경우 수정 실패
    @PostMapping("/updatetel")
    public ResponseEntity<?> updateTel(@AuthenticationPrincipal String memberId, @RequestBody MemberDTO memberDTO) {
        try {
            String tel = memberService.updateTel(Integer.parseInt(memberId), memberDTO.getTel());
            if (tel != null || !tel.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().tel(tel).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 닉네임 중복체크
    @PostMapping("/checknickname")
    public ResponseEntity<?> checkNickname(@RequestBody MemberDTO memberDTO){
        try{
            boolean check = memberService.checkNickname(memberDTO.getNickname());
            if(check){
                MemberDTO responseMemberDTO = MemberDTO.builder().nickname(memberDTO.getNickname()).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
