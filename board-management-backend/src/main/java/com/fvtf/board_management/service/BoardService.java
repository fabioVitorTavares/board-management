package com.fvtf.board_management.service;

import com.fvtf.board_management.dto.BoardResponse;
import com.fvtf.board_management.dto.CardResponse;
import com.fvtf.board_management.dto.ColumnResponse;
import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.entity.Card;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final ColumnService columnService;

    private final CardService cardService;

    @Transactional
    public Board create(String name) {

        Board board = Board.builder()
                .name(name)
                .build();

        return boardRepository.save(board);

    }

    public List<BoardResponse> findAll() {

        return boardRepository.findAll().stream().map(Board::getId).map(this::findById).toList();

    }

    public BoardResponse findById(UUID id) {

        Board board = boardRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board não encontrado: " + id));

        List<Columns> columns = columnService.findAllByBoardId(board.getId());

        List<ColumnResponse> columnResponses = columns
                .stream()
                .map(column -> {

                    List<CardResponse> cardResponseList = cardService
                            .findAllByColumnId(column.getId())
                            .stream()
                            .map(Card::getCardResponse)
                            .toList();


                    return new ColumnResponse(
                            column.getId(),
                            column.getName(),
                            cardResponseList,
                            column.getBoard().getId()
                    );

                }).toList();


        return new BoardResponse(board.getId(), board.getName(), columnResponses);

    }

    @Transactional
    public BoardResponse createColumInBoard(String name, UUID id) {

        Board board   = boardRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board não encontrado: " + id));

        Columns columns = Columns
                .builder()
                .name(name)
                .board(board)
                .build();

        columnService.create(columns);

        return findById(id);

    }

}
