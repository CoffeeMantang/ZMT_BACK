package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderListRepository extends JpaRepository<OrderListEntity, String> {
    // 가게 아이디와 회원 아이디로 해당 기간 내의 주문완료 갯수 가져오기
    @Query(value = "SELECT count(store_id) FROM store WHERE " +
            "store_id = :storeId AND member_id = :memberId, AND order_date < :date AND state = 2", nativeQuery = true)
    int countByStoreIdAndMemberId(@Param("storeId") String storeId, @Param("memberId") int memberId, @Param("date")LocalDateTime date);

    // 맴버아이디와 state 값에 맞는 오더리스트 가져오기
    OrderListEntity findByMemberIdAndState(int memberId, int state);

    OrderListEntity findByOrderlistId(String orderListId);

    // 해당 기간 내 전체 수익 합계 가져오기
    @Query(value = "SELECT sum(price) FROM orderlist WHERE state = :state " +
            "AND store_id = :storeId AND order_date BETWEEN :date1 AND :date2", nativeQuery = true)
    Integer selectPriceByDate(@Param("state") Integer state, @Param("storeId") String storeId,
                          @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 전체 배달비 합계 가져오기
    @Query(value = "SELECT sum(charge) FROM orderlist WHERE state = :state " +
            "AND store_id = :storeId AND order_date BETWEEN :date1 AND :date2", nativeQuery = true)
    Integer selectChargeByDate(@Param("state") Integer state, @Param("storeId") String storeId,
                          @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 메뉴 수량 합계 가져오기
    @Query(value = "SELECT COUNT(om.quantity) FROM orderlist ol INNER JOIN ordermenu om " +
            "on ol.orderlist_id = om.orderlist_id WHERE ol.state = :state " +
            "AND ol.store_id = :storeId AND om.menu_id = :menuId AND ol.order_date between :date1 and :date2", nativeQuery = true)
    int selectMenuCountByDate(@Param("state") int state, @Param("storeId") String storeId,
                                  @Param("menuId") int menuId, @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 메뉴 수익 합계 가져오기
    @Query(value = "SELECT SUM(om.price * om.quantity) FROM orderlist ol INNER JOIN ordermenu om " +
            "on ol.orderlist_id = om.orderlist_id WHERE ol.state = :state " +
            "AND ol.store_id = :storeId AND om.menu_id = :menuId AND ol.order_date between :date1 and :date2", nativeQuery = true)
    Integer selectMenuSumByDate(@Param("state") int state, @Param("storeId") String storeId,
                                  @Param("menuId") int menuId, @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 옵션 수량 합계 가져오기
    @Query(value = "SELECT COUNT(om.quantity) FROM orderlist ol " +
            "INNER JOIN ordermenu om on ol.orderlist_id = om.orderlist_id " +
            "INNER JOIN orderoption oo on om.ordermenu_id = oo.ordermenu_id " +
            "WHERE ol.state = :state AND ol.store_id = :storeId AND om.menu_id = :menuId " +
            "AND oo.option_id = :optionId AND ol.order_date between :date1 and :date2", nativeQuery = true)
    int selectOptionCountByDate(@Param("state") int state, @Param("storeId") String storeId,
                                @Param("menuId") int menuId, @Param("optionId") int optionId,
                                @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 옵션 수익 합계 가져오기
    @Query(value = "SELECT SUM(om.quantity * oo.price) FROM orderlist ol " +
            "INNER JOIN ordermenu om on ol.orderlist_id = om.orderlist_id " +
            "INNER JOIN orderoption oo on om.ordermenu_id = oo.ordermenu_id " +
            "WHERE ol.state = :state AND ol.store_id = :storeId AND om.menu_id = :menuId " +
            "AND oo.option_id = :optionId AND ol.order_date between :date1 and :date2", nativeQuery = true)
    Integer selectOptionSumByDate(@Param("state") int state, @Param("storeId") String storeId,
                              @Param("menuId") int menuId, @Param("optionId") int optionId,
                              @Param("date1") String date1, @Param("date2") String date2);

    // 해당 기간 내 취소한 주문 내역
    @Query(value = "SELECT * FROM orderlist WHERE state = :state AND store_id = :storeId " +
            "AND order_date between :date1 and :date2", nativeQuery = true)
    List<OrderListEntity> selectByStateAndDate(@Param("state") int state, @Param("storeId") String storeId,
                                               @Param("date1") String date1, @Param("date2") String date2);

    long countByMemberIdAndState(int memberId, int state);

    // 해당 멤버의 주문내역 모두 가져오기
    Page<OrderListEntity> findAllByMemberIdAndState(int memberId, int state,Pageable pageable);

}
