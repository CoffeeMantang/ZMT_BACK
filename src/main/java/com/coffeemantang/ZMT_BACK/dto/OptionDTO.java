package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    @NotNull
    private int optionId;

    @NotNull
    private int menuId;

    @NotBlank
    private String optionName;

    private int optionNumber;

    @NotNull
    private int price;

    private int count;

    private int total;

    public OptionDTO(final OptionEntity optionEntity) {

        this.optionId = optionEntity.getOptionId();
        this.menuId = optionEntity.getMenuId();
        this.optionName = optionEntity.getOptionName();
        this.price = optionEntity.getPrice();

    }

    public OptionDTO(String optionName, int price, int count, int total) {

        this.optionName = optionName;
        this.price = price;
        this.count = count;
        this.total = total;

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
