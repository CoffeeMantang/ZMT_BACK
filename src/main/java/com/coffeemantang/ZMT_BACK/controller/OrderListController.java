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

    // 장바구니 메뉴 삭제
    @DeleteMapping("/delete")
    public String deleteMenuFromBasket(@AuthenticationPrincipal String memberId, @RequestBody OrderMenuDTO orderMenuDTO) {

        orderListService.deleteMenuFromBasket(Integer.parseInt(memberId), orderMenuDTO);

        return "redirect:/";
    }

    // 장바구니 메뉴 전체 삭제
    @DeleteMapping("/deleteall")
    public String deleteAllMenuFromBasket(@AuthenticationPrincipal String memberId, @RequestBody OrderListDTO orderListDTO) {

        orderListService.deleteAllMenuFromBasket(Integer.parseInt(memberId), orderListDTO);

        return "redirect:/";
    }

    // 오더리스트 메뉴 보기
    @PostMapping("/list")
    public OrderListDTO viewMenuList(@AuthenticationPrincipal String memberId, @RequestBody OrderListDTO orderListDTO) {

        try {
            OrderListDTO newOrderListDTO = orderListService.viewMenuList(Integer.parseInt(memberId), orderListDTO);
            return newOrderListDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("오더리스트 메뉴 리스트를 가져오는 도중 오류 발생");
        }
    }

    // 결제 완료 후. 주문 대기 상태
    @PostMapping("/waiting")
    public ResponseEntity<?> waitingOrder(@AuthenticationPrincipal String memberId, @RequestBody OrderListDTO orderListDTO) {

        log.info(orderListDTO.getOrderlistId() + "오더리스트아이디");
        try {
            OrderListEntity orderListEntity = orderListService.waitingOrder(Integer.parseInt(memberId), orderListDTO);
            if (orderListEntity != null) {
                OrderListDTO responseOrderListDTO = OrderListDTO.builder()
                        .orderlistId(orderListEntity.getOrderlistId())
                        .memberId(orderListEntity.getMemberId())
                        .storeId(orderListEntity.getStoreId())
                        .orderDate(orderListEntity.getOrderDate())
                        .spoon(orderListEntity.getSpoon())
                        .userMessage(orderListEntity.getUserMessage())
                        .price(orderListEntity.getPrice())
                        .charge(orderListEntity.getCharge())
                        .memberrocationId(orderListEntity.getMemberrocationId())
                        .orderMenuDTOList(orderListDTO.getOrderMenuDTOList())
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

}
