package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.OptionDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.service.OptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners/store/menu/option")
public class OptionController {

    private final OptionService optionService;

    // 옵션 추가
    @PostMapping("/add")
    public ResponseEntity<?> addOption(@AuthenticationPrincipal String memberId, @RequestBody OptionDTO optionDTO) {

        try {
            OptionEntity tempOptionEntity = OptionDTO.toEntity(optionDTO);
            tempOptionEntity.setOptionNumber(optionService.createOptionNumber(tempOptionEntity.getMenuId()));
            OptionEntity optionEntity = optionService.addOption(tempOptionEntity, Integer.parseInt(memberId));

            OptionDTO responseOptionDTO = OptionDTO.builder()
                    .optionId(optionEntity.getOptionId())
                    .menuId(optionEntity.getMenuId())
                    .optionName(optionEntity.getOptionName())
                    .optionNumber(optionEntity.getOptionNumber())
                    .price(optionEntity.getPrice())
                    .build();

            return ResponseEntity.ok().body(responseOptionDTO);
        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
