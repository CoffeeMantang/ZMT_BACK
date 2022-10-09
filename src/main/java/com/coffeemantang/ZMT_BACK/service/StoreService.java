package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.OptionRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
// Store에 저장된 내용을 가져올 때 사용
// StoreRepository를 이용해 가게를 CRUD
public class StoreService {

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    private final MenuRepository menuRepository;

    private final OptionRepository optionRepository;

    // 가게 생성
    public StoreEntity create(final StoreEntity storeEntity){
        if(storeEntity == null || storeEntity.getMemberId() == 0){
            log.warn("StoreService.create() : storeEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.create() : storeEntity에 내용이 부족해요");
        } else if (memberRepository.findByMemberId(storeEntity.getMemberId()).getType() != 1) {
            // 사업자 회원이 아니면 오류 리턴
            log.warn("사업자 회원이 아닌 회원이 가게생성 시도");
            throw new RuntimeException("사업자 회원이 아닌 회원이 가게생성 시도");
        }
        return storeRepository.save(storeEntity);
    }

    //메뉴 추가
    public MenuEntity addMenu(final MenuEntity menuEntity) {

        if (menuEntity == null || menuEntity.getStoreId() == null) {
            log.warn("StoreService.addMenu() : menuEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.addMenu() : menuEntity에 내용이 부족해요");
        }

        return menuRepository.save(menuEntity);

    }

    //메뉴 번호 생성 메서드
    public int createMenuNumber() {

        //menuNumber 컬럼만 가져옴
        List<Integer> list = menuRepository.selectAllMenuNumber();

        //리스트가 비어있으면 1, 아니면 최대값 + 1
        if (list.isEmpty()) {
            return 1;
        } else {
            //menuNumber에서 최대값
            int max = Collections.max(list);
            return max + 1;
        }

    }

    //옵션 추가
    public OptionEntity addOption(final OptionEntity optionEntity) {

        if (optionEntity == null || optionEntity.getMenuId() == 0) {
            log.warn("StoreService.addOption() : optionEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.addOption() : optionEntity에 내용이 부족해요");
        }

        return optionRepository.save(optionEntity);

    }

    public void deleteStore(String storeId) {
        storeRepository.deleteById(storeId);
    }
}
