package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRocationRepository extends JpaRepository<MemberRocationEntity, Integer> {

    MemberRocationEntity findByMemberId(int memberId);

    // memberId로 state가 1인 memberrocationId 가져오기
    @Query(value = "SELECT memberrocation_id FROM memberrocation WHERE member_id = :memberId AND state = 1", nativeQuery = true)
    int selectMemberrocationIdByMemberIdAndState(@Param("memberId") int memberId);

    // 회원 아이디로 해당하는 상태의 주소 가져오기
    Optional<MemberRocationEntity> findAllByMemberIdAndState(int memberId, int state);

    // 주소로 멤버 아이디 가져오기
    @Query(value="SELECT m.member_id FROM member AS m INNER JOIN memberrocation AS mr ON m.member_id = mr.member_id AND " +
            "mr.address1 LIKE CONCAT('%', :address, '%') GROUP BY member_id ", nativeQuery = true)
    List<Integer> findMemberIdByAddress(@Param("address") String address);

    // 아이디로 해당 상태의 주소만 가져오기
    MemberRocationEntity findAddress1ByMemberIdAndState(int memberId, int state);

    // 아이디로 모든 주소 가져오기
    List<MemberRocationEntity> findAllByMemberId(int memberId);

}
