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

    private int bookmarkId;

    private int memberId;

    private String storeId;

    public BookmarkDTO(final BookmarkEntity bookmarkEntity) {

        this.bookmarkId = bookmarkEntity.getBookmarkId();
        this.memberId = bookmarkEntity.getMemberId();
        this.storeId = bookmarkEntity.getStoreId();

    }

    public static BookmarkEntity toEntity(final BookmarkDTO bookmarkDTO) {

        return BookmarkEntity.builder()
                .bookmarkId(bookmarkDTO.getBookmarkId())
                .memberId(bookmarkDTO.getMemberId())
                .storeId(bookmarkDTO.getStoreId())
                .build();

    }

}
