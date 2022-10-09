package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    private int optionId;

    private int menuId;

    private String optionName;

    private int optionNumber;

    private int price;

    public OptionDTO(final OptionEntity optionEntity) {

        this.optionId = optionEntity.getOptionId();
        this.menuId = optionEntity.getMenuId();
        this.optionName = optionEntity.getOptionName();
        this.price = optionEntity.getPrice();

    }

    public static OptionEntity toEntity(final OptionDTO optionDTO) {

        return OptionEntity.builder()
                .optionId(optionDTO.getOptionId())
                .menuId(optionDTO.getMenuId())
                .optionName(optionDTO.getOptionName())
                .price(optionDTO.getPrice())
                .build();

    }

}
