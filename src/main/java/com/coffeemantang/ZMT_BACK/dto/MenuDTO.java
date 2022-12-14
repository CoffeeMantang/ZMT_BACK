package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.MenuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private int menuId;

    @NotBlank
    private String storeId;

    @NotBlank
    private String menuName;

    @NotNull
    private int price;

    private String notice;

    @NotBlank
    private String category;

    private String tag;

    private int menuNumber;
    private String menuPic;

    @NotNull
    private int state;

    private List<OptionDTO> optionDTOList;
    private int quantity;

    private List<MultipartFile> files;

    private List<String> menuFiles;

    private List<String> images;

    private int count;

    private int total;

    public boolean checkNull(){
        if(this.files == null){
            return false;
        }else{
            return true;
        }
    }

    public MenuDTO(final MenuEntity menuEntity) {

        this.menuId = menuEntity.getMenuId();
        this.storeId = menuEntity.getStoreId();
        this.menuName = menuEntity.getMenuName();
        this.price = menuEntity.getPrice();
        this.notice = menuEntity.getNotice();
        this.category = menuEntity.getCategory();
        this.tag = menuEntity.getTag();
        this.state = menuEntity.getState();

    }

    public MenuDTO(String menuName, int price, int count, int total) {
        this.menuName = menuName;
        this.price = price;
        this.count = count;
        this.total = total;
    }

    public static MenuEntity toEntity(final MenuDTO menuDTO) {

        return MenuEntity.builder()
                .menuId(menuDTO.getMenuId())
                .storeId(menuDTO.getStoreId())
                .menuName(menuDTO.getMenuName())
                .price(menuDTO.getPrice())
                .notice(menuDTO.getNotice())
                .category(menuDTO.getCategory())
                .tag(menuDTO.getTag())
                .state(menuDTO.getState()).build();

    }

}
