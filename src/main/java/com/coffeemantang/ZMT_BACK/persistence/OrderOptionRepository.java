package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.OrderOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOptionEntity, Long> {

    // orderMenuId에 맞는 OptionId 리스트 가져오기
    @Query(value = "SELECT option_id FROM orderoption WHERE ordermenu_id = :ordermenuId", nativeQuery = true)
    public List<Integer> selectAllOptionIdByOrdermenuId(@Param("ordermenuId") Long ordermenuId);

    // orderMenuId로 OrderOptionEntity 리스트 가져오기
    public List<OrderOptionEntity> findAllByOrdermenuId(Long orderMenuId);
}
