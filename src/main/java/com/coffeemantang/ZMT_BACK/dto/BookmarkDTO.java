package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private int memberId;

    private int storeId;

    public BookmarkDTO(final BookmarkEntity bookmarkEntity) {

        this.memberId = bookmarkEntity.getMemberId();
        this.storeId = bookmarkEntity.getStoreId();

    }

    public static BookmarkEntity toEntity(final BookmarkDTO bookmarkDTO) {

        return BookmarkEntity.builder()
                .memberId(bookmarkDTO.getMemberId())
                .storeId(bookmarkDTO.getStoreId())
                .build();

    }

}
