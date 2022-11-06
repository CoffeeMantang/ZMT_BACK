package com.coffeemantang.ZMT_BACK.service;

import com.coffeemantang.ZMT_BACK.dto.BoardDTO;
import com.coffeemantang.ZMT_BACK.model.BoardEntity;
import com.coffeemantang.ZMT_BACK.persistence.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 글 작성
    public void writeBoard(int memberId, BoardDTO boardDTO, int type) {

        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setMemberId(memberId);
        boardEntity.setType(type);
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setContent(boardDTO.getContent());
        boardEntity.setDate(LocalDateTime.now());
        boardRepository.save(boardEntity);

    }

    // 글 수정
    public BoardDTO updateBoard(int memberId, BoardDTO boardDTO) {

        if(memberId != boardDTO.getMemberId()) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 글 작성자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 글 작성자가 다릅니다.");
        }

        BoardEntity boardEntity = boardRepository.findByBoardId(boardDTO.getBoardId());
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setContent(boardDTO.getContent());
        boardRepository.save(boardEntity);

        BoardDTO newBoardDTO = new BoardDTO(boardEntity);

        return newBoardDTO;
    }

    // 글 삭제
    public void deleteBoard(int memberId, BoardDTO boardDTO) {

        if(memberId != boardDTO.getMemberId()) {
            log.warn("StoreService.updateOption() : 로그인된 유저와 글 작성자가 다릅니다.");
            throw new RuntimeException("StoreService.updateOption() : 로그인된 유저와 글 작성자가 다릅니다.");
        }

        BoardEntity boardEntity = boardRepository.findByBoardId(boardDTO.getBoardId());
        boardRepository.delete(boardEntity);
    }

    // 글 보기
    public BoardDTO viewBoard(BoardDTO boardDTO) {

        BoardEntity boardEntity = boardRepository.findByBoardId(boardDTO.getBoardId());
        BoardDTO newBoardDTO = new BoardDTO(boardEntity);

        return newBoardDTO;

    }

    // 글 목록
    public List<BoardDTO> viewBoardList(int type, Pageable pageable) {

        Page<BoardEntity> boardPage = boardRepository.findByTypeOrderByDateDesc(type, pageable);
        List<BoardEntity> boardEntityList = boardPage.getContent();

        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            BoardDTO boardDTO = new BoardDTO(boardEntity);
            boardDTOList.add(boardDTO);
        }

        return boardDTOList;

    }

    // 내가 쓴 글 목록
    public List<BoardDTO> viewBoardMyList(int memberId, Pageable pageable) {

        Page<BoardEntity> boardPage = boardRepository.findByMemberIdOrderByDateDesc(memberId, pageable);
        List<BoardEntity> boardEntityList = boardPage.getContent();

        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            BoardDTO boardDTO = new BoardDTO(boardEntity);
            boardDTOList.add(boardDTO);
        }

        return boardDTOList;
    }

}
