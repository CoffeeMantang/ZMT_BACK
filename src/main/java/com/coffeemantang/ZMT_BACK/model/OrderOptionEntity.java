package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orderoption")
public class OrderOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "orderoption_id")
    private int orderoptionId;
    @Column(name = "ordermenu_id")
    private int ordermenuId;
    @Column(name = "option_id")
    private int optionId;
    @Column(name = "price")
    private int price;
}
