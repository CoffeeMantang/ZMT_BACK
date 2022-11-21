package com.coffeemantang.ZMT_BACK.persistence;

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

    long countByMemberIdAndState(int memberId, int state);

    OrderListEntity findByOrderlistId(String orderlistId);

    // 해당 멤버의 주문내역 모두 가져오기
    Page<OrderListEntity> findAllByMemberId(int memberId, Pageable pageable);
}
