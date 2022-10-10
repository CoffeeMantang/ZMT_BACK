package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.OptionDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;

    private final MenuRepository menuRepository;

    //옵션 추가
    public OptionEntity addOption(final OptionEntity optionEntity, int memberId) {

        int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(optionEntity.getMenuId());
        log.info(selectMemberIdByMenuId + "멤버아이디");
        if (optionEntity == null || optionEntity.getMenuId() == 0) {
            log.warn("StoreService.addOption() : optionEntity에 내용이 부족해요");
            throw new RuntimeException("StoreService.addOption() : optionEntity에 내용이 부족해요");
        }
        else if (memberId != selectMemberIdByMenuId) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        return optionRepository.save(optionEntity);

    }

    //옵션 번호 생성 메서드
    public int createOptionNumber(int menuId) {

        //optionNumber 컬럼만 가져옴
        List<Integer> list = optionRepository.selectAllOptionNumber(menuId);

        //리스트가 비어있으면 1, 아니면 최대값 + 1
        if (list.isEmpty()) {
            return 1;
        } else {
            //optionNumber에서 최대값
            int max = Collections.max(list);
            return max + 1;
        }

    }

    // 메뉴 순서 이동
    public OptionEntity optionSequenceMove(OptionDTO optionDTO, int memberId, int move) {

        try {
            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(optionDTO.getMenuId());


            if (memberId != selectMemberIdByMenuId) {
                log.warn("StoreService.optionSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
                throw new RuntimeException("StoreService.optionSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
            }

            int optionNumber = optionDTO.getOptionNumber();
            OptionEntity optionEntity;
            if(move == 1) { // move == 1 : /up
                optionNumber -= 1;
                // 순서가 내려갈 메뉴
                optionEntity = optionRepository.findByMenuIdAndOptionNumber(optionDTO.getMenuId(), optionNumber);
                optionEntity.setOptionNumber(optionNumber + 1);
                optionRepository.save(optionEntity);
            } else { // move == 2 : /down
                optionNumber += 1;
                // 순서가 올라갈 메뉴
                optionEntity = optionRepository.findByMenuIdAndOptionNumber(optionDTO.getMenuId(), optionNumber);
                optionEntity.setOptionNumber(optionNumber - 1);
                optionRepository.save(optionEntity);
            }

            // 유저가 선택한 메뉴
            optionEntity = optionRepository.findByMenuIdAndOptionId(optionDTO.getMenuId(), optionDTO.getOptionId());
            optionEntity.setOptionNumber(optionNumber);
            optionRepository.save(optionEntity);

            return optionEntity;

        } catch (Exception e) {
            throw new RuntimeException("StoreService.optionSequenceMove() Exception");
        }
    }
}
