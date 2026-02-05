package com.fvtf.board_management.controller;

import com.fvtf.board_management.dto.CardResponse;
import com.fvtf.board_management.dto.CreateOrUpdateTitleDescriptionRequest;
import com.fvtf.board_management.dto.MoveCardRequest;
import com.fvtf.board_management.service.CardService;
import com.fvtf.board_management.service.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class CardController {

    private final CardService cardService;

    @PostMapping("/columns/{id}/cards")
    public ResponseEntity<CardResponse> createCard(
            @PathVariable UUID id,
            @RequestBody @Valid CreateOrUpdateTitleDescriptionRequest request
    ) {

        CardResponse created = cardService.create(id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }


    @PutMapping("/cards/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable UUID id,
            @RequestBody @Valid CreateOrUpdateTitleDescriptionRequest request
    ) {

        CardResponse updated = cardService.update(id, request);

        return ResponseEntity.ok(updated);

    }

    @PatchMapping("/cards/{id}/move")
    public ResponseEntity<CardResponse> moveCard(
            @PathVariable UUID id,
            @RequestBody @Valid MoveCardRequest request
    ) {

        CardResponse updated = cardService.move(id, request);

        return ResponseEntity.ok(updated);

    }


    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {

        cardService.delete(id);

        return ResponseEntity.noContent().build();

    }

}
