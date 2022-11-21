package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orderlist")
public class OrderListEntity {

    @Id
    @GenericGenerator(name = "orderlist_id", strategy = "com.coffeemantang.ZMT_BACK.generator.OrderListGenerator")
    @GeneratedValue(generator = "orderlist_id")
    @Column(name = "orderlist_id")
    private String orderlistId;

    @Column(name = "store_id")
    @JoinColumn(name = "store_id")
    private String storeId;

    @Column(name = "member_id")
    @JoinColumn(name = "member_id")
    private int memberId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "price")
    private int price;

    @Column(name = "state")
    private int state;

    @Column(name = "time")
    private int time; // 예상 소요시간

    @Column(name = "usermessage")
    private String userMessage; // 유저메시지 ex) 문앞에두고벨, 리뷰이벤트

    @Column(name = "spoon")
    private int spoon; // 일회용품

    @Column(name = "cancelmessage")
    private String cancelMessage; // 취소사유

    @Column(name = "weather")
    private int weather; // 취소사유

    @Column(name = "charge")
    private int charge; // 배달비

    @Column(name = "memberrocation_id")
    @JoinColumn(name = "memberrocation_id")
    private int memberrocationId;
}
