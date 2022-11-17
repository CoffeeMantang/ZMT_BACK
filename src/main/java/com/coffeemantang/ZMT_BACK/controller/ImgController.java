package com.coffeemantang.ZMT_BACK.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImgController {

    //메뉴 이미지 url
    @GetMapping(value = "/menu/{fileOriginName}")
    public ResponseEntity<Resource> getMenuImgByName(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = "C:\\zmtImgs\\menuImg\\"; // 실제 이미지가 있는 위치
            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }
    // 가게 이미지 url
    @GetMapping(value = "/store/{fileOriginName}")
    public ResponseEntity<Resource> getStoreImgByName(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = "C:\\zmtImgs\\storeImg\\"; // 실제 이미지가 있는 위치
            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }
    // 리뷰 이미지 url
    @GetMapping(value = "/review/{fileOriginName}")
    public ResponseEntity<Resource> getReviewImgByName(@PathVariable("fileOriginName") String fileName) throws Exception{
        try{
            String path = "C:\\zmtImgs\\reviewImg\\"; // 실제 이미지가 있는 위치
            FileSystemResource resource = new FileSystemResource(path+fileName);
            if(!resource.exists()){
                throw new Exception();
            }
            HttpHeaders header = new HttpHeaders();
            Path filePath = null;
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath)); // filePath의 마임타입 체크해서 header에 추가
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        }catch(Exception e){
            throw new Exception();
        }
    }
}
