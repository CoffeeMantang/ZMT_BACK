package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.Recommend.ContentElement;
import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.dto.RecommendDTO;
import com.coffeemantang.ZMT_BACK.dto.StoreDTO;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
// 메뉴추천 기능을 위한 Service
public class RecommendService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MemberRocationRepository memberRocationRepository;
    @Autowired
    private OrderMenuRepository orderMenuRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MenuImgRepository menuImgRepository;

    private CosineSimilarity cosineSimilarity = new CosineSimilarity();

    // 사용자 기반 추천 : 메뉴아이디의 리스트를 넘기면 해당 메뉴들은 빼고 추천해줌 더 추천할게 없으면 null 리턴
    public List<RecommendDTO> userRecommend(final int memberId, final List<Integer> menuIdList) throws Exception {
        try{
            // 1. 현재 회원의 Entity 가져오기
            MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
            // 2. 현재 회원이 설정한 주소 가져오기
            Optional<MemberRocationEntity> oMemberRocation = memberRocationRepository.findAllByMemberIdAndState(memberId, 1);
            // 3. 현재 회원이 설정한 주소에서 '시군구'와 '읍면동'을 가져옴
            String[] address1 = oMemberRocation.get().getAddress1().split(" ");
            String dong = null;
            String si = null;

            for(int i = 0; i < address1.length; i++) {
                if (address1[i].endsWith("읍")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("면")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("동")) {
                    dong = address1[i];
                    break;
                }
            }
            for(int i = 0; i < address1.length; i++) {
                if (address1[i].endsWith("시")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("군")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("구")) {
                    si = address1[i];
                    break;
                }
            }
            log.warn(si + dong);
            // 4. 현재 회원과 같은 동의 로케이션을 가진 멤버를 가져옴
            List<Integer> memberList = memberRocationRepository.findMemberIdByAddress(si + " " + dong);
            log.warn("같은 동에 사는 사람들의 수: " + memberList.size());
            // 5. 현재 회원이 주문한 메뉴 리스트 가져오기(단, 현재 주소지에서 주문 가능한 메뉴)
            List<Integer> menuList = orderMenuRepository.findMenuIdByMemberIdAndAddress(si + " " + dong, memberId);
            // 6. 현재 회원의 Map 생성
            Map<CharSequence, Integer> curMemberMap = new HashMap<>(); // key: menuId, value: 주문횟수
            for(Integer menuId : menuList){
                int count = orderMenuRepository.countByMemberIdAndMenuId(memberId, menuId);
                curMemberMap.put(String.valueOf(menuId), count);
            }
            // 7. 같은 지역의 사는 유저들의 Map 가져와서 cos 유사도 비교
            Map<Integer, Double> cosMap = new HashMap<>(); // K : memberId, V : cosSimilarity
            for (Integer id : memberList) {
                Map<CharSequence, Integer> memberMap = new HashMap<>();
                for (Integer menuId : menuList){
                    int count = orderMenuRepository.countByMemberIdAndMenuId(id, menuId);
                    memberMap.put(String.valueOf(menuId), count);
                    log.warn(id + "의 " + menuId + " 주문횟수는 " + count);
                }
                double sim = cosineSimilarity.cosineSimilarity(curMemberMap, memberMap); // 코사인유사도 계산값
                log.warn(id + "의 코사인 유사도는 " + sim);
                if(sim > 0){
                    cosMap.put(id, sim); // cosMap에 추가
                }
            }
            // 8. 유사도 내림차순으로 정렬
            List<Integer> sortedMemberIdList = new ArrayList<>(cosMap.keySet());
            sortedMemberIdList.sort((s1, s2) -> cosMap.get(s2).compareTo(cosMap.get(s1)));

            // 9. 가장 유사도가 높은 유저부터 차례대로 비교해 10개 추출
            log.warn("가장 유사도가 높은 유저부터 차례대로 비교해 10개 추출");
            int count = 0;
            Comparator<Integer> comparator = Comparator.reverseOrder(); // 내림차순 정렬을 위한 Comparator
            List<Integer> similarList = new ArrayList<>();
            for(Integer otherMember : sortedMemberIdList){
                // 9-1. 유사도가 높은 유저가 주문한 메뉴들 가져오기(단, 주문가능한 메뉴에 한정)
                log.warn("9-1. 유사도가 높은 유저 " + otherMember);
                List<Integer> otherMemberMenuList = orderMenuRepository.findMenuIdByMemberIdAndAddressSortDESC(si + " " + dong, otherMember);
                Map<Integer, Integer> otherMemberMap = new TreeMap<>(comparator); // K: menuId, V: orderCount
                // 9-2. 유사도가 높은 유저가 주문한 메뉴들의 주문횟수 가져오기
                log.warn("9-2");
                for(Integer menuId : otherMemberMenuList){
                    otherMemberMap.put(menuId, orderMenuRepository.countByMemberIdAndMenuId(otherMember, menuId));
                }
                // 9-3. 현재 유저가 주문하지 않은 메뉴만 추출
                /* log.warn("9-3");
                curMemberMap.forEach(
                        (k, v) -> otherMemberMap.remove(k)
                );*/
                // 9-4. 위에서 순서대로 메뉴 추출 (단, 주문횟수 3회 이상인 메뉴만)
                log.warn("9-4");
                List<Integer> otherMenuIdList = new ArrayList<>(otherMemberMap.keySet());
                otherMenuIdList.sort((s1, s2) -> otherMemberMap.get(s2).compareTo(otherMemberMap.get(s1)));
                for(Integer key : otherMenuIdList){
                    int value = otherMemberMap.get(key);
                    // 매개변수로 들어온 리스트에 이미 해당 메뉴가 있는지 체크
                    if(!menuIdList.contains(value) && value >= 3){
                        // 리턴할 리스트에 없는지도 체크
                        if(!similarList.contains(value)) {
                            // 없는 경우에만 similarList에 삽입
                            similarList.add(key);
                            count++;
                        }
                    }
                    log.warn(key + "의 밸류는 " + value);
                }
                // 9-5. 메뉴 10개를 채우면 종료
                log.warn("9-5");
                if(count >= 10){
                    break;
                }
            }
            if(count == 0){ // 끝까지 돌았는데 찾은게 0개면 null 리턴
                return null;
            }
            // 10. 추출한 menuId리스트로 recommendDTO 생성
            log.warn("추출한 menuId리스트로 recommendDTO 생성");
            List<RecommendDTO> resultList = new ArrayList<>();
            for (Integer menuId : similarList){
               MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
               StoreEntity storeEntity = storeRepository.findByMenuId(menuId);
               MenuImgEntity menuImgEntity = menuImgRepository.findTop1ByMenuId(menuId);
               RecommendDTO recommendDTO = RecommendDTO.builder()
                       .menuName(menuEntity.getMenuName())
                       .menuId(menuId)
                       .menuPic("http://localhost:8080/images/menu/" + menuImgEntity.getPath())
                       .storeId(storeEntity.getStoreId())
                       .state(0)
                       .storeName(storeEntity.getName()).build();
               if(menuEntity.getState() != 0 || storeEntity.getState() != 1){ // 현재 주문 불가능한 메뉴 체크
                   recommendDTO.setState(1);
               }
               resultList.add(recommendDTO);
            }
            log.warn("사용자 기반 추천 resultList size: " + resultList.size());
            return resultList;
        }
        catch(Exception e){
            log.warn("RecommendService.userRecommend() : Exception");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 컨텐츠 기반 추천
    public List<RecommendDTO> contentRecommend(final int memberId, final List<Integer> menuIdList) throws Exception{
        try{
            // 1. 현재 회원의 Entity 가져오기
            MemberEntity memberEntity = memberRepository.findByMemberId(memberId);
            // 2. 현재 회원이 설정한 주소 가져오기
            Optional<MemberRocationEntity> oMemberRocation = memberRocationRepository.findAllByMemberIdAndState(memberId, 1);
            // 3. 현재 회원이 설정한 주소에서 '시군구'와 '읍면동'을 가져옴
            String[] address1 = oMemberRocation.get().getAddress1().split(" ");
            String dong = null;
            String si = null;
            for(int i = 0; i < address1.length; i++) {
                if (address1[i].endsWith("읍")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("면")) {
                    dong = address1[i];
                    break;
                }
                if (address1[i].endsWith("동")) {
                    dong = address1[i];
                    break;
                }
            }
            for(int i = 0; i < address1.length; i++) {
                if (address1[i].endsWith("시")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("군")) {
                    si = address1[i];
                    break;
                }
                if (address1[i].endsWith("구")) {
                    si = address1[i];
                    break;
                }
            }
            // 4. 현재 회원이 주문한 메뉴 리스트 가져오기(주문횟수 순으로 정렬, 주문횟수는 3회 이상)
            List<Integer> menuList = orderMenuRepository.findMenuIdByMemberIdAndAddressSortDESC(si + " " + dong, memberId);
            Map<Integer, Map<CharSequence, Integer>> menuMap = new LinkedHashMap<>();
            String[] elements = ContentElement.getElements(); // 컨텐츠 기반 추천에 필요한 컬럼들 가져오기
            // 5. 현재 회원이 주문한 메뉴들의 태그 가져와서 맵 만들기
            for(Integer menuId : menuList){
                log.warn("찾은 내 메뉴 " + menuId);
                MenuEntity tag = menuRepository.findByMenuId(menuId);
                // 5-1. 태그를 요소별로 나누어 맵 만들기
                Map<CharSequence, Integer> tempMap = new HashMap<>();
                for(int i = 0; i < elements.length; i++){
                    if(tag.getTag().contains(elements[i])){ // 해당하는 요소가 있는 메뉴인 경우 value에 1
                        tempMap.put(elements[i], 1);
                        log.warn(tag.getMenuId() + "의 요소들 " + elements[i] + 1);
                    }else {
                        tempMap.put(elements[i], 0); // 아닌경우에는 0
                    }
                }
                // 5-2. 만든 맵을 menuMap에 추가
                menuMap.put(menuId, tempMap);
            }
            // 6. 현재 위치에서 주문 가능한 메뉴 모두 가져오기
            List<MenuEntity> canMenuIdList = menuRepository.findMenuByAddress(si + " " + dong);
            // 7. 현재 위치에서 주문 가능한 메뉴들의 Map 만들기
            Map<Integer, Map<CharSequence, Integer>> canMenuMap = new HashMap<>();
            for(MenuEntity menuEntity : canMenuIdList){
                // 7-1. 태그를 요소별로 나누어 맵 만들기
                Map<CharSequence, Integer> tempMap = new HashMap<>();
                for(int i = 0; i < elements.length; i++){
                    if(menuEntity.getTag().contains(elements[i])){ // 해당하는 요소가 있는 메뉴인 경우 value에 1
                        tempMap.put(elements[i], 1);
                        log.warn(menuEntity.getMenuId() + "의 요소들 " + elements[i] + 1);
                    }else{
                        tempMap.put(elements[i], 0); // 아닌경우에는 0
                    }
                }
                // 7-2. 만든 맵을 canMenuMap에 추가
                canMenuMap.put(menuEntity.getMenuId(), tempMap);
            }
            // 8. 가장 많이 주문한 메뉴부터 cos유사도 계산해 상위 3개 메뉴씩 가져와 10개 모으기(단, menuIdList에 있는것 제외)
            List<Integer> similarityMenuList = new ArrayList<>(); // 결과물로 나오는 메뉴들을 담을 List
            int count = 0; // 갯수를 카운트하기 위한 변수
            for(Integer menuId : menuList){
                Map<Integer, Double> similarityMap = new HashMap<>(); // K: menuId, V: 유사도
                // 8-1. 만든 맵을 이용해 주문 가능한 모든 메뉴들과 유사도 비교해 저장
                Iterator<Integer> tempKeys = canMenuMap.keySet().iterator();
                while(tempKeys.hasNext()){
                    int key = tempKeys.next();
                    double similarity = cosineSimilarity.cosineSimilarity(menuMap.get(menuId), canMenuMap.get(key));
                    if(similarity > 0){ // 유사도가 0보다 클때만 넣음
                        similarityMap.put(key, similarity);
                    }
                    log.warn(menuId + "와 비교한 컨텐츠기반의 유사도" + key + " " + similarity);
                }
                // 8-2. 내가 주문한 메뉴와 주문가능한 메뉴들의 유사도 맵을 value값 내림차순으로 정렬
                List<Integer> sortedMenuIdList = new ArrayList<>(similarityMap.keySet());
                sortedMenuIdList.sort((s1, s2) -> similarityMap.get(s2).compareTo(similarityMap.get(s1)));
                // 8-3. 매개변수로 들어온 menuIdList와 비교해서 없는 메뉴만 추가(3개만)
                int tempCount = 0; // 갯수 카운트를 위한 변수. 3개만 꺼내야 함
                for(Integer sortedMenuId : sortedMenuIdList){
                    if(!menuIdList.contains(sortedMenuId) && !similarityMenuList.contains(sortedMenuId)){
                        similarityMenuList.add(sortedMenuId);
                        count++;
                        tempCount++;
                        if(tempCount >= 3 || count >= 10) break;
                    }
                }
                if(tempCount >= 3 || count >= 10) break;
            }
            // 9. 추출한 menuId리스트로 recommendDTO 생성
            List<RecommendDTO> resultList = new ArrayList<>();
            for (Integer menuId : similarityMenuList){
                MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
                StoreEntity storeEntity = storeRepository.findByMenuId(menuId);
                MenuImgEntity menuImgEntity = menuImgRepository.findTop1ByMenuId(menuId);
                RecommendDTO recommendDTO = RecommendDTO.builder()
                        .menuName(menuEntity.getMenuName())
                        .menuId(menuId)
                        .menuPic("http://localhost:8080/images/menu/" + menuImgEntity.getPath())
                        .storeId(storeEntity.getStoreId())
                        .state(0)
                        .storeName(storeEntity.getName()).build();
                if(menuEntity.getState() != 0 || storeEntity.getState() != 1){ // 현재 주문 불가능한 메뉴 체크
                    recommendDTO.setState(1);
                }
                resultList.add(recommendDTO);
            }
            return resultList;
        }catch (Exception e){
            log.warn("RecommendService.contentRecommend() : Exception");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
