package com.coffeemantang.ZMT_BACK.persistence;

import com.coffeemantang.ZMT_BACK.model.MemberRocationEntity;
import com.coffeemantang.ZMT_BACK.model.MenuImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuImgRepository extends JpaRepository<MenuImgEntity, Integer> {
    MenuImgEntity findByMenuId(int menuId);
}
