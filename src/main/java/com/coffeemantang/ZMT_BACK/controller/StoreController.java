package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/partners/store")
public class StoreController {
    @Autowired
    private StoreService storeService;

    // 새로운 가게 생성
    @PostMapping("/create")
    public ResponseEntity<?> createStore(@AuthenticationPrincipal int memberId, @RequestBody StoreDTO storeDTO){
        try {
            // StoreDTO를 StoreEntity로 변환
            StoreEntity tempStoreEntity = StoreDTO.toEntity(storeDTO);
            // 생성 당시에는 id가 없어야 하기 때문에 null로 초기화
            tempStoreEntity.setStoreId(null);
            // 생성일 현재시간을 초기화
            tempStoreEntity.setJoinDay(LocalDateTime.now());
            // AuthenticationPrincipal에서 넘어온 memberId set
            tempStoreEntity.setMemberId(memberId);
            // 가게의 상태 초기화
            tempStoreEntity.setState(0);
            // StoreService를 이용해 StoreEntity 생성
            StoreEntity storeEntity = storeService.create(tempStoreEntity);
            // 리턴할 Store DTO 초기화
            StoreDTO responseStoreDTO = StoreDTO.builder()
                    .storeId(storeEntity.getStoreId())
                    .memberId(storeEntity.getMemberId())
                    .name(storeEntity.getName())
                    .joinDay(storeEntity.getJoinDay())
                    .category(storeEntity.getCategory())
                    .address1(storeEntity.getAddress1())
                    .address2(storeEntity.getAddress2())
                    .addressX(storeEntity.getAddressX())
                    .addressY(storeEntity.getAddressY())
                    .build();
            // 응답
            return ResponseEntity.ok().body(responseStoreDTO);
        }catch (Exception e){
            // 예외 발생 시 error에 e.getMessage() 넣어 리턴
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
