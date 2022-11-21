package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenuEntity, Long> {

    // 오더리스트아이디로 메뉴 삭제하기
    public void deleteAllByOrderlistId(String orderListId);

    // 오더리스트아이디, 오더메뉴아이디로 전체 메뉴 삭제
    @Transactional
    public void deleteAllByOrderlistIdAndOrdermenuId(String orderListId, Long orderMenuId);

    // 오더리스트 아이디로 전체 메뉴 가져오기
    public List<OrderMenuEntity> findAllByOrderlistId(String orderlistId);

    // 멤버아이디와 메뉴아이디로 주문횟수 가져오기
    @Query(value = "SELECT COUNT(*) AS c FROM( SELECT ol.orderlist_id FROM ordermenu AS om INNER JOIN orderlist AS ol " +
            "ON om.orderlist_id = ol.orderlist_id AND ol.member_id = :memberId AND om.menu_id = :menuId " +
            "GROUP BY orderlist_id ) AS temp", nativeQuery = true)
    public int countByMemberIdAndMenuId(@Param("memberId") int memberId, @Param("menuId") int menuId);

    // 멤버아이디와 주소로 현재 지역에서 주문 가능하고 해당 회원이 주문한 메뉴 가져오기
    @Query(value = "SELECT om.menu_id FROM orderlist AS ol INNER JOIN ( " +
            "SELECT s.store_id FROM store AS s INNER JOIN charge AS c ON s.store_id = c.store_id WHERE c.dong " +
            "LIKE CONCAT('%', :address, '%') ) AS temp ON ol.store_id = temp.store_id " +
            "INNER JOIN ordermenu AS om ON om.orderlist_id = ol.orderlist_id " +
            "WHERE ol.member_id = :memberId GROUP BY menu_id ", nativeQuery = true)
    public List<Integer> findMenuIdByMemberIdAndAddress(@Param("address") String address, @Param("memberId") int memberId);

    // 멤버아이디와 주소로 현재 지역에서 주문 가능하고 해당 회원이 주문한 메뉴 가져오기(주문횟수 순으로 정렬)
    @Query(value = "SELECT asdf.menu_id FROM (SELECT om.menu_id, COUNT(*) AS cnt FROM orderlist AS ol INNER JOIN ( " +
            "SELECT s.store_id FROM store AS s INNER JOIN charge AS c ON s.store_id = c.store_id WHERE c.dong " +
            "LIKE CONCAT('%', :address, '%') ) AS temp ON temp.store_id = ol.store_id " +
            "INNER JOIN ordermenu AS om ON om.orderlist_id = ol.orderlist_id " +
            "WHERE ol.member_id = :memberId GROUP BY menu_id ORDER BY cnt DESC) AS asdf ", nativeQuery = true)
    public List<Integer> findMenuIdByMemberIdAndAddressSortDESC(@Param("address") String address, @Param("memberId") int memberId);

    OrderMenuEntity findByOrdermenuId(long ordermenuId);
}
