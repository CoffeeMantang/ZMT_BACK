package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public List<OrderMenuEntity> findAllByOrderlistId(String orderListId);
}
