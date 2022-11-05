package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRocationRepository extends JpaRepository<MemberRocationEntity, Integer> {

    MemberRocationEntity findByMemberId(int memberId);

    // memberId로 state가 1인 memberrocationId 가져오기
    @Query(value = "SELECT memberrocation_id FROM memberrocation WHERE member_id = :memberId AND state = 1", nativeQuery = true)
    int selectMemberrocationIdByMemberIdAndState(@Param("memberId") int memberId);

}
