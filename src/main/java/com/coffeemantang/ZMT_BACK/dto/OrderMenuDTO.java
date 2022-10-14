package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenuDTO {

    @NotNull
    private int ordermenuId;

    @NotNull
    private int orderlistId;

    @NotNull
    private int menuId;

    @NotNull
    private int price;

    public OrderMenuDTO(final OrderMenuEntity orderMenuEntity) {

        this.ordermenuId = orderMenuEntity.getOrdermenuId();
        this.orderlistId = orderMenuEntity.getOrderlistId();
        this.menuId = orderMenuEntity.getMenuId();
        this.price = orderMenuEntity.getPrice();

    }

    public static OrderMenuEntity toEntity(final OrderMenuDTO orderMenuDTO) {

        return OrderMenuEntity.builder()
                .ordermenuId(orderMenuDTO.getOrdermenuId())
                .orderlistId(orderMenuDTO.getOrderlistId())
                .menuId(orderMenuDTO.getMenuId())
                .price(orderMenuDTO.getPrice())
                .build();

    }

}
