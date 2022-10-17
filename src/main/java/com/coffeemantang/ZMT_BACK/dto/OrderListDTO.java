package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    @NotNull
    private int orderlistId;

    @NotBlank
    private String storeId;

    @NotNull
    private int memberId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;

    @NotNull
    private int price;

    @NotNull
    private int state;

    private int time;

    private String userMessage;

    private int spoon;

    private String cancelMessage;

    private int weather;

    public OrderListDTO(final OrderListEntity orderListEntity) {

        this.orderlistId = orderListEntity.getOrderlistId();
        this.storeId = orderListEntity.getStoreId();
        this.memberId = orderListEntity.getMemberId();
        this.orderDate = orderListEntity.getOrderDate();
        this.price = orderListEntity.getPrice();
        this.state = orderListEntity.getState();
        this.time = orderListEntity.getTime();
        this.userMessage = orderListEntity.getUserMessage();
        this.spoon = orderListEntity.getSpoon();
        this.cancelMessage = orderListEntity.getCancelMessage();

    }

    public static OrderListEntity toEntity(final OrderListDTO orderListDTO) {

        return OrderListEntity.builder()
                .orderlistId(orderListDTO.getOrderlistId())
                .storeId(orderListDTO.getStoreId())
                .memberId(orderListDTO.getMemberId())
                .orderDate(orderListDTO.getOrderDate())
                .price(orderListDTO.getPrice())
                .state(orderListDTO.getState())
                .time(orderListDTO.getTime())
                .userMessage(orderListDTO.getUserMessage())
                .spoon(orderListDTO.getSpoon())
                .cancelMessage(orderListDTO.getCancelMessage())
                .build();

    }

}