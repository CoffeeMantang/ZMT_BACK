package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.framework.qual.RequiresQualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners/store/menu")
public class MenuController {

    private final MenuService menuService;

    //메뉴 추가
    @PostMapping("/add")
    public ResponseEntity<?> addMenu(@AuthenticationPrincipal String memberId, @RequestBody MenuDTO menuDTO) {

        try {
            //MenuDTO를 MenuEntity로 변환
            MenuEntity tempMenuEntity = MenuDTO.toEntity(menuDTO);
            //MenuNumber 생성
            tempMenuEntity.setMenuNumber(menuService.createMenuNumber(tempMenuEntity.getStoreId()));
            //MenuEntity 생성
            MenuEntity menuEntity = menuService.addMenu(tempMenuEntity, Integer.parseInt(memberId));

            MenuDTO responseMenuDTO = MenuDTO.builder()
                    .menuId(menuEntity.getMenuId())
                    .storeId(menuEntity.getStoreId())
                    .menuName(menuEntity.getMenuName())
                    .price(menuEntity.getPrice())
                    .notice(menuEntity.getNotice())
                    .category(menuEntity.getCategory())
                    .tag(menuEntity.getTag())
                    .menuNumber(menuEntity.getMenuNumber())
                    .state(menuEntity.getState())
                    .build();

            return ResponseEntity.ok().body(responseMenuDTO);

        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 메뉴 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateMenu(@AuthenticationPrincipal String memberId, @Valid @RequestBody MenuDTO menuDTO) {

        try {
            MenuEntity menuEntity = menuService.updateMenu(Integer.parseInt(memberId), menuDTO);
            if (menuEntity != null) {
                MenuDTO responseMenuDTO = MenuDTO.builder()
                        .storeId(menuEntity.getStoreId())
                        .menuId(menuEntity.getMenuId())
                        .menuName(menuEntity.getMenuName())
                        .price(menuEntity.getPrice())
                        .notice(menuEntity.getNotice())
                        .category(menuEntity.getCategory())
                        .state(menuEntity.getState())
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

    // 메뉴 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMenu(@AuthenticationPrincipal String memberId, int menuId) {

        try {
            MenuEntity menuEntity = menuService.deleteMenu(Integer.parseInt(memberId), menuId);
            MenuDTO responseMenuDTO = MenuDTO.builder().state(menuEntity.getState()).build();
            return ResponseEntity.ok().body(responseMenuDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 메뉴 순서 위로 이동
    @PostMapping("/up")
    public ResponseEntity<?> menuSequenceUp(@AuthenticationPrincipal String memberId, @RequestBody MenuDTO menuDTO) {

        try {
            MenuEntity menuEntity = menuService.menuSequenceMove(menuDTO, Integer.parseInt(memberId), 1);
            MenuDTO responseMenuDTO = MenuDTO.builder().menuNumber(menuEntity.getMenuNumber()).build();
            return ResponseEntity.ok().body(responseMenuDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //메뉴 순서 아래로 이동
    @PostMapping("/down")
    public ResponseEntity<?> menuSequenceDown(@AuthenticationPrincipal String memberId, @RequestBody MenuDTO menuDTO) {

        try {
            MenuEntity menuEntity = menuService.menuSequenceMove(menuDTO, Integer.parseInt(memberId), 2);
            MenuDTO responseMenuDTO = MenuDTO.builder().menuNumber(menuEntity.getMenuNumber()).build();
            return ResponseEntity.ok().body(responseMenuDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }




}
