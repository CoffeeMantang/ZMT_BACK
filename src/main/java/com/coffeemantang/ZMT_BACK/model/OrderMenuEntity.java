package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordermenu")
public class OrderMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "ordermenu_id")
    private Long ordermenuId;

    @Column(name = "orderlist_id")
    @JoinColumn(name = "orderlist_id")
    private String orderlistId;

    @Column(name = "menu_id")
    @JoinColumn(name = "menu_id")
    private int menuId;

    @Column(name = "price")
    private int price;

    @Column(name = "quantity")
    private int quantity;

}