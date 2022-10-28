package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderListRepository extends JpaRepository<OrderListEntity, Integer> {
    // 가게 아이디와 회원 아이디로 해당 기간 내의 주문완료 갯수 가져오기
    @Query(value = "SELECT count(store_id) FROM store WHERE " +
            "store_id = :storeId AND member_id = :memberId, AND order_date < :date AND state = 2", nativeQuery = true)
    int countByStoreIdAndMemberId(@Param("storeId") String storeId, @Param("memberId") int memberId, @Param("date")LocalDateTime date);
}
