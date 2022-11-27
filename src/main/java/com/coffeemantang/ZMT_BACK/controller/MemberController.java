package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.EmailTokenService;
import com.coffeemantang.ZMT_BACK.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    @Autowired
    private EmailTokenService emailTokenService;



    // 로그인 테스트
    @GetMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal String memberId){

        try{
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
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
    @GetMapping("/getinfo")
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



    // 이름변경
    @PostMapping("/updatename")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal String memberId,  @RequestBody MemberDTO memberDTO){
        try {
            String name = memberService.updateName(Integer.parseInt(memberId), memberDTO.getName());
            if (name != null || !name.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().name(name).build();
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

    // 본인확인 질문 답변 변경
    @PostMapping("/updateqa")
    public ResponseEntity<?> updateQA(@AuthenticationPrincipal String memberId,  @RequestBody MemberDTO memberDTO){
        try {
            MemberEntity memberEntity = memberService.updateQA(Integer.parseInt(memberId), memberDTO.getQuestion(), memberDTO.getAnswer());
            if (memberEntity != null) {
                MemberDTO responseMemberDTO = MemberDTO.builder().question(memberEntity.getQuestion())
                        .answer(memberEntity.getAnswer()).build();
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

    // 대표 주소 가져오기
    @PostMapping("/getmainaddress")
    public ResponseEntity<?> getMainAddress(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            String address1 = memberService.getMainAddress(Integer.parseInt(memberId));
            MemberRocationDTO mrDTO = MemberRocationDTO.builder().address1(address1).build();
            return ResponseEntity.ok().body(mrDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    // 모든 주소 가져오기
    @PostMapping("/getalladdress")
    public ResponseEntity<?> getAllAddress(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            List<MemberRocationDTO> list = memberService.getAllAddress(Integer.parseInt(memberId));
            ResponseDTO responseDTO = ResponseDTO.builder().data(Collections.singletonList(list)).error("ok").build();
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    // 새 주소 저장하기
    @PostMapping("/newaddress")
    public ResponseEntity<?> newAddress(@AuthenticationPrincipal String memberId, @RequestBody MemberRocationDTO dto) throws Exception{
        try{
            memberService.newAddress(Integer.parseInt(memberId), dto);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 최근 검색어 10개 가져오기
    @GetMapping("/getSearchList")
    public ResponseEntity<?> getSearchList(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            List<SearchDTO> result = memberService.getSearchList(Integer.parseInt(memberId));
            ResponseDTO responseDTO = ResponseDTO.builder().data(Arrays.asList(result.toArray())).build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 검색기록 추가하기
    @GetMapping("/addSearch")
    public ResponseEntity<?> addSearch(@AuthenticationPrincipal String memberId, @RequestParam(value = "keyword") String search) throws Exception{
        try{
            memberService.addSearch(Integer.parseInt(memberId), search);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 대표주소 변경하기
    @GetMapping("/setFirstAddress")
    public ResponseEntity<?> setFirstAddress(@AuthenticationPrincipal String memberId, @RequestParam("id") int memberrocationId) throws Exception{
        try{
            // 추후에 memberId로 유효성 비교 넣어야 함
            memberService.setFirstAddress(Integer.parseInt(memberId), memberrocationId);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
