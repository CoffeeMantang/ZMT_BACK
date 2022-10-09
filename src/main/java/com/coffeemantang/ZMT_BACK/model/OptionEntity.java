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
@Table(name = "option")
public class OptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "option_id")
    private int optionId;
    @Column(name = "menu_id")
    @JoinColumn(name = "menu_id")
    private int menuId;
    @Column(name = "option_name")
    private String optionName;
    @Column(name = "option_number")
    private int optionNumber;
    @Column(name = "price")
    private int price;
}
