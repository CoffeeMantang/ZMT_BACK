package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.ImageDTO;
import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.StatsDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.MenuImgEntity;
import com.coffeemantang.ZMT_BACK.model.OptionEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
// Store에 저장된 내용을 가져올 때 사용
// StoreRepository를 이용해 가게를 CRUD
public class StoreService {

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    private final BookmarkRepository bookmarkRepository;

    private final OrderListRepository orderListRepository;
    @Autowired
    private final MenuRepository menuRepository;
    @Autowired
    private final MenuImgRepository menuImgRepository;
    @Autowired
    private final ReviewRepository reviewRepository;

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

    // 가게페이지 정보 가져오기(로그인 없이)
    public StoreDTO nonLoginStore(final String storeId) throws Exception{
        try{
            StoreEntity storeEntity = storeRepository.findByStoreId(storeId);
            List<MenuEntity> menuList = menuRepository.selectMenuOrderByMenuNumber(storeId, 2);
            List<MenuDTO> menuDTOList = new ArrayList<>();
            List<ImageDTO> imageList = new ArrayList<>();
            ImageDTO imageDTO = ImageDTO.builder().build();
            if(storeEntity.getThumb() != null){
                imageDTO.setImage("http://localhost:8080/images/store/" + storeEntity.getThumb());
            }
            imageList.add(imageDTO);

            for(MenuEntity menuEntity : menuList) {
                // 1. 메뉴의 첫번째 이미지 가져오기
                MenuImgEntity menuImgEntity = null;
                menuImgEntity = menuImgRepository.findTop1ByMenuId(menuEntity.getMenuId());
                // 2. StoreDTO에 MenuDTO List 넣기
                MenuDTO menuDTO = MenuDTO.builder().menuId(menuEntity.getMenuId())
                        .menuName(menuEntity.getMenuName())
                        .notice(menuEntity.getNotice())
                        .category(menuEntity.getCategory())
                        .price(menuEntity.getPrice()).build();
                if (menuImgEntity != null) {
                    menuDTO.setMenuPic("http://localhost:8080/images/menu/" + menuImgEntity.getPath());
                }
                menuDTOList.add(menuDTO);
                if (menuImgEntity != null) {
                    ImageDTO tempImageDTO = ImageDTO.builder().image("http://localhost:8080/images/menu/" + menuImgEntity.getPath()).build();
                    imageList.add(tempImageDTO);
                }
            }
            StoreDTO storeDTO = StoreDTO.builder()
                    .storeId(storeId)
                    .name(storeEntity.getName())
                    .images(imageList)
                    .menuList(menuDTOList)
                    .min(storeEntity.getMin())
                    .state(storeEntity.getState()).build();

            // 3. 평균리뷰점수 가져와서 추가
            Optional<Double> oReviewScore = reviewRepository.findReviewScoreByStoreId(storeId);
            if(oReviewScore.isPresent()){
                storeDTO.setScore(Math.round((oReviewScore.get()*10)/10.0)); //소숫점 두번째에서 반올림
            }else{
                storeDTO.setScore(0.0); //리뷰가 없으면 0으로
            }
            // 4. 리뷰갯수 가져와서 추가
            Long reviewCount = reviewRepository.countByStoreId(storeId);
            storeDTO.setReviewCount(reviewCount);

            return storeDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 기간별 수익
    public StatsDTO viewStats(int memberId, HashMap<String, String> map) {

        if (memberId != storeRepository.selectMemberIdByStoreId(map.get("storeId"))) {
            log.warn("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("StoreService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        String year = map.get("year");
        String month = map.get("month");
        String day = map.get("day");

        try {

            StatsDTO statsDTO = new StatsDTO();
            String storeId = map.get("storeId");
            List<MenuDTO> menuDTOList = menuRepository.findByStoreId(storeId);
            List<MenuDTO> statsMenuDTOList = new ArrayList<>();
            int profit = 0;

            if (0 == Integer.parseInt(month)) {

                profit = orderListRepository.selectPriceByYear(2, storeId, year);

                for (MenuDTO menuDTO : menuDTOList) {
                    int count = orderListRepository.selectQuantityCountByYear(2, menuDTO.getStoreId(), menuDTO.getMenuId(), year);
                    int total = menuDTO.getPrice() * count;
                    MenuDTO statsMenuDTO = new MenuDTO(menuDTO.getMenuName(), menuDTO.getPrice(), count, total);
                    statsMenuDTOList.add(statsMenuDTO);
                }

            } else if (0 == Integer.parseInt(day)) {

                profit = orderListRepository.selectPriceByMonth(2, storeId, year + "-" + month);

                for (MenuDTO menuDTO : menuDTOList) {
                    int count = orderListRepository.selectQuantityCountByMonth(2, menuDTO.getStoreId(), menuDTO.getMenuId(), year + "-" + month);
                    int total = menuDTO.getPrice() * count;
                    MenuDTO statsMenuDTO = new MenuDTO(menuDTO.getMenuName(), menuDTO.getPrice(), count, total);
                    statsMenuDTOList.add(statsMenuDTO);
                }

            } else if (0 < Integer.parseInt(day)) {

                profit = orderListRepository.selectPriceByDay(2, storeId, year + "-" + month + "-" + day);

                for (MenuDTO menuDTO : menuDTOList) {
                    int count = orderListRepository.selectQuantityCountByDay(2, menuDTO.getStoreId(), menuDTO.getMenuId(), year + "-" + month + "-" + day);
                    int total = menuDTO.getPrice() * count;
                    MenuDTO statsMenuDTO = new MenuDTO(menuDTO.getMenuName(), menuDTO.getPrice(), count, total);
                    statsMenuDTOList.add(statsMenuDTO);
                }

            }

            Comparator<MenuDTO> comparingMenuDTO = Comparator.comparing(MenuDTO::getCount, Comparator.reverseOrder());
            List<MenuDTO> newStatsMenuDTOList = statsMenuDTOList.stream().sorted(comparingMenuDTO).collect(Collectors.toList());
            statsDTO.setMenuDTOList(newStatsMenuDTOList);
            statsDTO.setProfit(profit);

            return statsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("기간별 수익을 가져오는 중 에러 발생");
        }
    }

}
