package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.OrderListDTO;
import com.coffeemantang.ZMT_BACK.dto.OrderMenuDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import com.coffeemantang.ZMT_BACK.service.OrderListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orderlist")
public class OrderListController {

    private final OrderListService orderListService;

    // 장바구니 추가
    @PostMapping("/addbasket")
    public ResponseEntity<?> addBasket(@AuthenticationPrincipal String memberId, @RequestBody OrderMenuDTO orderMenuDTO) {

        try {
            OrderListEntity orderListEntity = orderListService.addBasket(Integer.parseInt(memberId), orderMenuDTO);
            if (orderListEntity != null) {
                OrderListDTO responseOrderListDTO = OrderListDTO.builder()
                        .orderlistId(orderListEntity.getOrderlistId())
                        .memberId(orderListEntity.getMemberId())
                        .storeId(orderListEntity.getStoreId())
                        .price(orderListEntity.getPrice())
                        .state(orderListEntity.getState())
                        .build();
                return ResponseEntity.ok().body(responseOrderListDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping("/delete")
    public String deleteMenuFromBasket(@AuthenticationPrincipal String memberId, @RequestBody OrderMenuDTO orderMenuDTO) {
        orderListService.deleteMenuFromBasket(Integer.parseInt(memberId), orderMenuDTO);

        return "redirect:/";
    }
}
