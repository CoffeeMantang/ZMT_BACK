package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.BoardDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreInfoDTO;
import com.coffeemantang.ZMT_BACK.model.StoreInfoEntity;
import com.coffeemantang.ZMT_BACK.persistence.StoreInfoRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreInfoService {

    private final StoreInfoRepository storeInfoRepository;

    private final StoreRepository storeRepository;

    // 가게 정보 추가
    public void addStoreInfo(int memberId, StoreInfoDTO storeInfoDTO) {

        int dtoMemberId = storeRepository.selectMemberIdByStoreId(storeInfoDTO.getStoreId());
        if(memberId != dtoMemberId) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        StoreInfoEntity storeInfoEntity = new StoreInfoEntity();
        storeInfoEntity.setStoreId(storeInfoDTO.getStoreId());
        storeInfoEntity.setNotice(storeInfoDTO.getNotice());
        storeInfoEntity.setTel(storeInfoDTO.getTel());
        storeInfoEntity.setOpenTime1(storeInfoDTO.getOpenTime1());
        storeInfoEntity.setOpenTime2(storeInfoDTO.getOpenTime2());
        storeInfoEntity.setOpenTime3(storeInfoDTO.getOpenTime3());
        storeInfoEntity.setOpenTime4(storeInfoDTO.getOpenTime4());
        storeInfoEntity.setOpenTime5(storeInfoDTO.getOpenTime5());
        storeInfoEntity.setOpenTime6(storeInfoDTO.getOpenTime6());
        storeInfoEntity.setOpenTime7(storeInfoDTO.getOpenTime7());
        storeInfoEntity.setCloseTime1(storeInfoDTO.getCloseTime1());
        storeInfoEntity.setCloseTime2(storeInfoDTO.getCloseTime2());
        storeInfoEntity.setCloseTime3(storeInfoDTO.getCloseTime3());
        storeInfoEntity.setCloseTime4(storeInfoDTO.getCloseTime4());
        storeInfoEntity.setCloseTime5(storeInfoDTO.getCloseTime5());
        storeInfoEntity.setCloseTime6(storeInfoDTO.getCloseTime6());
        storeInfoEntity.setCloseTime7(storeInfoDTO.getCloseTime7());
        storeInfoEntity.setBreakTimeStart1(storeInfoDTO.getBreakTimeStart1());
        storeInfoEntity.setBreakTimeStart2(storeInfoDTO.getBreakTimeStart2());
        storeInfoEntity.setBreakTimeStart3(storeInfoDTO.getBreakTimeStart3());
        storeInfoEntity.setBreakTimeStart4(storeInfoDTO.getBreakTimeStart4());
        storeInfoEntity.setBreakTimeStart5(storeInfoDTO.getBreakTimeStart5());
        storeInfoEntity.setBreakTimeStart6(storeInfoDTO.getBreakTimeStart6());
        storeInfoEntity.setBreakTimeStart7(storeInfoDTO.getBreakTimeStart7());
        storeInfoEntity.setBreakTimeEnd1(storeInfoDTO.getBreakTimeEnd1());
        storeInfoEntity.setBreakTimeEnd2(storeInfoDTO.getBreakTimeEnd2());
        storeInfoEntity.setBreakTimeEnd3(storeInfoDTO.getBreakTimeEnd3());
        storeInfoEntity.setBreakTimeEnd4(storeInfoDTO.getBreakTimeEnd4());
        storeInfoEntity.setBreakTimeEnd5(storeInfoDTO.getBreakTimeEnd5());
        storeInfoEntity.setBreakTimeEnd6(storeInfoDTO.getBreakTimeEnd6());
        storeInfoEntity.setBreakTimeEnd7(storeInfoDTO.getBreakTimeEnd7());
        storeInfoRepository.save(storeInfoEntity);

    }

    public StoreInfoDTO updateStoreInfo(int memberId, StoreInfoDTO storeInfoDTO) {

        int dtoMemberId = storeRepository.selectMemberIdByStoreId(storeInfoDTO.getStoreId());
        if(memberId != dtoMemberId) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        StoreInfoEntity storeInfoEntity = storeInfoRepository.findByStoreinfoId(storeInfoDTO.getStoreinfoId());
        storeInfoEntity.setNotice(storeInfoDTO.getNotice());
        storeInfoEntity.setTel(storeInfoDTO.getTel());
        storeInfoEntity.setOpenTime1(storeInfoDTO.getOpenTime1());
        storeInfoEntity.setOpenTime2(storeInfoDTO.getOpenTime2());
        storeInfoEntity.setOpenTime3(storeInfoDTO.getOpenTime3());
        storeInfoEntity.setOpenTime4(storeInfoDTO.getOpenTime4());
        storeInfoEntity.setOpenTime5(storeInfoDTO.getOpenTime5());
        storeInfoEntity.setOpenTime6(storeInfoDTO.getOpenTime6());
        storeInfoEntity.setOpenTime7(storeInfoDTO.getOpenTime7());
        storeInfoEntity.setCloseTime1(storeInfoDTO.getCloseTime1());
        storeInfoEntity.setCloseTime2(storeInfoDTO.getCloseTime2());
        storeInfoEntity.setCloseTime3(storeInfoDTO.getCloseTime3());
        storeInfoEntity.setCloseTime4(storeInfoDTO.getCloseTime4());
        storeInfoEntity.setCloseTime5(storeInfoDTO.getCloseTime5());
        storeInfoEntity.setCloseTime6(storeInfoDTO.getCloseTime6());
        storeInfoEntity.setCloseTime7(storeInfoDTO.getCloseTime7());
        storeInfoEntity.setBreakTimeStart1(storeInfoDTO.getBreakTimeStart1());
        storeInfoEntity.setBreakTimeStart2(storeInfoDTO.getBreakTimeStart2());
        storeInfoEntity.setBreakTimeStart3(storeInfoDTO.getBreakTimeStart3());
        storeInfoEntity.setBreakTimeStart4(storeInfoDTO.getBreakTimeStart4());
        storeInfoEntity.setBreakTimeStart5(storeInfoDTO.getBreakTimeStart5());
        storeInfoEntity.setBreakTimeStart6(storeInfoDTO.getBreakTimeStart6());
        storeInfoEntity.setBreakTimeStart7(storeInfoDTO.getBreakTimeStart7());
        storeInfoEntity.setBreakTimeEnd1(storeInfoDTO.getBreakTimeEnd1());
        storeInfoEntity.setBreakTimeEnd2(storeInfoDTO.getBreakTimeEnd2());
        storeInfoEntity.setBreakTimeEnd3(storeInfoDTO.getBreakTimeEnd3());
        storeInfoEntity.setBreakTimeEnd4(storeInfoDTO.getBreakTimeEnd4());
        storeInfoEntity.setBreakTimeEnd5(storeInfoDTO.getBreakTimeEnd5());
        storeInfoEntity.setBreakTimeEnd6(storeInfoDTO.getBreakTimeEnd6());
        storeInfoEntity.setBreakTimeEnd7(storeInfoDTO.getBreakTimeEnd7());
        storeInfoRepository.save(storeInfoEntity);

        StoreInfoDTO newStoreInfoDTO = new StoreInfoDTO(storeInfoEntity);

        return newStoreInfoDTO;

    }


    // 가게 정보 보기
    public StoreInfoDTO viewStoreInfo(StoreDTO storeDTO) {

        StoreInfoEntity storeInfoEntity = storeInfoRepository.findByStoreId(storeDTO.getStoreId());
        StoreInfoDTO storeInfoDTO = new StoreInfoDTO(storeInfoEntity);

        return storeInfoDTO;

    }
}