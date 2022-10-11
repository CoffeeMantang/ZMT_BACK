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

    // 옵션 아이디로 OptionEntity 가져오기
    OptionEntity findByOptionId(int optionId);

    // 옵션 아이디로 가게 아이디 가져오기
    @Query(value = "select menu.store_id from menu left outer join option on menu.menu_id = option.menu_id where option.option_id = :optionId", nativeQuery = true)
    String selectStoreIdByOptionId(@Param("optionId") int optionId);

    // 해당 optionNumber 보다 큰 OptionEntity 가져오기
    @Query(value = "SELECT * FROM option WHERE option_number > :optionNumber AND menu_id = :menuId", nativeQuery = true)
    List<OptionEntity> findByGreaterThanOptionNumberAndMenuId(@Param("optionNumber") int optionNumber, @Param("menuId") int menuId);
}
