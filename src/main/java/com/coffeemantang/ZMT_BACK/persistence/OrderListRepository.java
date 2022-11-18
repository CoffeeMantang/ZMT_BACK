package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderListRepository extends JpaRepository<OrderListEntity, String> {
    // 가게 아이디와 회원 아이디로 해당 기간 내의 주문완료 갯수 가져오기
    @Query(value = "SELECT count(store_id) FROM store WHERE " +
            "store_id = :storeId AND member_id = :memberId, AND order_date < :date AND state = 2", nativeQuery = true)
    int countByStoreIdAndMemberId(@Param("storeId") String storeId, @Param("memberId") int memberId, @Param("date")LocalDateTime date);

    // 맴버아이디와 state 값에 맞는 오더리스트 가져오기
    OrderListEntity findByMemberIdAndState(int memberId, int state);

    OrderListEntity findByOrderlistId(String orderListId);

    // 가게 아이디로 해당 년도 총수익 가져오기
    @Query(value = "SELECT SUM(price) FROM orderlist WHERE state = :state " +
            "AND store_id = :storeId AND date_format(order_date, '%Y') = :Y", nativeQuery = true)
    int selectPriceByYear(@Param("state") int state, @Param("storeId") String storeId, @Param("Y") String Y);

    // 가게 아이디로 해당 월 총수익 가져오기
    @Query(value = "SELECT SUM(price) FROM orderlist WHERE state = :state " +
            "AND store_id = :storeId AND date_format(order_date, '%Y-%m') = :Ym", nativeQuery = true)
    int selectPriceByMonth(@Param("state") int state, @Param("storeId") String storeId, @Param("Ym") String Ym);

    // 가게 아이디로 해당 일 총수익 가져오기
    @Query(value = "SELECT SUM(price) FROM orderlist WHERE state = :state " +
            "AND store_id = :storeId AND date_format(order_date, '%Y-%m-%d') = :Ymd", nativeQuery = true)
    int selectPriceByDay(@Param("state") int state, @Param("storeId") String storeId, @Param("Ymd") String Ymd);

    // 파라미터 값으로 해당 년도 수량 합계 가져오기
    @Query(value = "SELECT COUNT(om.quantity) FROM orderlist ol INNER JOIN ordermenu om " +
            "on ol.orderlist_id = om.orderlist_id WHERE ol.state = :state " +
            "AND ol.store_id = :storeId AND om.menu_id = :menuId AND date_format(ol.order_date, '%Y') = :Y", nativeQuery = true)
    int selectQuantityCountByYear(@Param("state") int state, @Param("storeId") String storeId,
                                  @Param("menuId") int menuId, @Param("Y") String Y);

    // 파라미터 값으로 해당 월 수량 합계 가져오기
    @Query(value = "SELECT COUNT(om.quantity) FROM orderlist ol INNER JOIN ordermenu om " +
            "on ol.orderlist_id = om.orderlist_id WHERE ol.state = :state " +
            "AND ol.store_id = :storeId AND om.menu_id = :menuId AND date_format(ol.order_date, '%Y-%m') = :Ym", nativeQuery = true)
    int selectQuantityCountByMonth(@Param("state") int state, @Param("storeId") String storeId,
                                   @Param("menuId") int menuId, @Param("Ym") String Ym);

    // 파라미터 값으로 해당 일 수량 합계 가져오기
    @Query(value = "SELECT COUNT(om.quantity) FROM orderlist ol INNER JOIN ordermenu om " +
            "on ol.orderlist_id = om.orderlist_id WHERE ol.state = :state " +
            "AND ol.store_id = :storeId AND om.menu_id = :menuId AND date_format(ol.order_date, '%Y-%m-%d') = :Ymd", nativeQuery = true)
    int selectQuantityCountByDay(@Param("state") int state, @Param("storeId") String storeId,
                                      @Param("menuId") int menuId, @Param("Ymd") String Ymd);


}
