package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.OrderListDTO;
import com.coffeemantang.ZMT_BACK.dto.OrderMenuDTO;
import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.model.OrderListEntity;
import com.coffeemantang.ZMT_BACK.persistence.OrderListRepository;
import com.coffeemantang.ZMT_BACK.service.OrderListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orderlist")
public class OrderListController {

    private final OrderListService orderListService;

    private final OrderListRepository orderListRepository;

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
    @PostMapping("/delete")
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
    @GetMapping("/list")
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
    @GetMapping("/waiting")
    public ResponseEntity<?> waitingOrder(@AuthenticationPrincipal String memberId) {
        try {
            OrderListEntity orderListEntity1 = orderListRepository.findByMemberIdAndState(Integer.parseInt(memberId), 0);
            OrderListDTO orderListDTO1 = OrderListDTO.builder().orderlistId(orderListEntity1.getOrderlistId()).build();
            OrderListEntity orderListEntity = orderListService.waitingOrder(Integer.parseInt(memberId), orderListDTO1);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 주문 수락
    @GetMapping("/accept")
    public ResponseEntity<?> acceptOrder(@AuthenticationPrincipal String memberId, @RequestParam String orderlistId) {

        try {
            OrderListEntity orderListEntity1 = orderListRepository.findByOrderlistId(orderlistId);
            OrderListDTO orderListDTO1 = OrderListDTO.builder().orderlistId(orderListEntity1.getOrderlistId()).build();
            OrderListEntity orderListEntity = orderListService.acceptOrder(Integer.parseInt(memberId), orderListDTO1);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 주문 수락
    @GetMapping("/complete")
    public ResponseEntity<?> completeOrder(@AuthenticationPrincipal String memberId, @RequestParam String orderlistId) {

        try {
            OrderListEntity orderListEntity1 = orderListRepository.findByOrderlistId(orderlistId);
            OrderListDTO orderListDTO1 = OrderListDTO.builder().orderlistId(orderListEntity1.getOrderlistId()).build();
            OrderListEntity orderListEntity = orderListService.completeOrder(Integer.parseInt(memberId), orderListDTO1);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 주문 취소
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal String memberId, @RequestBody OrderListDTO orderListDTO) {

        try {
            OrderListEntity orderListEntity = orderListService.cancelOrder(Integer.parseInt(memberId), orderListDTO);
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
                        .time(orderListEntity.getTime())
                        .cancelMessage(orderListEntity.getCancelMessage())
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

    // 주문 삭제
    @PostMapping("/deleteorder")
    public String deleteOrder(@AuthenticationPrincipal String memberId, @RequestBody OrderListDTO orderListDTO) {

        orderListService.deleteOrder(Integer.parseInt(memberId), orderListDTO);

        return "redirect:/";

    }

    // 주문내역 가져오기
    @GetMapping("/getOrderlist")
    public ResponseEntity<?> getMyOrderlist(@AuthenticationPrincipal String memberId, @PageableDefault(size = 10) Pageable pageable) throws Exception {

        try{
            List<OrderListDTO> result = orderListService.getMyOrderlist(Integer.parseInt(memberId), pageable);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").data(Arrays.asList(result.toArray())).build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 장바구니 가져오기
    @GetMapping("/getBasket")
    public ResponseEntity<?> getBasket(@AuthenticationPrincipal String memberId) throws Exception{
        try{
            OrderListDTO orderListDTO = orderListService.getBasket(Integer.parseInt(memberId));
            return ResponseEntity.ok().body(orderListDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 해당 가게의 상태에 맞는 orderlist 가져옴
    @GetMapping("/getStoreOrderList")
    public ResponseEntity<?> getOrderList(@AuthenticationPrincipal String memberId, @RequestParam("storeId") String storeId,
                                          @RequestParam("state") int state, @PageableDefault(size = 10) Pageable pageable) throws Exception{
        try{
            List<OrderListDTO> orderListDTOList = orderListService.getStoreOrder(Integer.parseInt(memberId), storeId, state, pageable);
            return ResponseEntity.ok().body(orderListDTOList);
        }catch(Exception e){
            e.printStackTrace();
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
