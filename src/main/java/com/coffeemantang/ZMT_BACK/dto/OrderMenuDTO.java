package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenuDTO {

    private Long ordermenuId;


    private String orderlistId;

    @NotNull
    private int menuId;

    private int price;

    private List<OrderOptionDTO> orderOptionDTOS;

    @NotNull
    private int quantity;

    public OrderMenuDTO(final OrderMenuEntity orderMenuEntity) {

        this.ordermenuId = orderMenuEntity.getOrdermenuId();
        this.orderlistId = orderMenuEntity.getOrderlistId();
        this.menuId = orderMenuEntity.getMenuId();
        this.price = orderMenuEntity.getPrice();
        this.quantity = orderMenuEntity.getQuantity();

    }

    public static OrderMenuEntity toEntity(final OrderMenuDTO orderMenuDTO) {

        return OrderMenuEntity.builder()
                .ordermenuId(orderMenuDTO.getOrdermenuId())
                .orderlistId(orderMenuDTO.getOrderlistId())
                .menuId(orderMenuDTO.getMenuId())
                .price(orderMenuDTO.getPrice())
                .quantity(orderMenuDTO.getQuantity())
                .build();

    }

}
