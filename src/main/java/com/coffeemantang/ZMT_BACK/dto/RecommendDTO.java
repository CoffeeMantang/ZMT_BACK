package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 추천시스템을 위한 DTO
public class RecommendDTO {
    private String storeName;
    private String menuName;
    private String menuPic;
    private int menuId;
    private String storeId;
    private int state; // 0: 주문가능 1: 주문불가능
}
