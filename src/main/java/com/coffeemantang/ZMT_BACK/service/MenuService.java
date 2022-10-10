package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    //메뉴 추가
    public MenuEntity addMenu(final MenuEntity menuEntity, int memberId) {

        int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuEntity.getMenuId());

        if (menuEntity == null || menuEntity.getStoreId() == null) {
            log.warn("StoreService.addMenu() : menuEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.addMenu() : menuEntity에 내용이 부족해요");
        }
        else if (memberId != selectMemberIdByMenuId) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        return menuRepository.save(menuEntity);

    }

    //메뉴 번호 생성 메서드
    public int createMenuNumber(String storeId) {

        //menuNumber 컬럼만 가져옴
        List<Integer> list = menuRepository.selectAllMenuNumber(storeId);

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
                log.warn("StoreService.menuSequenceUp() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
                throw new RuntimeException("StoreService.menuSequenceUp() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
            }

            int menuNumber = menuDTO.getMenuNumber();
            MenuEntity menuEntity;
            if(move == 1) { // move == 1 : /up
                menuNumber -= 1;
                // 순서가 내려갈 메뉴
                menuEntity = menuRepository.findByStoreIdAndMenuNumber(menuDTO.getStoreId(), menuNumber);
                menuEntity.setMenuNumber(menuNumber + 1);
                menuRepository.save(menuEntity);
            } else { // move == 2 : /down
                menuNumber += 1;
                // 순서가 올라갈 메뉴
                menuEntity = menuRepository.findByStoreIdAndMenuNumber(menuDTO.getStoreId(), menuNumber);
                menuEntity.setMenuNumber(menuNumber - 1);
                menuRepository.save(menuEntity);
            }

            // 유저가 선택한 메뉴
            menuEntity = menuRepository.findByStoreIdAndMenuId(menuDTO.getStoreId(), menuDTO.getMenuId());
            menuEntity.setMenuNumber(menuNumber);
            menuRepository.save(menuEntity);

            return menuEntity;

        } catch (Exception e) {
            throw new RuntimeException("StoreService.menuSequenceUp() Exception");
        }
    }

//    // 메뉴 순서 아래로 이동
//    public MenuEntity menuSequenceDown(MenuDTO menuDTO, int memberId) {
//
//        try {
//            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuDTO.getMenuId());
//
//            if (memberId != selectMemberIdByMenuId) {
//                log.warn("StoreService.menuSequenceDown() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
//                throw new RuntimeException("StoreService.menuSequenceDown() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
//            }
//
//            int menuNumber = menuDTO.getMenuNumber();
//            menuNumber += 1;
//            // 순서가 올라갈 메뉴
//            MenuEntity menuEntity = menuRepository.findByStoreIdAndMenuNumber(menuDTO.getStoreId(), menuNumber);
//            menuEntity.setMenuNumber(menuNumber - 1);
//            menuRepository.save(menuEntity);
//
//            // 순서가 내려갈 메뉴
//            menuEntity = menuRepository.findByStoreIdAndMenuId(menuDTO.getStoreId(), menuDTO.getMenuId());
//            menuEntity.setMenuNumber(menuNumber);
//            menuRepository.save(menuEntity);
//
//            return menuEntity;
//
//        } catch (Exception e) {
//            throw new RuntimeException("StoreService.menuSequenceDown() Exception");
//        }
//    }
}
