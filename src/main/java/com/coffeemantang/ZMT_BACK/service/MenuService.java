package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.MenuDTO;
import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import com.coffeemantang.ZMT_BACK.model.MenuImgEntity;
import com.coffeemantang.ZMT_BACK.model.StoreEntity;
import com.coffeemantang.ZMT_BACK.persistence.MenuImgRepository;
import com.coffeemantang.ZMT_BACK.persistence.MenuRepository;
import com.coffeemantang.ZMT_BACK.persistence.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    @Autowired
    private final MenuRepository menuRepository;

    private final StoreRepository storeRepository;

    @Autowired
    private final MenuImgRepository menuImgRepository;

    //메뉴 추가
    public void addMenu(final MenuDTO menuDTO, int memberId) {
        try {
            MenuEntity menuEntity = MenuEntity.builder().menuId(menuDTO.getMenuId())
                    .storeId(menuDTO.getStoreId())
                    .menuName(menuDTO.getMenuName())
                    .category(menuDTO.getCategory())
                    .notice(menuDTO.getNotice())
                    .price(menuDTO.getPrice())
                    .tag(menuDTO.getTag())
                    .state(0).build();

            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuEntity.getMenuId());

            if (menuEntity == null) {
                log.warn("MenuService.addMenu() : menuEntity에 내용이 부족해요");
                throw new RuntimeException("MenuService.addMenu() : menuEntity에 내용이 부족해요");
            } else if (memberId != selectMemberIdByMenuId) {
                log.warn("MenuService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
                throw new RuntimeException("MenuService.addOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            }

            // 해당 카테고리의 메뉴 갯수 가져오기...
            long menuNumber = menuRepository.countByCategory(menuDTO.getCategory());
            // 가장 마지막 순서로 지정
            menuEntity.setMenuNumber((int) menuNumber);

            // 저장
            int menuId = menuRepository.save(menuEntity).getMenuId();

            // 이미지가 있는 경우

            List<MultipartFile> multipartFiles = menuDTO.getFiles();
            // 반환할 파일 리스트
            List<MenuImgEntity> fileList = new ArrayList<>();
            // 전달되어 온 파일이 존재할 경우
            String current_date = null;
            if (!CollectionUtils.isEmpty(multipartFiles)) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                current_date = now.format(dateTimeFormatter);

                // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
                // 경로 구분자 File.separator 사용
                // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

                String absolutePath = "C:" + File.separator + "zmtImgs" + File.separator + "menuImg";

                // 파일을 저장할 세부 경로 지정
                String path = absolutePath;
                File file = new File(path);

                // 디렉터리가 존재하지 않을 경우
                if (!file.exists()) {
                    boolean wasSuccessful = file.mkdirs(); // 디렉터리 생성

                    // 디렉터리 생성에 실패했을 경우
                    if (!wasSuccessful) {
                        log.warn("file: was not successful");
                    }
                }

                // 다중 파일 처리
                int cnt = 1;
                for (MultipartFile multipartFile : multipartFiles) {
                    // 파일의 확장자 추출
                    String originalFileExtension;
                    String contentType = multipartFile.getContentType();

                    // 확장자명이 존재하지 않을 경우 처리하지 않음
                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else { // 확장자가 jpeg, png인 파일들만 받아서 처리
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("images/png")) {
                            originalFileExtension = ".png";
                        } else { // 다른 확장자일 경우 처리하지 않음
                            break;
                        }
                    }

                    // 파일명 중복 피하기 위해 나노초까지 얻어와 지정
                    //String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                    // 파일명은 아이디 + _숫자로
                    String new_file_name = String.valueOf(menuId) + "_" + String.valueOf(cnt);

                    // 엔티티 생성
                    MenuImgEntity menuImgEntity = MenuImgEntity.builder()
                            .menuId(menuId) // 리뷰아이디
                            .path(new_file_name + originalFileExtension)
                            .build();

                    // 생성후 리스트에 추가
                    fileList.add(menuImgEntity);

                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File(path + File.separator + new_file_name + originalFileExtension);
                    multipartFile.transferTo(file);

                    // 파일 권한 설정
                    file.setWritable(true);
                    file.setReadable(true);

                    // 엔티티 저장
                    menuImgRepository.save(menuImgEntity);
                    cnt++;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    // 메뉴 수정
    public MenuEntity updateMenu(int memberId, @Valid MenuDTO menuDTO) {
        try{
            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuDTO.getMenuId());

            if (memberId != selectMemberIdByMenuId) {
                log.warn("MenuService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
                throw new RuntimeException("MenuService.updateOption() : 로그인된 유저와 가게 소유자가 다릅니다.");
            }

            MenuEntity menuEntity = menuRepository.findByMenuId(menuDTO.getMenuId());
            menuEntity.setMenuName(menuDTO.getMenuName());
            menuEntity.setPrice(menuDTO.getPrice());
            menuEntity.setNotice(menuDTO.getNotice());
            menuEntity.setCategory(menuDTO.getCategory());
            menuEntity.setState(menuDTO.getState());
            menuRepository.save(menuEntity);

            int menuId = menuDTO.getMenuId();

            List<MultipartFile> multipartFiles = menuDTO.getFiles();
            // 반환할 파일 리스트
            List<MenuImgEntity> fileList = new ArrayList<>();
            // 전달되어 온 파일이 존재할 경우
            String current_date = null;
            if (!CollectionUtils.isEmpty(multipartFiles)) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                current_date = now.format(dateTimeFormatter);

                // 프로젝트 디렉터리 내의 저장을 위한 절대 경로 설정
                // 경로 구분자 File.separator 사용
                // String absolutePath = new File("").getAbsolutePath() + File.separator + File.separator;

                String absolutePath = "C:" + File.separator + "zmtImgs" + File.separator + "menuImg";

                // 파일을 저장할 세부 경로 지정
                String path = absolutePath;
                File file = new File(path);

                // 디렉터리가 존재하지 않을 경우
                if (!file.exists()) {
                    boolean wasSuccessful = file.mkdirs(); // 디렉터리 생성

                    // 디렉터리 생성에 실패했을 경우
                    if (!wasSuccessful) {
                        log.warn("file: was not successful");
                    }
                }

                // 다중 파일 처리
                int cnt = 1;
                for (MultipartFile multipartFile : multipartFiles) {
                    // 파일의 확장자 추출
                    String originalFileExtension;
                    String contentType = multipartFile.getContentType();

                    // 확장자명이 존재하지 않을 경우 처리하지 않음
                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else { // 확장자가 jpeg, png인 파일들만 받아서 처리
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("images/png")) {
                            originalFileExtension = ".png";
                        } else { // 다른 확장자일 경우 처리하지 않음
                            break;
                        }
                    }

                    // 파일명 중복 피하기 위해 나노초까지 얻어와 지정
                    //String new_file_name = System.nanoTime() + originalFileExtension; // 나노초 + 확장자

                    // 파일명은 아이디 + _숫자로
                    String new_file_name = String.valueOf(menuId) + "_" + String.valueOf(cnt);

                    // 엔티티 생성
                    MenuImgEntity menuImgEntity = menuImgRepository.findTop1ByMenuId(menuId);

                    // 기존에 파일이 있는 경우 기존 파일을 제거하고 진행
                    if(menuImgEntity.getPath() != null && menuImgEntity.getPath() != ""){
                        String tempPath = absolutePath + File.separator + menuImgEntity.getPath();
                        File delFile = new File(tempPath);
                        // 해당 파일이 존재하는지 한번 더 체크 후 삭제
                        if(delFile.isFile()){
                            delFile.delete();
                        }
                    }

                    // 추후 다중파일업로드를 위해 아래 코드 수정예정

                    // 메뉴 엔티티 수정
                    menuImgEntity.setMenuId(menuId);
                    menuImgEntity.setPath(new_file_name + originalFileExtension);

                    // 생성후 리스트에 추가
                    fileList.add(menuImgEntity);

                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File(path + File.separator + new_file_name + originalFileExtension);
                    multipartFile.transferTo(file);

                    // 파일 권한 설정
                    file.setWritable(true);
                    file.setReadable(true);

                    // 엔티티 저장
                    menuImgRepository.save(menuImgEntity);
                    cnt++;

                }
            }

            return menuEntity;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    // 메뉴 삭제 메서드
    public MenuEntity deleteMenu(int memberId, int menuId) {

        MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
        StoreEntity storeEntity = storeRepository.findByStoreIdAndMemberId(menuEntity.getStoreId(), memberId);

        if (!menuEntity.getStoreId().equals(storeEntity.getStoreId())) {
            log.warn("MenuService.deleteMenu() : 로그인된 유저와 가게 소유자가 다릅니다.");
            throw new RuntimeException("MenuService.deleteMenu() : 로그인된 유저와 가게 소유자가 다릅니다.");
        }

        int menuNumber = menuEntity.getMenuNumber();
        menuEntity.setState(2);
        List<MenuEntity> menuEntityList = menuRepository.findByGreaterThanMenuNumberAndStoreId(menuNumber, menuEntity.getStoreId());

        for (MenuEntity menuEntity1 : menuEntityList) {
            menuEntity1.setMenuNumber(menuEntity1.getMenuNumber() - 1);
            menuRepository.save(menuEntity1);
        }

        return menuEntity;

    }

    //메뉴 번호 생성 메서드
    public int createMenuNumber(String storeId) {

        //menuNumber 컬럼만 가져옴
        List<Integer> list = menuRepository.selectAllMenuNumber(storeId, 2);

        //리스트가 비어있으면 1, 아니면 최대값 + 1
        if (list.isEmpty()) {
            return 1;
        } else {
            //menuNumber에서 최대값
            int max = Collections.max(list);
            return max + 1;
        }

    }

    // 메뉴 순서 이동
    public MenuEntity menuSequenceMove(MenuDTO tmenuDTO, int memberId, int move) {

        try {
            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(tmenuDTO.getMenuId());

            if (memberId != selectMemberIdByMenuId) {
                log.warn("MenuService.menuSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
                throw new RuntimeException("MenuService.menuSequenceMove() : 로그인된 유저와 가게 소유 유저가 다릅니다.");
            }
            MenuEntity menuEntity1 = menuRepository.findByMenuId(tmenuDTO.getMenuId());

            int menuNumber = menuEntity1.getMenuNumber();
            long maxNumber = menuRepository.countByCategory(menuEntity1.getCategory()) - 1;
            MenuEntity menuEntity;
            // 메뉴번호가 최소치이거나 최대치가 아닐때만 수행

            if (move == 1) { // move == 1 : /up

                if (menuNumber != 0) {
                    menuNumber -= 1;
                    // 순서가 내려갈 메뉴
                    menuEntity = menuRepository.selectMenuStoreIdAndMenuNumberAndState(menuEntity1.getStoreId(), menuNumber, 2);
                    menuEntity.setMenuNumber(menuNumber + 1);
                    menuRepository.save(menuEntity);
                }
            } else { // move == 2 : /down
                if (menuNumber != maxNumber) {
                    menuNumber += 1;
                    // 순서가 올라갈 메뉴
                    menuEntity = menuRepository.selectMenuStoreIdAndMenuNumberAndState(menuEntity1.getStoreId(), menuNumber, 2);
                    menuEntity.setMenuNumber(menuNumber - 1);
                    menuRepository.save(menuEntity);
                }

            }

            // 유저가 선택한 메뉴
            menuEntity = menuRepository.selectMenuStoreIdAndMenuIdAndState(menuEntity1.getStoreId(), menuEntity1.getMenuId(), 2);
            menuEntity.setMenuNumber(menuNumber);
            menuRepository.save(menuEntity);

            return menuEntity;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MenuService.menuSequenceMove() Exception");
        }
    }

    // 메뉴 목록
    public List<MenuDTO> viewMenuList(String storeId) {

        List<MenuEntity> menuEntityList = menuRepository.selectMenuOrderByMenuNumber(storeId, 2);
        List<MenuDTO> menuDTOList = new ArrayList<>();
        for (MenuEntity menuEntity : menuEntityList) {
            MenuDTO menuDTO = new MenuDTO(menuEntity);
            menuDTOList.add(menuDTO);
        }

        return menuDTOList;

    }

    // 메뉴정보 가져오기
    public MenuDTO getMenuInfo(int menuId) throws Exception {
        try {
            MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
            MenuImgEntity menuImgEntity = menuImgRepository.findTop1ByMenuId(menuEntity.getMenuId());
            MenuDTO menuDTO = MenuDTO.builder().menuId(menuId)
                    .price(menuEntity.getPrice()).notice(menuEntity.getNotice()).menuName(menuEntity.getMenuName())
                    .storeId(menuEntity.getStoreId()).category(menuEntity.getCategory())
                    .menuPic("http://localhost:8080/images/menu/" + menuImgEntity.getPath()).build();
            return menuDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // 메뉴 상태 변경
    public void setState(int memberId, int menuId, int state) throws Exception{
        try{
            int selectMemberIdByMenuId = menuRepository.selectMemberIdByMenuId(menuId);

            if (memberId != selectMemberIdByMenuId) {
                log.warn("로그인된 유저와 가게 소유 유저가 다릅니다.");
                throw new RuntimeException("로그인된 유저와 가게 소유 유저가 다릅니다.");
            }

            if(state > 2 || state < 0){
                log.warn("상태값 왜이럼");
                throw new RuntimeException("상태값 왜이럼");
            }

            MenuEntity menuEntity = menuRepository.findByMenuId(menuId);
            menuEntity.setState(state);
            menuRepository.save(menuEntity);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
