package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
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

    private final BookmarkRepository bookmarkRepository;

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

    // 가게 수정
    public StoreEntity updateStore(int memberId, @Valid StoreDTO storeDTO) {

        if(memberId != storeDTO.getMemberId()) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        StoreEntity storeEntity = storeRepository.findByStoreId(storeDTO.getStoreId());
        storeEntity.setName(storeDTO.getName());
        storeEntity.setCategory(storeDTO.getCategory());
        storeEntity.setAddress1(storeDTO.getAddress1());
        storeEntity.setAddress2(storeDTO.getAddress2());
        storeEntity.setState(storeDTO.getState());
        storeEntity.setAddressX(storeDTO.getAddressX());
        storeEntity.setAddressY(storeDTO.getAddressY());
        storeRepository.save(storeEntity);

        return storeEntity;

    }

    // 가게 삭제
    public void deleteStore(int memberId, String storeId) {

        StoreEntity storeEntity = storeRepository.findByStoreId(storeId);

        if (memberId != storeEntity.getMemberId()) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        storeRepository.deleteById(storeId);
    }

    // 가게 목록
    public List<StoreDTO> viewStoreList() {

        List<StoreEntity> storeEntityList = storeRepository.findAll();
        List<StoreDTO> storeDTOList = new ArrayList<>();
        for (StoreEntity storeEntity : storeEntityList) {
            StoreDTO storeDTO = new StoreDTO(storeEntity);
            storeDTOList.add(storeDTO);
        }

        return storeDTOList;
    }

    // 가게 보기(클릭했을 때).
    public StoreDTO viewStore(int memberId, StoreDTO storeDTO) {

        StoreEntity storeEntity = storeRepository.findByStoreId(storeDTO.getStoreId());
        StoreDTO newStoreDTO = new StoreDTO(storeEntity);
        if (memberId != 0){ // 비회원이면 0
            newStoreDTO.setBookmark(bookmarkRepository.countByMemberIdAndStoreId(memberId, storeDTO.getStoreId()));
        }

        return newStoreDTO;

    }
}
