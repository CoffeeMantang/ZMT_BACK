package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.MemberRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
// Store에 저장된 내용을 가져올 때 사용
// StoreRepository를 이용해 가게를 CRUD
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MemberRepository memberRepository;

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
}
