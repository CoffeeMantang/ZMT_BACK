package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private int menuId;

    private String storeId;

    private String menuName;

    private int price;

    private String notice;

    private String category;

    private String tag;

    private int menuNumber;

    private int state;

    public MenuDTO(final MenuEntity menuEntity) {

        this.menuId = menuEntity.getMenuId();
        this.storeId = menuEntity.getStoreId();
        this.menuName = menuEntity.getMenuName();
        this.price = menuEntity.getPrice();
        this.notice = menuEntity.getNotice();
        this.category = menuEntity.getCategory();
        this.tag = menuEntity.getTag();
        this.state = menuEntity.getState();

    }

    public static MenuEntity toEntity(final MenuDTO menuDTO) {

        return MenuEntity.builder()
                .menuId(menuDTO.getMenuId())
                .storeId(menuDTO.getStoreId())
                .menuName(menuDTO.getMenuName())
                .price(menuDTO.getPrice())
                .notice(menuDTO.getNotice())
                .category(menuDTO.getCategory())
                .tag(menuDTO.getTag())
                .state(menuDTO.getState()).build();

    }

}
