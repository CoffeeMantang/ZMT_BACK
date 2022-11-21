package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OrderOptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptionDTO {


    private Long orderoptionId;


    private Long ordermenuId;

    @NotNull
    private int optionId;
    private String name;


    private int price;

    public OrderOptionDTO(final OrderOptionEntity orderOptionEntity) {

        this.orderoptionId = orderOptionEntity.getOrderoptionId();
        this.ordermenuId = orderOptionEntity.getOrdermenuId();
        this.optionId = orderOptionEntity.getOptionId();
        this.price = orderOptionEntity.getPrice();

    }

    public static OrderOptionEntity toEntity(final OrderOptionDTO orderOptionDTO) {

        return OrderOptionEntity.builder()
                .orderoptionId(orderOptionDTO.orderoptionId)
                .ordermenuId(orderOptionDTO.ordermenuId)
                .optionId(orderOptionDTO.optionId)
                .price(orderOptionDTO.price)
                .build();

    }

}
