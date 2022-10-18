package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderListRepository extends JpaRepository<OrderListEntity, String> {

    // 맴버아이디와 state 값에 맞는 오더리스트 가져오기
    OrderListEntity findByMemberIdAndState(int memberId, int state);
}
