package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    //menu_number 컬럼만 가져오는 메서드
    @Query(value = "SELECT menu_number FROM menu WHERE store_id = :storeId", nativeQuery = true)
    List<Integer> selectAllMenuNumber(@Param("storeId") String storeId);

    //멤버 아이디 가져옴
    @Query(value = "select store.member_id from store " +
            "left outer join menu on store.store_id = " +
            "(select menu.store_id from menu WHERE menu_id = :menuId)", nativeQuery = true)
    int selectMemberIdByMenuId(@Param("menuId") int menuId);

    MenuEntity findByStoreId(String storeId);


}
