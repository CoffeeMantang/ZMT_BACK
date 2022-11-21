package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.BookmarkEntity;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private int bookmarkId;

    private int memberId;

    private String thumb; // 썸네일
    private String storeName; // 가게명
    private double score; // 리뷰점수
    private String address1; // 가게주소1

    private String storeId;
    
    private long count; // 북마크 갯수
    private List<BookmarkDTO> bookmarkList;

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
