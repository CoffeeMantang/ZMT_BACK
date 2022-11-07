package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.OptionDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.service.MenuService;
import com.coffeemantang.ZMT_BACK.service.OptionService;
import com.coffeemantang.ZMT_BACK.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/nonmember")
public class NonmemberController {

    private final StoreService storeService;

    private final MenuService menuService;

    private final OptionService optionService;

    // 가게 목록
    @PostMapping("/store/list")
    public List<StoreDTO> viewStoreList() {

        try {
            List<StoreDTO> storeDTOList = storeService.viewStoreList();
            return storeDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 리스트를 가져오는 도중 오류 발생");
        }

    }

    // 가게 보기(클릭했을 때)
    @PostMapping("/store/view")
    public StoreDTO viewStore(@RequestBody StoreDTO storeDTO) {

        try {
            StoreDTO responseStoreDTO = storeService.viewStore(0, storeDTO);
            return responseStoreDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("가게 정보를 가져오는 도중 오류 발생");
        }

    }

    // 메뉴 목록
    @PostMapping("/store/menu")
    public List<MenuDTO> viewMenuList(String storeId) {

        try {
            List<MenuDTO> menuDTOList = menuService.viewMenuList(storeId);
            return menuDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메뉴 리스트를 가져오는 도중 오류 발생");
        }

    }


    // 옵션 목록
    @PostMapping("/store/option")
    public List<OptionDTO> viewOptionList(int menuId) {

        try {
            List<OptionDTO> optionDTOList = optionService.viewOptionList(menuId);
            return optionDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("옵션 리스트를 가져오는 도중 오류 발생");
        }

    }


}
