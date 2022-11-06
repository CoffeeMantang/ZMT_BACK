package com.coffeemantang.ZMT_BACK.dto;

import com.coffeemantang.ZMT_BACK.model.BoardEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private int boardId;

    private int memberId;

    private int type;

    private String title;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date;

    public BoardDTO(final BoardEntity boardEntity) {

        this.boardId = boardEntity.getBoardId();
        this.memberId = boardEntity.getMemberId();
        this.type = boardEntity.getType();
        this.title = boardEntity.getTitle();
        this.content = boardEntity.getContent();
        this.date = boardEntity.getDate();

    }

    public static BoardEntity toEntity(final BoardDTO boardDTO) {

        return BoardEntity.builder()
                .boardId(boardDTO.getBoardId())
                .memberId(boardDTO.getMemberId())
                .type(boardDTO.getType())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .date(boardDTO.getDate())
                .build();

    }

}
