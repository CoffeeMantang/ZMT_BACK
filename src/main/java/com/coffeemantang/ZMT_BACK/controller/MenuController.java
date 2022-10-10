package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    //메뉴 순서 위로 이동
//    @PutMapping("/up")
//    public ResponseEntity<?> menuSequenceUp(@AuthenticationPrincipal String memberId, @RequestBody MenuDTO menuDTO) {
//
//        MenuEntity menuEntity = menuService.menuSequenceUp(menuId, Integer.parseInt(memberId));
//    }

}
