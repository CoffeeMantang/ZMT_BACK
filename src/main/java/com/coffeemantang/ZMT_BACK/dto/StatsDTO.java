package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {

    // 전체 수익
    private int totalProfit;

    // 전체 수익에서 배달비 뺀 수익
    private int totalProfitMinusCharge;

    // 메뉴들만 합한 수익
    private int sumAllMenus;

    // 해당 메뉴의 옵션들만 합한 수익
    private int sumAllOptions;

    private List<MenuDTO> menuDTOList;

    private List<OptionDTO> optionDTOList;




}
