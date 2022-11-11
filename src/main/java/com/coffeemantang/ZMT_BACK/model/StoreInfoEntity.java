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
    
    @Column(name = "opentime1")
    private LocalTime openTime1;
    @Column(name = "opentime2")
    private LocalTime openTime2;
    @Column(name = "opentime3")
    private LocalTime openTime3;
    @Column(name = "opentime4")
    private LocalTime openTime4;
    @Column(name = "opentime5")
    private LocalTime openTime5;
    @Column(name = "opentime6")
    private LocalTime openTime6;
    @Column(name = "opentime7")
    private LocalTime openTime7;
    
    @Column(name = "closetime1")
    private LocalTime closeTime1;
    @Column(name = "closetime2")
    private LocalTime closeTime2;
    @Column(name = "closetime3")
    private LocalTime closeTime3;
    @Column(name = "closetime4")
    private LocalTime closeTime4;
    @Column(name = "closetime5")
    private LocalTime closeTime5;
    @Column(name = "closetime6")
    private LocalTime closeTime6;
    @Column(name = "closetime7")
    private LocalTime closeTime7;

    @Column(name = "breaktimestart1")
    private LocalTime breakTimeStart1;
    @Column(name = "breaktimestart2")
    private LocalTime breakTimeStart2;
    @Column(name = "breaktimestart3")
    private LocalTime breakTimeStart3;
    @Column(name = "breaktimestart4")
    private LocalTime breakTimeStart4;
    @Column(name = "breaktimestart5")
    private LocalTime breakTimeStart5;
    @Column(name = "breaktimestart6")
    private LocalTime breakTimeStart6;
    @Column(name = "breaktimestart7")
    private LocalTime breakTimeStart7;

    @Column(name = "breaktimeend1")
    private LocalTime breakTimeEnd1;
    @Column(name = "breaktimeend2")
    private LocalTime breakTimeEnd2;
    @Column(name = "breaktimeend3")
    private LocalTime breakTimeEnd3;
    @Column(name = "breaktimeend4")
    private LocalTime breakTimeEnd4;
    @Column(name = "breaktimeend5")
    private LocalTime breakTimeEnd5;
    @Column(name = "breaktimeend6")
    private LocalTime breakTimeEnd6;
    @Column(name = "breaktimeend7")
    private LocalTime breakTimeEnd7;
}
