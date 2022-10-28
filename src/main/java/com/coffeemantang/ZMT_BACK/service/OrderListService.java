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
import java.util.List;

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
            orderListEntity.setPrice(price * orderMenuDTO.getQuantity());
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

    // 장바구니 메뉴 삭제
    public void deleteMenuFromBasket(int memberId, OrderMenuDTO orderMenuDTO) {

        OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderMenuDTO.getOrderlistId());

        if (memberId != orderListEntity.getMemberId()) {
            log.warn("OrderListService.deleteMenuFromBasket() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
            throw new RuntimeException("OrderListService.deleteMenuFromBasket() : 로그인된 유저와 장바구니 소유자가 다릅니다.");
        }
        int price = getPrice(orderMenuDTO);
        orderListEntity.setPrice(orderListEntity.getPrice() - price);
        orderListRepository.save(orderListEntity);
        orderMenuRepository.deleteAllByOrderlistIdAndOrdermenuId(orderMenuDTO.getOrderlistId(), orderMenuDTO.getOrdermenuId());

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
}
