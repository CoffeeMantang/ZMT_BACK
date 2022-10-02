package com.coffeemantang.ZMT_BACK.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "honer")
public class HonerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "honer_id")
    private int honerId;
    @Column(name = "member_id")
    private int memberId;
    @Column(name = "store_id")
    private String storeId;
    @Column(name = "type")
    private int type; // 어떤 메달인지, 0:없음, 1부터 메달있음
}