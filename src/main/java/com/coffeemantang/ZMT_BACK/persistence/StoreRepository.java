package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Store의 Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {
    // 가게 아이디로 찾기
    StoreEntity findByStoreId(String storeId);
    // 회원 아이디로 찾기
//    List<StoreEntity> findByMemberId(int memberId);

    //회원 아이디로 StoreEntity 가져오기
    StoreEntity findByMemberId(int memberId);

    // 가게 아이디와 회원 아이디로 StoreEntity 가져오기
    StoreEntity findByStoreIdAndMemberId(String storeId, int memberId);
}

