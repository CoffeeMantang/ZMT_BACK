package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.OptionDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.service.OptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


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

    // 옵션 수정
    @PostMapping("update")
    public ResponseEntity<?> updateOption(@AuthenticationPrincipal String memberId, @Valid @RequestBody OptionDTO optionDTO) {
        //빌드 수정 필요
        try {
            OptionEntity optionEntity = optionService.updateOption(Integer.parseInt(memberId), optionDTO);
            if(optionEntity != null) {
                OptionDTO responseMenuDTO = OptionDTO.builder()
                        .menuId(optionEntity.getMenuId())
                        .optionId(optionEntity.getOptionId())
                        .optionName(optionEntity.getOptionName())
                        .price(optionEntity.getPrice())
                        .build();
                return ResponseEntity.ok().body(responseMenuDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 옵션 삭제
    @DeleteMapping("/delete")
    public String deleteOption(@AuthenticationPrincipal String memberId, int optionId) {

        optionService.deleteOption(Integer.parseInt(memberId), optionId);

        return "redirect:/";
    }

    // 옵션 순서 위로 이동
    @PostMapping("/up")
    public ResponseEntity<?> optionSequenceUp(@AuthenticationPrincipal String memberId, @RequestBody OptionDTO optionDTO) {

        try {
            OptionEntity optionEntity = optionService.optionSequenceMove(optionDTO, Integer.parseInt(memberId), 1);
            OptionDTO responseOptionDTO = OptionDTO.builder().optionNumber(optionEntity.getOptionNumber()).build();
            return ResponseEntity.ok().body(responseOptionDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //옵션 순서 아래로 이동
    @PostMapping("/down")
    public ResponseEntity<?> optionSequenceDown(@AuthenticationPrincipal String memberId, @RequestBody OptionDTO optionDTO) {

        try {
            OptionEntity optionEntity = optionService.optionSequenceMove(optionDTO, Integer.parseInt(memberId), 2);
            OptionDTO responseOptionDTO = OptionDTO.builder().optionNumber(optionEntity.getOptionNumber()).build();
            return ResponseEntity.ok().body(responseOptionDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
