package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.OrderListDTO;
import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import com.coffeemantang.ZMT_BACK.model.OrderMenuEntity;
import com.coffeemantang.ZMT_BACK.model.OrderOptionEntity;
import com.coffeemantang.ZMT_BACK.persistence.OrderListRepository;
import com.coffeemantang.ZMT_BACK.persistence.OrderMenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.OrderOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMenuService {
    @Autowired
    private OrderMenuRepository orderMenuRepository;
    @Autowired
    private OrderOptionRepository orderOptionRepository;
    @Autowired
    private OrderListRepository orderListRepository;

    // 수량더하기
    public void plusQuantity(long orderMenuId) throws Exception{
        try{
            OrderMenuEntity orderMenuEntity = orderMenuRepository.findByOrdermenuId(orderMenuId);
            int quantity = orderMenuEntity.getQuantity();
            quantity++;
            orderMenuEntity.setQuantity(quantity);
            int menuPrice = orderMenuEntity.getPrice();
            List<OrderOptionEntity> orderOptionEntity = orderOptionRepository.findAllByOrdermenuId(orderMenuEntity.getOrdermenuId());
            int optionPrice = 0;
            for(OrderOptionEntity ooEntity : orderOptionEntity){
                optionPrice = optionPrice + ooEntity.getPrice();
            }
            OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderMenuEntity.getOrderlistId());
            int tempPrice = orderListEntity.getPrice();
            tempPrice = tempPrice + optionPrice + menuPrice;
            orderListEntity.setPrice(tempPrice);
            orderListRepository.save(orderListEntity);
            orderMenuRepository.save(orderMenuEntity);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 수량빼기
    public void minusQuantity(long orderMenuId) throws Exception{
        try{
            OrderMenuEntity orderMenuEntity = orderMenuRepository.findByOrdermenuId(orderMenuId);
            int quantity = orderMenuEntity.getQuantity();
            if(quantity > 1){
                quantity--;
                orderMenuEntity.setQuantity(quantity);
                int menuPrice = orderMenuEntity.getPrice();
                List<OrderOptionEntity> orderOptionEntity = orderOptionRepository.findAllByOrdermenuId(orderMenuEntity.getOrdermenuId());
                int optionPrice = 0;
                for(OrderOptionEntity ooEntity : orderOptionEntity){
                    optionPrice = optionPrice + ooEntity.getPrice();
                }
                OrderListEntity orderListEntity = orderListRepository.findByOrderlistId(orderMenuEntity.getOrderlistId());
                int tempPrice = orderListEntity.getPrice();
                tempPrice = tempPrice - (optionPrice + menuPrice);
                orderListEntity.setPrice(tempPrice);
                orderListRepository.save(orderListEntity);
                orderMenuRepository.save(orderMenuEntity);
            }


        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
