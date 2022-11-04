package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.*;
import com.coffeemantang.ZMT_BACK.model.*;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
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

    private final OrderListRepository orderListRepository;

    private final OrderMenuRepository orderMenuRepository;

    private final OrderOptionRepository orderOptionRepository;

    private final MemberRocationRepository memberRocationRepository;

    private final ChargeRepository chargeRepository;

    // 장바구니 추가
    @Transactional
    public OrderListEntity addBasket(int memberId, @Valid OrderMenuDTO orderMenuDTO) {

        OrderListEntity orderListEntity;
        String storeId = menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId());
        int charge = selectCharge(memberId, storeId);
        // state가 0인 오더리스트가 없으면 오더리스트 생성
        if ((orderListEntity = orderListRepository.findByMemberIdAndState(memberId, 0)) == null) {
            orderListEntity = new OrderListEntity();
            orderListEntity.setMemberId(memberId);
            orderListEntity.setStoreId(storeId);
            int price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
            for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                price += optionRepository.selectPriceByOptionId(orderOptionDTO.getOptionId());
            }
            orderListEntity.setCharge(charge);
            orderListEntity.setPrice((price * orderMenuDTO.getQuantity()) + charge);
            orderListEntity.setState(0);
            orderListRepository.save(orderListEntity);
        } else {
            int price = 0;
            // state가 0인 오더리스트가 있으면 불러와서 가격 수정
            orderListEntity = orderListRepository.findByMemberIdAndState(memberId, 0);
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
        String orderListId = orderListEntity.getOrderlistId();
        OrderMenuEntity orderMenuEntity = new OrderMenuEntity();
        orderMenuEntity.setOrderlistId(orderListId);
        orderMenuEntity.setMenuId(orderMenuDTO.getMenuId());
        orderMenuEntity.setQuantity(orderMenuDTO.getQuantity());
        orderMenuRepository.save(orderMenuEntity);

        // 오더 옵션 추가
        Long orderMenuId = orderMenuRepository.save(orderMenuEntity).getOrdermenuId();
        for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
            OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
            orderOptionEntity.setOrdermenuId(orderMenuId);
            orderOptionEntity.setOptionId(orderOptionDTO.getOptionId());
            orderOptionRepository.save(orderOptionEntity);
        }
//        OrderListDTO responseOrderListDTO = OrderListDTO.builder()
//                .orderlistId(orderListEntity.getOrderlistId())
//                .memberId(orderListEntity.getMemberId())
//                .storeId(orderListEntity.getStoreId())
//                .price(orderListEntity.getPrice())
//                .state(orderListEntity.getState())
//
//                .build();
        return orderListEntity;

    }

    // 배달비 구하기
    public int selectCharge(int memberId, String storeId) {
        log.info("서비스 시작");
        MemberRocationEntity memberRocationEntity = memberRocationRepository.findByMemberId(memberId);
        String address = memberRocationEntity.getAddress2();
        String[] arr = address.split(" ");
        char j = '동';
        String dong = null;
        for(int i = 0; i < arr.length; i++) {
            if (j == (arr[i].charAt(arr[i].length() - 1))) {
                dong = arr[i];
            }
        }

        ChargeEntity chargeEntity = chargeRepository.findByStoreIdAndDong(storeId, dong);
        int charge = chargeEntity.getCharge();

        return charge;

    }

    // 장바구니 메뉴 삭제
    public void deleteMenuFromBasket(int memberId, OrderMenuDTO orderMenuDTO) {

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderMenuDTO.getOrderlistId());

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
        // 유저 대표 주소 가져올 예정
        orderListEntity.setMemberrocationId(orderListDTO.getMemberrocationId());
        orderListRepository.save(orderListEntity);

        return orderListEntity;

    }

}
