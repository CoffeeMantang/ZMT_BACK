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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    @NotNull
    private String orderlistId;

    @NotBlank
    private String storeId;
    private String storeName;

    @NotNull
    private int memberId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;

    @NotNull
    private int price;

    @NotNull
    private int state;
    private String address;

    private int time;

    private String userMessage;

    private int spoon;

    private String cancelMessage;

    private int weather;

    private List<OrderMenuDTO> orderMenuDTOList;

    private List<MenuDTO> menuDTOList;

    private int charge;
    private String thumb;

    private int memberrocationId;
    private int canReview;
    private String nickname; // 회원 닉네임


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
        this.memberrocationId = orderListEntity.getMemberrocationId();

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
                .memberrocationId(orderListDTO.getMemberrocationId())
                .build();

    }

}
