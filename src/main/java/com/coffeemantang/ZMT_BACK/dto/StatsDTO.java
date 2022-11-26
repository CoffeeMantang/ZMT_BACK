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

    private int profit;

    private int totalAll;

    private List<MenuDTO> menuDTOList;

    private List<OptionDTO> optionDTOList;




}
