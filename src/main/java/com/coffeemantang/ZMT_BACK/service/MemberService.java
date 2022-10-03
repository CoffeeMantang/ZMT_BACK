package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
