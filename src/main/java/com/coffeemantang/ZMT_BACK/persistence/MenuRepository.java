package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    // state가 2보다 작은 menu_number 컬럼만 가져오는 메서드
    @Query(value = "SELECT menu_number FROM menu WHERE store_id = :storeId AND state < :state", nativeQuery = true)
    List<Integer> selectAllMenuNumber(@Param("storeId") String storeId, @Param("state") int state);

    //멤버 아이디 가져옴
    @Query(value = "select store.member_id from store " +
            "left outer join menu on store.store_id = " +
            "(select menu.store_id from menu WHERE menu_id = :menuId)", nativeQuery = true)
    int selectMemberIdByMenuId(@Param("menuId") int menuId);

    // 가게 아이디로 메뉴 번호로 정렬 된 MenuEntity 가져오기
    @Query(value = "SELECT * FROM menu WHERE store_id = :storeId AND state < :state order by menu_number", nativeQuery = true)
    List<MenuEntity> selectMenuOrderByMenuNumber(@Param("storeId") String storeId, @Param("state") int state);

    // 가게 아이디, 메뉴 번호로 state가 2보다 작은 MenuEntity 가져오기
    @Query(value = "SELECT * FROM menu WHERE store_id = :storeId AND menu_number = :menuNumber AND state < :state", nativeQuery = true)
    MenuEntity selectMenuStoreIdAndMenuNumberAndState(@Param("storeId") String storeId, @Param("menuNumber") int menuNumber, @Param("state") int state);

    // 가게 아이디, 메뉴 아이디로 MenuEntity 가져오기
    @Query(value = "SELECT * FROM menu WHERE store_id = :storeId AND menu_id = :menuId AND state < :state", nativeQuery = true)
    MenuEntity selectMenuStoreIdAndMenuIdAndState(@Param("storeId") String storeId, @Param("menuId") int menuId, @Param("state") int state);

    // 메뉴 아이디로 MenuEntity 가져오기
    MenuEntity findByMenuId(int menuId);

    // 해당 menuNumber 보다 큰 MenuEntity 가져오기
    @Query(value = "SELECT * FROM menu WHERE menu_number > :menuNumber AND store_id = :storeId", nativeQuery = true)
    List<MenuEntity> findByGreaterThanMenuNumberAndStoreId(@Param("menuNumber") int menuNumber, @Param("storeId") String storeId);


}
