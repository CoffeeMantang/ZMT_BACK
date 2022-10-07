package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Member의 Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    // 이메일로 찾기
    MemberEntity findByEmail(String email);
    // 해당하는 이메일이 있는지 확인
    Boolean existsByEmail (String email);
    // 이메일과 비밀번호로 찾기
    MemberEntity findByEmailAndPassword(String email, String password);
    // 아이디로 찾기
    MemberEntity findByMemberId(int memberId);
}
