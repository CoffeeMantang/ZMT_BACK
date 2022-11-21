package com.coffeemantang.ZMT_BACK.controller;

import com.coffeemantang.ZMT_BACK.dto.ResponseDTO;
import com.coffeemantang.ZMT_BACK.service.OrderMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orderlist/ordermenu")
public class OrderMenuController {
    @Autowired
    OrderMenuService orderMenuService;

    @GetMapping("/plus")
    public ResponseEntity<?> plus(@AuthenticationPrincipal String memberId, @RequestParam(value="orderMenuId") long orderMenuId) throws Exception{
        try{
            // 추후에 memberId 검증하는 부분 추가해야 함
            orderMenuService.plusQuantity(orderMenuId);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/minus")
    public ResponseEntity<?> minus(@AuthenticationPrincipal String memberId, @RequestParam(value="orderMenuId") long orderMenuId) throws Exception{
        try{
            // 추후에 memberId 검증하는 부분 추가해야 함
            orderMenuService.minusQuantity(orderMenuId);
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
