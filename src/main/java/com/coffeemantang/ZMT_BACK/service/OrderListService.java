package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.OrderMenuDTO;
import com.coffeemantang.ZMT_BACK.dto.OrderOptionDTO;
import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import com.coffeemantang.ZMT_BACK.model.OrderOptionEntity;
import com.coffeemantang.ZMT_BACK.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderListService {

    private final MenuRepository menuRepository;

    private final OptionRepository optionRepository;

    private final OrderListRepository orderListRepository;

    private final OrderMenuRepository orderMenuRepository;

    private final OrderOptionRepository orderOptionRepository;

    // 장바구니 추가
    @Transactional
    public OrderListEntity addBasket(int memberId, @Valid OrderMenuDTO orderMenuDTO) {

        OrderListEntity orderListEntity;
        // state가 0인 오더리스트가 없으면 오더리스트 생성
        if ((orderListEntity = orderListRepository.findByMemberIdAndState(memberId, 0)) == null) {
            orderListEntity = new OrderListEntity();
            orderListEntity.setMemberId(memberId);
            orderListEntity.setStoreId(menuRepository.selectStoreIdByMenuId(orderMenuDTO.getMenuId()));
            int price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
            for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                price += optionRepository.selectPriceByOptionId(orderOptionDTO.getOptionId());
            }
            orderListEntity.setPrice(price);
            // 수량이 1보다 많으면 price * 수량
            if (orderMenuDTO.getNumber() > 1) {
                orderListEntity.setPrice(price * orderMenuDTO.getNumber());
            }
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
                orderListEntity.setPrice(price);
            }
            // 가격 설정
            price = menuRepository.selectPriceByMenuId(orderMenuDTO.getMenuId());
            for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                price += optionRepository.selectPriceByOptionId(orderOptionDTO.getOptionId());
            }
            if (orderMenuDTO.getNumber() > 1) {
                price *= orderMenuDTO.getNumber();
                orderListEntity.setPrice(orderListEntity.getPrice() + price);
            } else {
                orderListEntity.setPrice(orderListEntity.getPrice() + price);
            }
            orderListRepository.save(orderListEntity);
        }

        // 오더메뉴 추가
        String orderListId = orderListEntity.getOrderlistId();
        for (int i = 0; i < orderMenuDTO.getNumber(); i++) {
            OrderMenuEntity orderMenuEntity = new OrderMenuEntity();
            orderMenuEntity.setOrderlistId(orderListId);
            orderMenuEntity.setMenuId(orderMenuDTO.getMenuId());
            orderMenuRepository.save(orderMenuEntity);

            // 오데 옵션 추가
            Long orderMenuId = orderMenuRepository.save(orderMenuEntity).getOrdermenuId();
            for (OrderOptionDTO orderOptionDTO : orderMenuDTO.getOrderOptionDTOS()) {
                OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
                orderOptionEntity.setOrderoptionId(null);
                orderOptionEntity.setOrdermenuId(orderMenuId);
                orderOptionEntity.setOptionId(orderOptionDTO.getOptionId());
                orderOptionRepository.save(orderOptionEntity);
            }
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

    // 장바구니 삭제

}
