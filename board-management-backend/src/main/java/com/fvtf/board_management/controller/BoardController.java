package com.fvtf.board_management.controller;

import com.fvtf.board_management.dto.BoardResponse;
import com.fvtf.board_management.dto.CreateByNameRequest;
import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody @Valid CreateByNameRequest request) {

        Board created = boardService.create(request.name());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);

    }

    @PostMapping("/{id}/columns")
    public ResponseEntity<BoardResponse> createColumInBoard(@RequestBody @Valid CreateByNameRequest request, @PathVariable @Valid UUID id) {

        BoardResponse created = boardService.createColumInBoard(request.name(), id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);

    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {

        List<BoardResponse> list = boardService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable UUID id) {

        BoardResponse board = boardService.findById(id);

        return ResponseEntity.ok(board);

    }

}
