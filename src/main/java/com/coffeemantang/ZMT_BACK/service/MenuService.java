package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.model.MemberEntity;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    private final StoreRepository storeRepository;

    //메뉴 추가
    public MenuEntity addMenu(final MenuEntity menuEntity, int memberId) {

        int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuEntity.getMenuId());

        if (menuEntity == null || menuEntity.getStoreId() == null) {
            log.warn("MenuService.addMenu() : menuEntity에 내용이 부족해요");
            throw new RuntimeException("MenuService.addMenu() : menuEntity에 내용이 부족해요");
        }
        else if (memberId != selectMemberIdByMenuId) {
            log.warn("MenuService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("MenuService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        return menuRepository.save(menuEntity);

    }

    // 메뉴 수정
    public MenuEntity updateMenu(int memberId, @Valid MenuDTO menuDTO) {

        int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuDTO.getMenuId());

        if (memberId != selectMemberIdByMenuId) {
            log.warn("MenuService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("MenuService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        MenuEntity menuEntity = menuRepository.findByMenuId(menuDTO.getMenuId());
        menuEntity.setMenuName(menuDTO.getMenuName());
        menuEntity.setPrice(menuDTO.getPrice());
        menuEntity.setNotice(menuDTO.getNotice());
        menuEntity.setCategory(menuDTO.getCategory());
        menuEntity.setState(menuDTO.getState());
        menuRepository.save(menuEntity);

        return menuEntity;

    }

    // 메뉴 삭제 메서드
    public MenuEntity deleteMenu(int memberId, int menuId) {

        MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
        StoreEntity storeEntity = storeRepository.findByStoreIdAndMemberId(menuEntity.getStoreId(), memberId);

        if(!menuEntity.getStoreId().equals(storeEntity.getStoreId())) {
            log.warn("MenuService.deleteMenu() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("MenuService.deleteMenu() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        int menuNumber = menuEntity.getMenuNumber();
        menuEntity.setState(2);
        List<MenuEntity> menuEntityList = menuRepository.findByGreaterThanMenuNumberAndStoreId(menuNumber, menuEntity.getStoreId());

        for (MenuEntity menuEntity1 : menuEntityList) {
            menuEntity1.setMenuNumber(menuEntity1.getMenuNumber() - 1);
            menuRepository.save(menuEntity1);
        }

        return menuEntity;

    }

    //메뉴 번호 생성 메서드
    public int createMenuNumber(String storeId) {

        //menuNumber 컬럼만 가져옴
        List<Integer> list = menuRepository.selectAllMenuNumber(storeId, 2);

        //리스트가 비어있으면 1, 아니면 최대값 + 1
        if (list.isEmpty()) {
            return 1;
        } else {
            //menuNumber에서 최대값
            int max = Collections.max(list);
            return max + 1;
        }

    }

    // 메뉴 순서 이동
    public MenuEntity menuSequenceMove(MenuDTO menuDTO, int memberId, int move) {

        try {
            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuDTO.getMenuId());

            if (memberId != selectMemberIdByMenuId) {
                log.warn("MenuService.menuSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
                throw new RuntimeException("MenuService.menuSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
            }

            int menuNumber = menuDTO.getMenuNumber();
            MenuEntity menuEntity;
            if(move == 1) { // move == 1 : /up
                menuNumber -= 1;
                // 순서가 내려갈 메뉴
                menuEntity = menuRepository.selectMenuStoreIdAndMenuNumberAndState(menuDTO.getStoreId(), menuNumber, 2);
                menuEntity.setMenuNumber(menuNumber + 1);
                menuRepository.save(menuEntity);
            } else { // move == 2 : /down
                menuNumber += 1;
                // 순서가 올라갈 메뉴
                menuEntity = menuRepository.selectMenuStoreIdAndMenuNumberAndState(menuDTO.getStoreId(), menuNumber, 2);
                menuEntity.setMenuNumber(menuNumber - 1);
                menuRepository.save(menuEntity);
            }

            // 유저가 선택한 메뉴
            menuEntity = menuRepository.selectMenuStoreIdAndMenuIdAndState(menuDTO.getStoreId(), menuDTO.getMenuId(), 2);
            menuEntity.setMenuNumber(menuNumber);
            menuRepository.save(menuEntity);

            return menuEntity;

        } catch (Exception e) {
            throw new RuntimeException("MenuService.menuSequenceMove() Exception");
        }
    }

    // 메뉴 목록
    public List<MenuDTO> viewMenuList(String storeId) {

        List<MenuEntity> menuEntityList = menuRepository.selectMenuOrderByMenuNumber(storeId, 2);
        List<MenuDTO> menuDTOList = new ArrayList<>();
        for(MenuEntity menuEntity : menuEntityList) {
            MenuDTO menuDTO = new MenuDTO(menuEntity);
            menuDTOList.add(menuDTO);
        }

        return menuDTOList;

    }

}
