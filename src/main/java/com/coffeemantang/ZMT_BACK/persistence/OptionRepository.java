package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<OptionEntity, Integer> {

    // optionNumber만 가져옴
    @Query(value = "SELECT option_number FROM option WHERE menu_id = :menuId", nativeQuery = true)
    List<Integer> selectAllOptionNumber(@Param("menuId") int menuId);

    // 메뉴 아이디, 옵션 번호로 OptionEntity 가져오기
    OptionEntity findByMenuIdAndOptionNumber(int menuId, int optionNumber);

    // 메뉴 아이디, 옵션 아이디로 OptionEntity 가져오기
    OptionEntity findByMenuIdAndOptionId(int menuId, int optionId);
}
