package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    // 메뉴 아이디로 MenuEntity 리스트 가져오기
    @Query(value = "SELECT * FROM menu WHERE menu_id = :menuId", nativeQuery = true)
    List<MenuEntity> selectByMenuId(@Param("menuId") int menuId);

    // 해당 menuNumber 보다 큰 MenuEntity 가져오기
    @Query(value = "SELECT * FROM menu WHERE menu_number > :menuNumber AND store_id = :storeId", nativeQuery = true)
    List<MenuEntity> findByGreaterThanMenuNumberAndStoreId(@Param("menuNumber") int menuNumber, @Param("storeId") String storeId);

    // 메뉴 아이디로 가격 가져오기
    @Query(value = "SELECT price FROM menu WHERE menu_id = :menuId", nativeQuery = true)
    int selectPriceByMenuId(@Param("menuId") int menuId);

    // 메뉴 아이디로 가게 아이디 가져오기
    @Query(value = "SELECT store_id FROM menu WHERE menu_id = :menuId", nativeQuery = true)
    String selectStoreIdByMenuId(@Param("menuId") int menuId);

    // 배달 가능한 가게의 메뉴 엔티티 가져오기(위치기준)
    @Query(value = "SELECT m.menu_id, m.tag FROM menu AS m INNER JOIN ( " +
            "SELECT s.store_id FROM store AS s INNER JOIN charge AS c ON s.store_id = c.store_id AND c.dong = :dong " +
            "WHERE s.state = 1 AND ST_Distance_Sphere(POINT(:x, :y), POINT(s.address_x, s.address_y)) < 10000 " +
            ") AS asdf ON asdf.store_id = m.store_id WHERE menu_id.state = 0", nativeQuery = true)
    List<MenuEntity> findMenuIdAndTagByMemberInfo(@Param("dong") String dong, @Param("x") double addressX, @Param("y") double addressY);

    // 메뉴 아이디로 태그 가져오기
    MenuEntity findTagByMenuId(int menuId);

    // 현재 위치에서 주문 가능한 메뉴 가져오기(동 기준)
    @Query(value = "SELECT * FROM menu AS m INNER JOIN store AS s ON m.store_id = s.store_id " +
            "INNER JOIN charge AS c ON s.store_id = c.store_id AND c.dong LIKE CONCAT('%', :address, '%') ", nativeQuery = true)
    List<MenuEntity> findMenuByAddress(@Param("address") String address);

}
