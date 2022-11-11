package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 메뉴아이디로 가게정보 가져오기
    @Query(value="SELECT s.name, s.store_id FROM store AS s INNER JOIN menu AS m ON s.store_id = m.store_id AND m.menu_id = :menuId " +
            "GROUP BY store_id", nativeQuery = true)
    StoreEntity findByMenuId(int menuId);

    // 가게 아이디로 회원 아이디 찾기
    @Query(value = "SELECT member_id FROM store WHERE store_id = :storeId", nativeQuery = true)
    int selectMemberIdByStoreId(@Param("storeId") String storeId);

}

