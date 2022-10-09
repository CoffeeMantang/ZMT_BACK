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
@Table(name = "menuimg")
public class MenuImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "menuimg_id")
    private int menuimgId;
    @Column(name = "menu_id")
    @JoinColumn(name = "menu_id")
    private int menuId;
    @Column(name = "path")
    private String path;
}
