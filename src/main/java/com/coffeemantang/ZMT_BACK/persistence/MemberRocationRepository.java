package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRocationRepository extends JpaRepository<MemberRocationEntity, Integer> {

    public MemberRocationEntity findByMemberId(int memberId);
}
