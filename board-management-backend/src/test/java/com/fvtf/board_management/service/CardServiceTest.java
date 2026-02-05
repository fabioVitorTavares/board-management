package com.fvtf.board_management.service;

import com.fvtf.board_management.dto.CardResponse;
import com.fvtf.board_management.dto.CreateOrUpdateTitleDescriptionRequest;
import com.fvtf.board_management.dto.MoveCardRequest;
import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.entity.Card;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.InvalidOperationException;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    private final CardRepository cardRepository = mock(CardRepository.class);
    private final ColumnService columnService = mock(ColumnService.class);

    private final CardService cardService = new CardService(cardRepository, columnService);

    //Simula uma coluna existente via ColumnService.findById(columnId).
    //Cria um card com title/description e valida que:
    //CardRepository.save() foi chamado com um Card ligado à coluna correta
    //o CardResponse retornado contém title, description, columnId e um id preenchido (simulado no mock do save)
    @Test
    void create_shouldCreateCardInColumnAndReturnResponse() {
        UUID columnId = UUID.randomUUID();

        Board board = Board.builder().id(UUID.randomUUID()).name("B").build();
        Columns column = Columns.builder().id(columnId).name("Todo").board(board).build();

        when(columnService.findById(columnId)).thenReturn(column);
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(UUID.randomUUID()); // simula JPA gerando id
            return c;
        });

        CreateOrUpdateTitleDescriptionRequest req =
                new CreateOrUpdateTitleDescriptionRequest("Task 1", "Desc");

        CardResponse resp = cardService.create(columnId, req);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());

        Card saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Task 1");
        assertThat(saved.getDescription()).isEqualTo("Desc");
        assertThat(saved.getColumn().getId()).isEqualTo(columnId);

        assertThat(resp.title()).isEqualTo("Task 1");
        assertThat(resp.description()).isEqualTo("Desc");
        assertThat(resp.columnId()).isEqualTo(columnId);
        assertThat(resp.id()).isNotNull();
    }

    //Simula um card existente via CardRepository.findById(cardId).
    //Atualiza título e descrição e valida que:
    //os campos do card foram alterados
    //CardRepository.save() foi chamado
    //o CardResponse retornado reflete os novos valores
    @Test
    void update_shouldUpdateTitleAndDescriptionAndReturnResponse() {
        UUID cardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        Board board = Board.builder().id(UUID.randomUUID()).name("B").build();
        Columns column = Columns.builder().id(columnId).name("Todo").board(board).build();

        Card existing = Card.builder()
                .id(cardId)
                .title("Old")
                .description("OldDesc")
                .column(column)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existing));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrUpdateTitleDescriptionRequest req =
                new CreateOrUpdateTitleDescriptionRequest("New", "NewDesc");

        CardResponse resp = cardService.update(cardId, req);

        verify(cardRepository).save(existing);
        assertThat(existing.getTitle()).isEqualTo("New");
        assertThat(existing.getDescription()).isEqualTo("NewDesc");

        assertThat(resp.id()).isEqualTo(cardId);
        assertThat(resp.title()).isEqualTo("New");
        assertThat(resp.description()).isEqualTo("NewDesc");
        assertThat(resp.columnId()).isEqualTo(columnId);
    }

    //Chama delete(cardId) e valida que CardRepository.deleteById(cardId) foi executado.
    @Test
    void delete_shouldDeleteById() {
        UUID cardId = UUID.randomUUID();

        cardService.delete(cardId);

        verify(cardRepository).deleteById(cardId);
    }
    //Simula:
    //um card em uma coluna antiga
    //uma nova coluna dentro do mesmo board
    //Chama move(cardId, newColumnId) e valida que:
    //o card passa a apontar para a nova coluna
    //CardRepository.save() é chamado
    //o CardResponse retorna o columnId atualizado
    @Test
    void move_shouldMoveCardWhenTargetColumnIsInSameBoard() {
        UUID cardId = UUID.randomUUID();

        UUID boardId = UUID.randomUUID();
        Board board = Board.builder().id(boardId).name("Board").build();

        Columns oldColumn = Columns.builder()
                .id(UUID.randomUUID())
                .name("Old")
                .board(board)
                .build();

        Columns newColumn = Columns.builder()
                .id(UUID.randomUUID())
                .name("New")
                .board(board)
                .build();

        Card card = Card.builder()
                .id(cardId)
                .title("Task")
                .description(null)
                .column(oldColumn)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnService.findById(newColumn.getId())).thenReturn(newColumn);
        // o CardService também chama findById da coluna antiga:
        when(columnService.findById(oldColumn.getId())).thenReturn(oldColumn);

        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        MoveCardRequest req = new MoveCardRequest(newColumn.getId());

        CardResponse resp = cardService.move(cardId, req);

        // valida que mudou a coluna
        assertThat(card.getColumn().getId()).isEqualTo(newColumn.getId());
        verify(cardRepository).save(card);

        assertThat(resp.id()).isEqualTo(cardId);
        assertThat(resp.columnId()).isEqualTo(newColumn.getId());
    }

    //Simula:
    //coluna antiga em um board A
    //coluna nova em um board B (diferente)
    //Chama move(...) e valida que:
    //lança InvalidOperationException
    //não chama CardRepository.save() (move bloqueado pela regra de negócio)
    @Test
    void move_shouldThrowInvalidOperationWhenTargetColumnIsFromAnotherBoard() {
        UUID cardId = UUID.randomUUID();

        Board boardA = Board.builder().id(UUID.randomUUID()).name("A").build();
        Board boardB = Board.builder().id(UUID.randomUUID()).name("B").build();

        Columns oldColumn = Columns.builder()
                .id(UUID.randomUUID())
                .name("Old")
                .board(boardA)
                .build();

        Columns newColumn = Columns.builder()
                .id(UUID.randomUUID())
                .name("New")
                .board(boardB)
                .build();

        Card card = Card.builder()
                .id(cardId)
                .title("Task")
                .column(oldColumn)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnService.findById(newColumn.getId())).thenReturn(newColumn);
        when(columnService.findById(oldColumn.getId())).thenReturn(oldColumn);

        MoveCardRequest req = new MoveCardRequest(newColumn.getId());

        assertThatThrownBy(() -> cardService.move(cardId, req))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("mesmo board");

        verify(cardRepository, never()).save(any(Card.class));
    }

    //Simula card inexistente e valida que findById lança ResourceNotFoundException com mensagem contendo o id.
    @Test
    void findById_shouldThrowWhenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.findById(cardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Card não encontrado")
                .hasMessageContaining(cardId.toString());
    }
}
