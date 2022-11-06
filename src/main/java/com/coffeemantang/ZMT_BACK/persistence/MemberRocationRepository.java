package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRocationRepository extends JpaRepository<MemberRocationEntity, Integer> {

    public MemberRocationEntity findByMemberId(int memberId);

    // 회원 아이디로 해당하는 상태의 주소 가져오기
    Optional<MemberRocationEntity> findAllByMemberIdAndState(int memberId, int state);
}
