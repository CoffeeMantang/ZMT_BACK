package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;

@Slf4j
@Service
// Member 데이터베이스에 저장된 내용을 가져올 때 사용
// MemberRepository를 이용해 사용자를 생성하고 로그인 시 인증에 사용할 메서드 작성
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    // 새 계정 생성 - 이메일 중복 검사
    public MemberEntity create(final MemberEntity memberEntity){
        if(memberEntity == null || memberEntity.getEmail() == null){
            log.warn("MemberService.create() : memberEntity에 email이 없어요");
            throw new RuntimeException("MemberService.create() : memberEntity에 email이 없어요");
        }
        final String email = memberEntity.getEmail();
        if(memberRepository.existsByEmail(email)){
            log.warn("MemberService.create() : 해당 email이 이미 존재해요");
            throw new RuntimeException("MemberService.create() : 해당 email이 이미 존재해요");
        }

        return memberRepository.save(memberEntity);
    }

    // 로그인 - 자격증명
    public MemberEntity getByCredentials(final String email, final String password, final PasswordEncoder encoder){
        final MemberEntity originalMember = memberRepository.findByEmail(email); // 이메일로 MemberEntity를 찾음
        // 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())){
            return originalMember;
        }
        return null;
    }

    // 아이디로 멤버정보 가져오기
    public MemberEntity getByMemberId(final int memberId){
        if(memberId <= 0){
            log.warn("MemberService.getByMemberId() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.getByMemberId() : memberId 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId); // 아이디로 MemberEntity 찾음
        return memberEntity;
    }

    // 멤버의 비밀번호 랜덤하게 변경하고 entity 리턴
    public MemberEntity changeMemberPw(final int memberId){
        if(memberId <= 0){
            log.warn("MemberService.changeMemberPw() : memberId 값이 이상해요");
            throw new RuntimeException("MemberService.changeMemberPw() : memberId 값이 이상해요");
        }
        final MemberEntity memberEntity = memberRepository.findByMemberId(memberId); // 아이디로 MemberEntity 찾음
        // 12자리 랜덤 비밀번호 생성
        final String pw = RandomStringUtils.random(12, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        memberEntity.setPassword(pw);
        return memberEntity;
    }

    // 들어온 본인확인답변과 아이디가 일치하는지 체크 후 비밀번호 변경하고 entity 리턴
    public MemberEntity checkAnswer(final int memberId, final String answer){
        if(memberId <= 0 || answer == null){
            log.warn("MemberService.checkAnswer() : 들어온 값이 이상해요");
            throw new RuntimeException("MemberService.checkAnswer() : 들어온 값이 이상해요");
        }
        final String originalAnswer = memberRepository.findByMemberId(memberId).getAnswer();
        if(!originalAnswer.equals(answer)){
            // 답변이 일치하지 않으면
            log.warn("MemberService.checkAnswer() : 답변이 달라요");
            throw new RuntimeException("MemberService.checkAnswer() : 답변이 달라요");
        }
        // 답변이 일치하면 비밀번호 랜덤하게 변경 후 entity 리턴
        final MemberEntity memberEntity = changeMemberPw(memberId);
        return memberEntity;
    }
}
