package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.ChargeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDTO {

    private String storeId;

    private String dong;

    private int charge;

    public ChargeDTO(final ChargeEntity chargeEntity) {

        this.storeId = chargeEntity.getStoreId();
        this.dong = chargeEntity.getDong();
        this.charge = chargeEntity.getCharge();

    }

    public static ChargeEntity toEntity(final ChargeDTO chargeDTO) {

        return ChargeEntity.builder()
                .storeId(chargeDTO.getStoreId())
                .dong(chargeDTO.getDong())
                .charge(chargeDTO.getCharge())
                .build();

    }

}
