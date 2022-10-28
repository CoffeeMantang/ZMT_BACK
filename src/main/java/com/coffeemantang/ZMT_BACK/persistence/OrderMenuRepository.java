package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenuEntity, Long> {

    // 오더리스트아이디로 메뉴 삭제하기
    public void deleteAllByOrderlistId(String orderListId);

    @Transactional
    public void deleteAllByOrderlistIdAndOrdermenuId(String orderListId, Long orderMenuId);
}
