package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    //menu_number 컬럼만 가져오는 메서드
    @Query(value = "SELECT menu_number from menu", nativeQuery = true)
    List<Integer> selectAllMenuNumber();

    MenuEntity findByStoreId(String storeId);
}
