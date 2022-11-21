package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.ImageDTO;
import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
// Store에 저장된 내용을 가져올 때 사용
// StoreRepository를 이용해 가게를 CRUD
public class StoreService {

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    private final BookmarkRepository bookmarkRepository;
    @Autowired
    private final MenuRepository menuRepository;
    @Autowired
    private final MenuImgRepository menuImgRepository;
    @Autowired
    private final ReviewRepository reviewRepository;
    @Autowired
    private final MemberRocationRepository memberRocationRepository;
    @Autowired
    private final ChargeRepository chargeRepository;

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

    // 가게이름으로 검색하기 - paging
    public List<StoreDTO> getSearchResult(final String keyword, final int page, final String sort, String address) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByNameOrderByReviewScore(10, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb(entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 가게이름으로 검색하기 - paging
    public List<StoreDTO> getSearchResultforMember(final String keyword, final int page, final String sort, final int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    entities = storeRepository.findByNameOrderByReviewScore(10, ((page-1)*10), address, keyword);
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb(entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 메뉴명으로 검색하기 = nonLogin
    public List<StoreDTO> getSearchByMenuName(final String keyword, final int page, final String sort, final String address) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 메뉴명으로 검색하기 - require login
    public List<StoreDTO> getSearchByMenuNameForMember(final String keyword, final int page, final String sort, final int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByMenuNameOrderByOrderCount(10, ((page-1)*10), address, keyword);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 회원아이디와 가게아이디로 배달비 찾기
    public int findCharge(final int memberId, final String storeId) throws Exception{
        try{
            MemberRocationEntity mrEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1);
            String[] address1 = mrEntity.getAddress1().split(" "); // 멤버의 address1 가져오기
            String dong = null;
            // 읍 면 동 가져오기
            for(int i = 0 ; i < address1.length ; i++){
                if(address1[i].endsWith("읍") || address1[i].endsWith("면") || address1[i].endsWith("동")){
                    dong = address1[i];
                    break;
                }
            }
            // 가져온 읍면동으로 배달비 검색
            List<ChargeEntity> charge = chargeRepository.findByDongContainingAndStoreId(dong, storeId);
            if(charge.size() > 0){
                return charge.get(0).getCharge();
            }else{
                return 99999; // 99999면 검색실패한경우 -> 배달불가능으로 표시해주면 됩니다.
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 카테고리로 검색하기(로그인 없이)
    public List<StoreDTO> getCategorySearch(final int category, final int page, final String sort, String address) throws Exception{
        try{
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb() ).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // 카테고리로 검색하기(로그인시)
    public List<StoreDTO> getCategorySearchWithLogin(final int category, final int page, final String sort, int memberId) throws Exception{
        try{
            // 멤버아이디로 주소가져오기
            MemberRocationEntity memberRocationEntity = memberRocationRepository.findAddress1ByMemberIdAndState(memberId, 1); //대표주소 가져옴
            String address = memberRocationEntity.getAddress1();
            List<StoreEntity> entities = new ArrayList<>();
            // 1. 가게이름으로 검색한 entity 가져오기(10개씩!)
            switch (sort){
                case("orderCount"):// 주문수 정렬
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
                    break;
                case("reviewScore"):
                    // 리뷰평점 정렬
                    break;
                case("charge"): // 배달팁 낮은순
                    break;
                case("distance"): // 거리순
                    break;
                default: // 기본값은 주문순으로
                    entities = storeRepository.findByCategoryOrderByOrderCount(10, ((page-1)*10), address, category);
            }
            List<StoreDTO> dtos = new ArrayList<>();
            // 2. DTO list로 변환
            for(StoreEntity entity : entities){
                // 2-1. 리뷰 평점 가져오기
                Optional<Double> score = reviewRepository.findReviewScoreByStoreId(entity.getStoreId());
                StoreDTO dto = StoreDTO.builder().storeId(entity.getStoreId())
                        .state(entity.getState()).name(entity.getName())
                        .thumb("http://localhost:8080/images/store/" + entity.getThumb()).build();
                if(score.isPresent()){
                    dto.setScore(score.get());
                }
                dtos.add(dto);
            }
            return dtos;
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
