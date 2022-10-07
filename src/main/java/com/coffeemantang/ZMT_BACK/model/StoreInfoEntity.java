package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "storeinfo")
public class StoreInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "storeinfo_id")
    private int storeinfoId;
    @Column(name = "store_id")
    @JoinColumn(name = "store_id")
    private String storeId;
    @Column(name = "notice")
    private String notice;
    @Column(name = "tel")
    private String tel;
    
    @Column(name = "openTime1")
    private LocalTime openTime1;
    @Column(name = "openTime2")
    private LocalTime openTime2;
    @Column(name = "openTime3")
    private LocalTime openTime3;
    @Column(name = "openTime4")
    private LocalTime openTime4;
    @Column(name = "openTime5")
    private LocalTime openTime5;
    @Column(name = "openTime6")
    private LocalTime openTime6;
    @Column(name = "openTime7")
    private LocalTime openTime7;
    
    @Column(name = "closeTime1")
    private LocalTime closeTime1;
    @Column(name = "closeTime2")
    private LocalTime closeTime2;
    @Column(name = "closeTime3")
    private LocalTime closeTime3;
    @Column(name = "closeTime4")
    private LocalTime closeTime4;
    @Column(name = "closeTime5")
    private LocalTime closeTime5;
    @Column(name = "closeTime6")
    private LocalTime closeTime6;
    @Column(name = "closeTime7")
    private LocalTime closeTime7;

    @Column(name = "breakTimeStart1")
    private LocalTime breakTimeStart1;
    @Column(name = "breakTimeStart2")
    private LocalTime breakTimeStart2;
    @Column(name = "breakTimeStart3")
    private LocalTime breakTimeStart3;
    @Column(name = "breakTimeStart4")
    private LocalTime breakTimeStart4;
    @Column(name = "breakTimeStart5")
    private LocalTime breakTimeStart5;
    @Column(name = "breakTimeStart6")
    private LocalTime breakTimeStart6;
    @Column(name = "breakTimeStart7")
    private LocalTime breakTimeStart7;

    @Column(name = "breakTimeEnd1")
    private LocalTime breakTimeEnd1;
    @Column(name = "breakTimeEnd2")
    private LocalTime breakTimeEnd2;
    @Column(name = "breakTimeEnd3")
    private LocalTime breakTimeEnd3;
    @Column(name = "breakTimeEnd4")
    private LocalTime breakTimeEnd4;
    @Column(name = "breakTimeEnd5")
    private LocalTime breakTimeEnd5;
    @Column(name = "breakTimeEnd6")
    private LocalTime breakTimeEnd6;
    @Column(name = "breakTimeEnd7")
    private LocalTime breakTimeEnd7;
}
