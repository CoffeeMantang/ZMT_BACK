package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.OptionDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import com.coffeemantang.ZMT_BACK.security.TokenProvider;
import com.coffeemantang.ZMT_BACK.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners/store")
public class StoreController {

    private final StoreService storeService;

    private final StoreRepository storeRepository;

    private final MenuRepository menuRepository;

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


    //메뉴 추가
    @PostMapping("/addmenu")
    public ResponseEntity<?> addMenu(@AuthenticationPrincipal String memberId, @RequestBody MenuDTO menuDTO) {

        StoreEntity storeEntity = storeRepository.findByMemberId(Integer.parseInt(memberId));

        try {
            //MenuDTO를 MenuEntity로 변환
            MenuEntity tempMenuEntity = MenuDTO.toEntity(menuDTO);
            //storeId 가져와서 추가
            tempMenuEntity.setStoreId(storeEntity.getStoreId());
            //MenuNumber 생성
            tempMenuEntity.setMenuNumber(storeService.createMenuNumber());
            //MenuEntity 생성
            MenuEntity menuEntity = storeService.addMenu(tempMenuEntity);

            MenuDTO responseMenuDTO = MenuDTO.builder()
                    .menuId(menuDTO.getMenuId())
                    .storeId(menuDTO.getStoreId())
                    .menuName(menuDTO.getMenuName())
                    .price(menuDTO.getPrice())
                    .notice(menuDTO.getNotice())
                    .category(menuDTO.getCategory())
                    .tag(menuDTO.getTag())
                    .menuNumber(menuDTO.getMenuNumber())
                    .state(menuDTO.getState())
                    .build();

            return ResponseEntity.ok().body(responseMenuDTO);

        } catch (Exception e) {
            ResponseDTO response = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    //가게 삭제
    @DeleteMapping("/delete/{storeId}")
    public String deleteStore(@PathVariable("storeId") String storeId) {
        storeService.deleteStore(storeId);

        return "redirect:/";
    }
    //메뉴 순서 위로 이동
//    @PostMapping("/menuUp")

    // 옵션 추가
    @PostMapping("/{menuId}")
//    public ResponseEntity<?> addOption(@AuthenticationPrincipal String memberId, @PathVariable("menuId") int menuId, @RequestBody OptionDTO optionDTO) {
    public void addOption(@AuthenticationPrincipal String memberId, @PathVariable("menuId") int menuId, @RequestBody OptionDTO optionDTO) {

        StoreEntity storeEntity = storeRepository.findByMemberId(Integer.parseInt(memberId));
        MenuEntity menuEntity = menuRepository.findByStoreId(storeEntity.getStoreId());
        log.info(menuEntity.getMenuId() + " : 메뉴아이디");
        log.info(menuId + "패스 메뉴 아이디");

    }


}
