package com.fvtf.board_management.service;

import com.fvtf.board_management.dto.CardResponse;
import com.fvtf.board_management.dto.CreateOrUpdateTitleDescriptionRequest;
import com.fvtf.board_management.dto.MoveCardRequest;
import com.fvtf.board_management.entity.Card;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.InvalidOperationException;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.CardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final ColumnService columnService;


    public List<Card> findAllByColumnId(UUID id) {

        return cardRepository.findAllByColumnId(id);

    }

    public Card findById(UUID id) {

        return cardRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card não encontrado: " + id));

    }

    public CardResponse create(UUID id, @Valid CreateOrUpdateTitleDescriptionRequest request) {

        Columns columns = columnService.findById(id);

        Card card = Card
                .builder()
                .title(request.title())
                .description(request.description())
                .column(columns)
                .build();

        card = cardRepository.save(card);

        return card.getCardResponse();

    }

    public CardResponse update(UUID id, @Valid CreateOrUpdateTitleDescriptionRequest request) {

        Card card = findById(id);

        card.setTitle(request.title());

        card.setDescription(request.description());

        cardRepository.save(card);

        return card.getCardResponse();

    }

    public void delete(UUID id) {

        cardRepository.deleteById(id);

    }

    public CardResponse move(UUID id, @Valid MoveCardRequest request) {

        Card card = findById(id);

        Columns newColumn = columnService.findById(request.newColumnId());

        Columns oldColum = columnService.findById(card.getColumn().getId());

        if (!oldColum.getBoard().getId().equals(newColumn.getBoard().getId())) {

            throw new InvalidOperationException("O card deve ser movido para uma coluna do mesmo board");

        }

        card.setColumn(newColumn);

        cardRepository.save(card);

        return card.getCardResponse();

    }

}
