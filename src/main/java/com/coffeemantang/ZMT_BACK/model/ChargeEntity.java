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
@Table(name = "charge")
public class ChargeEntity {

    @Id
    @Column(name = "store_id")
    @JoinColumn(name = "store_id")
    private String storeId;

    @Column(name = "dong")
    private String dong;

    @Column(name = "charge")
    private int charge;

}
