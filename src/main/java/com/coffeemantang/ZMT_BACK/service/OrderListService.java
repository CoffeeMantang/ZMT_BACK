package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderListService {

    private final MenuRepository menuRepository;

    private final OptionRepository optionRepository;

    @Autowired
    private final OrderListRepository orderListRepository;

    private final OrderMenuRepository orderMenuRepository;

    private final OrderOptionRepository orderOptionRepository;

    private final MemberRocationRepository memberRocationRepository;

    private final ChargeRepository chargeRepository;
    @Autowired
    private final StoreRepository storeRepository;

    // 장바구니 추가
    @Transactional
    public OrderListEntity addBasket(int memberId, @Valid OrderMenuDTO orderMenuDTO) {
        try{
            OrderListEntity orderListEntity;
            String storeId = menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId());
            int charge = selectCharge(memberId, storeId);
            // state가 0인 오더리스트가 없으면 오더리스트 생성
            if (orderListRepository.countByMemberIdAndState(memberId, 0) == 0) {
                orderListEntity = new OrderListEntity();
                orderListEntity.setMemberId(memberId);
                orderListEntity.setStoreId(storeId);
                orderListEntity.setMemberrocationId(memberRocationRepository.selectMemberrocationIdByMemberIdAndState(memberId));
                int price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
                for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                    price += optionRepository.selectPriceByOptionId(orderOptionDTO.getOptionId());
                }
                orderListEntity.setCharge(charge);
                orderListEntity.setPrice((price * orderMenuDTO.getQuantity()));
                orderListEntity.setState(0);
                orderListRepository.save(orderListEntity);
            } else {
                orderListEntity = orderListRepository.findByMemberIdAndState(memberId, 0); // 장바구니 가져오기
                // 같은 주문이 있는지 확인하기
                // 1. 장바구니에 같은 메뉴가 있는지 확인하기위해 OrderMenuEntity List 가져옴
                List<OrderMenuEntity> omEntityList = orderMenuRepository.findAllByOrderlistId(orderListEntity.getOrderlistId());

                for(OrderMenuEntity omEntity : omEntityList){
                    // 2. 장바구니에 같은 메뉴가 있는지 검사
                    if(omEntity.getMenuId() == orderMenuDTO.getMenuId()){
                        // 3. 장바구니에 같은 메뉴가 있으면 옵션들을 검사
                        List<OrderOptionEntity> ooEntityList = orderOptionRepository.findAllByOrdermenuId(omEntity.getOrdermenuId());
                        boolean check1 = true; // 검사용 변수
                        for(OrderOptionEntity ooEntity : ooEntityList){
                            for(OrderOptionDTO ooDTO : orderMenuDTO.getOrderOptionDTOS()){
                                if(ooDTO.getOptionId() == ooEntity.getOptionId()){
                                    check1 = true; // 같은 내용이 있으면 true로 바꾼 후 다음 ooEntity 가져와서 비교
                                    break;
                                }else{
                                    check1 = false; // 같은 내용이 없으면 false로
                                }
                            }
                            if(check1 == false){
                                break;
                            }
                        }
                        boolean check2 = true;
                        for(OrderOptionDTO ooDTO : orderMenuDTO.getOrderOptionDTOS()){
                            for(OrderOptionEntity ooEntity : ooEntityList){
                                if(ooDTO.getOptionId() == ooEntity.getOptionId()){
                                    check2 = true; // 같은 내용이 있으면 true로 바꾼 후 다음 ooEntity 가져와서 비교
                                    break;
                                }else{
                                    check2 = false; // 같은 내용이 없으면 false로
                                }
                            }
                            if(check2 == false){
                                break;
                            }
                        }
                        if(check2 == true && check1 == true){ // 서로 같은 메뉴이므로 수량과 가격만 증가시킴
                            // 수량증가
                            omEntity.setQuantity(omEntity.getQuantity() + orderMenuDTO.getQuantity());
                            orderMenuRepository.save(omEntity);

                            // 옵션가격 합 구하기
                            int optionPriceSum = 0;
                            for(OrderOptionEntity ooEntity : ooEntityList){
                                optionPriceSum = optionPriceSum + ooEntity.getPrice();
                            }

                            // 메뉴의 가격과 옵션가격합에 수량곱하기
                            int tempPrice = (optionPriceSum + omEntity.getPrice());
                            // 가격 변경
                            orderListEntity.setPrice(orderListEntity.getPrice() + tempPrice);

                            // 저장
                            orderListRepository.save(orderListEntity);
                            // 함수종료
                            return orderListEntity;
                        }
                    }
                }


                int price = 0;
                // state가 0인 오더리스트가 있으면 불러와서 가격 수정

                // 가게 아이디가 다르면
                if (!orderListEntity.getStoreId().equals(menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId()))) {
                    // 메뉴 삭제
                    orderMenuRepository.deleteAllByOrderlistId(orderListEntity.getOrderlistId());
                    // 가게 아이디 수정
                    orderListEntity.setStoreId(menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId()));
                    orderListEntity.setPrice(price + charge);
                    orderListEntity.setCharge(charge);
                }
                // 가격 설정
                price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
                for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                    price += optionRepository.selectPriceByOptionId(orderOptionDTO.getOptionId());
                }
                price *= orderMenuDTO.getQuantity();
                orderListEntity.setPrice(orderListEntity.getPrice() + price);
                orderListRepository.save(orderListEntity);
            }


            // 오더메뉴 추가
            MenuEntity menuEntity = menuRepository.findByMenuId(orderMenuDTO.getMenuId());
            String orderListId = orderListEntity.getOrderlistId();
            OrderMenuEntity orderMenuEntity = new OrderMenuEntity();
            orderMenuEntity.setOrderlistId(orderListId);
            orderMenuEntity.setMenuId(orderMenuDTO.getMenuId());
            orderMenuEntity.setQuantity(orderMenuDTO.getQuantity());
            orderMenuEntity.setPrice(menuEntity.getPrice());
            orderMenuEntity.setName(menuEntity.getMenuName());
            orderMenuRepository.save(orderMenuEntity);

            // 오더 옵션 추가
            Long orderMenuId = orderMenuRepository.save(orderMenuEntity).getOrdermenuId();
            for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                OptionEntity optionEntity = optionRepository.findByOptionId(orderOptionDTO.getOptionId());
                OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
                orderOptionEntity.setOrdermenuId(orderMenuId);
                orderOptionEntity.setOptionId(orderOptionDTO.getOptionId());
                orderOptionEntity.setPrice(optionEntity.getPrice());
                orderOptionEntity.setName(optionEntity.getOptionName());
                orderOptionRepository.save(orderOptionEntity);
            }

            return orderListEntity;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }



    }

    // 배달비 구하기
    public int selectCharge(int memberId, String storeId) {
        log.info("서비스 시작");
        MemberRocationEntity memberRocationEntity = memberRocationRepository.findByMemberIdAndState(memberId, 1);
        String address = memberRocationEntity.getAddress1();
        String[] arr = address.split(" ");
        char j = '동';
        String dong = null;
        for(int i = 0; i < arr.length; i++) {
            if (j == (arr[i].charAt(arr[i].length() - 1))) {
                dong = arr[i];
            }
        }

        ChargeEntity chargeEntity = chargeRepository.findByStoreIdAndDongContaining(storeId, dong);
        int charge = chargeEntity.getCharge();

        return charge;

    }

    // 장바구니 메뉴 삭제
    public void deleteMenuFromBasket(int memberId, OrderMenuDTO omDTO) {

        OrderMenuEntity olEntity = orderMenuRepository.findByOrdermenuId(omDTO.getOrdermenuId());
        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(olEntity.getOrderlistId());
        OrderMenuDTO orderMenuDTO = OrderMenuDTO.builder()
                .ordermenuId(olEntity.getOrdermenuId())
                .orderlistId(olEntity.getOrderlistId())
                .menuId(olEntity.getMenuId())
                .quantity(olEntity.getQuantity())
                .price(olEntity.getPrice())
                .build();

        if (memberId != orderListEntity.getMemberId()) {
            log.warn("OrderListService.deleteMenuFromBasket() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.deleteMenuFromBasket() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
        }

        // 오더리스트에서 삭제하는 메뉴의 가격 빼기
        int price = getPrice(orderMenuDTO);
        orderListEntity.setPrice(orderListEntity.getPrice() - price);

        // 오더리스트 가격에 배달가격만 있다면 오더리스트 삭제. 아니면 변경한 가격 저장
        int charge = selectCharge(memberId, menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId()));
        if(orderListEntity.getPrice() == charge) {
            orderListRepository.deleteById(orderMenuDTO.getOrderlistId());
        } else {
            orderListRepository.save(orderListEntity);
            // 메뉴 삭제
            orderMenuRepository.deleteAllByOrderlistIdAndOrdermenuId(orderMenuDTO.getOrderlistId(), orderMenuDTO.getOrdermenuId());
        }


    }

    // 장바구니 메뉴 전체 삭제
    public void deleteAllMenuFromBasket(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.deleteAllMenu() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.deleteAllMenu() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
        }

        orderListRepository.deleteById(orderListDTO.getOrderlistId());

    }

    // 가격 구하기
    public int getPrice(OrderMenuDTO orderMenuDTO) {

        int price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
        List<Integer> optionIdList = orderOptionRepository.selectAllOptionIdByOrdermenuId(orderMenuDTO.getOrdermenuId());
        for (Integer optionIdLists : optionIdList) {
            price += optionRepository.selectPriceByOptionId(optionIdLists);
        }
        price *= orderMenuDTO.getQuantity();

        return price;

    }

    // 오더리스트 메뉴 목록
    public OrderListDTO viewMenuList(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.viewMenuList() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.viewMenuList() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
        }

        // 오더메뉴 엔티티 리스트 가져오기
        List<OrderMenuEntity> orderMenuEntities = orderMenuRepository.findAllByOrderlistId(orderListDTO.getOrderlistId());
        // 메뉴dto temp
        List<MenuDTO> mdto = new ArrayList<MenuDTO>();
        for (OrderMenuEntity orderMenuEntity : orderMenuEntities) {
            // 메뉴아이디로 메뉴 엔티티 리스트 가져옴
            List<MenuEntity> menuEntityList = menuRepository.selectByMenuId(orderMenuEntity.getMenuId());
            // 메뉴 엔티티 리스트를 dto 리스트로 변환
            List<MenuDTO> menuDTOS = menuEntityList.stream()
                    .map(MenuDTO::new)
                    .collect(Collectors.toList());
            // 수량추가
            for(int i = 0; i < orderMenuEntities.size(); i++){
                int quantity = orderMenuEntities.get(i).getQuantity();
                MenuDTO tempDTO = menuDTOS.get(i);
                tempDTO.setQuantity(quantity);
                menuDTOS.set(i, tempDTO);
            }
            // 옵션 dto temp
            List<OptionDTO> odto = new ArrayList<OptionDTO>();
            // 오더옵션 엔티티 리스트 가져오기
            List<OrderOptionEntity> orderOptionEntities = orderOptionRepository.findAllByOrdermenuId(orderMenuEntity.getOrdermenuId());
            for(OrderOptionEntity orderOptionEntity : orderOptionEntities) {
                // 옵션아이디로 옵션 엔티티 리스트 가져옴
                List<OptionEntity> optionEntityList = optionRepository.findAllByOptionId(orderOptionEntity.getOptionId());
                // 옵션 엔티티 리스트를 dto 리스트로 변환
                List<OptionDTO> optionDTOS = optionEntityList.stream()
                        .map(OptionDTO::new)
                        .collect(Collectors.toList());
                // 메뉴dto에 저장
                for (MenuDTO menuDTO : menuDTOS) {
                    odto.addAll(optionDTOS);
                    menuDTO.setOptionDTOList(odto);
                }
            }
            // 메뉴 dto 리스트 temp로 저장
            mdto.addAll(menuDTOS);
        }
        // 오더리스트 dto에 저장
        orderListDTO.setMenuDTOList(mdto);

        return orderListDTO;

    }

    // 결제 완료 후. 주문 대기 상태
    public OrderListEntity waitingOrder(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.waitingOrder() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.waitingOrder() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
        }

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderListDTO.getOrderlistId());
        orderListEntity.setState(1);
        orderListEntity.setUserMessage(orderListDTO.getUserMessage());
        orderListEntity.setSpoon(orderListDTO.getSpoon());
        orderListEntity.setOrderDate(LocalDateTime.now());
        orderListEntity.setMemberrocationId(memberRocationRepository.selectMemberrocationIdByMemberIdAndState(memberId));
        orderListRepository.save(orderListEntity);

        return orderListEntity;

    }

    //주문 수락
    public OrderListEntity acceptOrder(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.waitingOrder() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.waitingOrder() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderListDTO.getOrderlistId());
        orderListEntity.setState(2);
        // 배달 예상 시간 가져오는 함수 넣을 예정
        orderListEntity.setTime(orderListDTO.getTime());
        orderListRepository.save(orderListEntity);

        return orderListEntity;

    }

    //주문 취소
    public OrderListEntity cancelOrder(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.waitingOrder() : 로그인된 유저와 주문 내역 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.waitingOrder() : 로그인된 유저와 주문 내역 소유자가 다릅니다.");
        }

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderListDTO.getOrderlistId());
        orderListEntity.setState(3);
        orderListEntity.setCancelMessage(orderListDTO.getCancelMessage());
        orderListRepository.save(orderListEntity);

        return orderListEntity;

    }

    //주문 삭제
    public void deleteOrder(int memberId, OrderListDTO orderListDTO) {

        if (memberId != orderListDTO.getMemberId()) {
            log.warn("OrderListService.waitingOrder() : 로그인된 유저와 주문 내역 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.waitingOrder() : 로그인된 유저와 주문 내역 소유자가 다릅니다.");
        }

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderListDTO.getOrderlistId());
        orderListEntity.setState(4);
        orderListRepository.save(orderListEntity);

    }

    // 내 주문목록 가져오기 - 페이징...
    public List<OrderListDTO> getMyOrderlist(int memberId, Pageable pageable) throws Exception{
        try{
            // 1. 내 Orderlist 가져오기
            Page<OrderListEntity> orderListPage = orderListRepository.findAllByMemberId(memberId, pageable);
            List<OrderListEntity> orderListList = orderListPage.getContent();
            List<OrderListDTO> orderListDTOList = new ArrayList<>(); // 리턴할 리스트
            // 2. foreach로 메뉴목록 가져오기
            for(OrderListEntity list : orderListList){
                // orderlistId로 메뉴목록 가져오기
                List<OrderMenuEntity> orderMenuEntityList =  orderMenuRepository.findAllByOrderlistId(list.getOrderlistId());
                // orderListDTOList에 넣을 OrderMenuDTO
                List<OrderMenuDTO> orderMenuDTOList = new ArrayList<>();
                // 메뉴로 옵션목록 가져오기
                for(OrderMenuEntity orderMenuEntity : orderMenuEntityList){
                    List<OrderOptionEntity> orderOptionEntityList = new ArrayList<>();
                    orderOptionEntityList = orderOptionRepository.findAllByOrdermenuId(orderMenuEntity.getOrdermenuId());
                    List<OrderOptionDTO> orderOptionDTOList = new ArrayList<>();
                    if(orderOptionEntityList.size() > 0) { // 크기체크후 OrderMenuDTO에 넣음
                        for(OrderOptionEntity orderOptionEntity : orderOptionEntityList){
                            OrderOptionDTO orderOptionDTO = OrderOptionDTO.builder()
                                    .orderoptionId(orderOptionEntity.getOrderoptionId())
                                    .price(orderOptionEntity.getPrice())
                                    .name(orderOptionEntity.getName())
                                    .build();
                            orderOptionDTOList.add(orderOptionDTO); // 옵션리스트에 추가
                        }
                    }
                    OrderMenuDTO orderMenuDTO = OrderMenuDTO.builder().ordermenuId(orderMenuEntity.getOrdermenuId())
                            .menuId(orderMenuEntity.getMenuId())
                            .quantity(orderMenuEntity.getQuantity())
                            .price(orderMenuEntity.getPrice())
                            .name(orderMenuEntity.getName()).orderOptionDTOS(orderOptionDTOList).build();
                    orderMenuDTOList.add(orderMenuDTO);
                }
                // 주문한 주소 가져오기
                MemberRocationEntity addr = memberRocationRepository.findByMemberrocationId(list.getMemberrocationId());
                String address = addr.getAddress1() + " " + addr.getAddress2();
                // 가게이름 가져오기
                StoreEntity store = storeRepository.findByStoreId(list.getStoreId());
                String storeName = store.getName();
                OrderListDTO orderListDTO = OrderListDTO.builder().orderlistId(list.getOrderlistId())
                                .state(list.getState()).charge(list.getCharge()).address(address).storeId(list.getStoreId())
                        .thumb("http://localhost:8080/images/store/" + list.getStoreId() + ".jpg").price(list.getPrice())
                        .orderMenuDTOList(orderMenuDTOList).storeName(storeName).orderDate(list.getOrderDate()).build();
                orderListDTOList.add(orderListDTO); // 주문목록 리스트에 추가
            }
            return orderListDTOList;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 장바구니 가져오기
    public OrderListDTO getBasket(final int memberId) throws Exception{
        try{
            // 1. 내 장바구니 가져오기
            OrderListEntity orderListEntity = orderListRepository.findByMemberIdAndState(memberId, 0);
            // 2. 가져온 장바구니로 DTO 만들기
            OrderListDTO orderListDTO = OrderListDTO.builder().orderlistId(orderListEntity.getOrderlistId())
                    .price(orderListEntity.getPrice())
                    .charge(orderListEntity.getCharge()).storeId(orderListEntity.getStoreId()).build();
            // 3. orderlistId로 ordermenuList 가져오기
            List<OrderMenuEntity> orderMenuEntityList = orderMenuRepository.findAllByOrderlistId(orderListEntity.getOrderlistId());
            // 4. dto로 변환
            List<OrderMenuDTO> orderMenuDTOList = new ArrayList<>();
            for(OrderMenuEntity omEntity : orderMenuEntityList){
                MenuEntity menuEntity = menuRepository.findByMenuId(omEntity.getMenuId());
                OrderMenuDTO orderMenuDTO = OrderMenuDTO.builder().menuId(omEntity.getMenuId())
                        .price(menuEntity.getPrice())
                        .quantity(omEntity.getQuantity()).menuPic("http://localhost:8080/images/menu/" + omEntity.getMenuId() + "_1.jpg")
                        .ordermenuId(omEntity.getOrdermenuId())
                        .name(menuEntity.getMenuName()).build();
                // 5. orderoption 가져오기
                List<OrderOptionEntity> orderOptionEntityList = orderOptionRepository.findAllByOrdermenuId(omEntity.getOrdermenuId());
                List<OrderOptionDTO> orderOptionDTOList = new ArrayList<>();
                for(OrderOptionEntity ooEntity : orderOptionEntityList){
                    // 6. dto 변환
                    OptionEntity optionEntity = optionRepository.findByOptionId(ooEntity.getOptionId());
                    OrderOptionDTO orderOptionDTO = OrderOptionDTO.builder().optionId(ooEntity.getOptionId())
                            .price(optionEntity.getPrice())
                            .name(optionEntity.getOptionName()).build();
                    // 추가
                    orderOptionDTOList.add(orderOptionDTO);
                }
                // 추가
                orderMenuDTO.setOrderOptionDTOS(orderOptionDTOList);
                orderMenuDTOList.add(orderMenuDTO);
            }
            // 추가
            orderListDTO.setOrderMenuDTOList(orderMenuDTOList);
            return orderListDTO;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
