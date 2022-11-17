package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecommendDTOList {
    private List<RecommendDTO> recommendDTOs;
}
