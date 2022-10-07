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
@Table(name = "menu")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "menu_id")
    private int menuId;
    @Column(name = "store_id")
    @JoinColumn(name = "store_id")
    private String storeId;
    @Column(name = "menu_name")
    private String menuName;
    @Column(name = "price")
    private int price;
    @Column(name = "notice")
    private String notice;
    @Column(name = "pic")
    private String pic; // 이미지 경로
    @Column(name = "category")
    private String category;
    @Column(name = "tag")
    private String tag;
    @Column(name = "menu_number")
    private int menuNumber;
    @Column(name = "state")
    private int state; // 0:활성화, 1:품절, 2:비활성화
    

}
