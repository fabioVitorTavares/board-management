package com.fvtf.board_management.service;

import com.fvtf.board_management.dto.BoardResponse;
import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.entity.Card;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardServiceTest {

    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final ColumnService columnService = mock(ColumnService.class);
    private final CardService cardService = mock(CardService.class);


    private final BoardService boardService = new BoardService(boardRepository, columnService, cardService);

//    Cria um board com um nome e valida que o BoardRepository.save() foi chamado com um Board contendo esse nome.
    @Test
    void create_shouldSaveBoardWithName() {
        String name = "Meu Board";


        when(boardRepository.save(any(Board.class))).thenAnswer(inv -> inv.getArgument(0));

        Board created = boardService.create(name);

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());

        Board saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(created.getName()).isEqualTo(name);
    }

//    Simula um board existente no repositório, simula as colunas retornadas pelo ColumnService e os cards retornados pelo CardService.
//    Valida que o BoardService.findById() monta corretamente o BoardResponse com:
//    o id e nome do board
//    duas colunas
//    os cards de cada coluna (cada CardResponse com columnId correto)
    @Test
    void findById_shouldReturnBoardResponseWithColumnsAndCards() {
        UUID boardId = UUID.randomUUID();

        Board board = Board.builder().id(boardId).name("Board 1").build();
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        Columns col1 = Columns.builder()
                .id(UUID.randomUUID())
                .name("Todo")
                .board(board)
                .build();

        Columns col2 = Columns.builder()
                .id(UUID.randomUUID())
                .name("Doing")
                .board(board)
                .build();

        when(columnService.findAllByBoardId(boardId)).thenReturn(List.of(col1, col2));


        Card c1 = Card.builder()
                .id(UUID.randomUUID())
                .title("Task 1")
                .description("Desc 1")
                .column(col1)
                .build();

        Card c2 = Card.builder()
                .id(UUID.randomUUID())
                .title("Task 2")
                .description(null)
                .column(col1)
                .build();

        Card c3 = Card.builder()
                .id(UUID.randomUUID())
                .title("Task 3")
                .description("Desc 3")
                .column(col2)
                .build();

        when(cardService.findAllByColumnId(col1.getId())).thenReturn(List.of(c1, c2));
        when(cardService.findAllByColumnId(col2.getId())).thenReturn(List.of(c3));

        BoardResponse resp = boardService.findById(boardId);

        assertThat(resp.id()).isEqualTo(boardId);
        assertThat(resp.name()).isEqualTo("Board 1");
        assertThat(resp.columns()).hasSize(2);

        var rcol1 = resp.columns().stream().filter(c -> c.id().equals(col1.getId())).findFirst().orElseThrow();
        assertThat(rcol1.name()).isEqualTo("Todo");
        assertThat(rcol1.boardId()).isEqualTo(boardId);
        assertThat(rcol1.cards()).hasSize(2);
        assertThat(rcol1.cards().stream().allMatch(cr -> cr.columnId().equals(col1.getId()))).isTrue();

        var rcol2 = resp.columns().stream().filter(c -> c.id().equals(col2.getId())).findFirst().orElseThrow();
        assertThat(rcol2.name()).isEqualTo("Doing");
        assertThat(rcol2.boardId()).isEqualTo(boardId);
        assertThat(rcol2.cards()).hasSize(1);
        assertThat(rcol2.cards().get(0).title()).isEqualTo("Task 3");

        verify(cardService).findAllByColumnId(col1.getId());
        verify(cardService).findAllByColumnId(col2.getId());
    }

//    Simula board não encontrado no BoardRepository.findById() e valida que o service lança ResourceNotFoundException.
    @Test
    void findById_shouldThrowWhenBoardNotFound() {
        UUID boardId = UUID.randomUUID();
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.findById(boardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Board não encontrado");
    }

//    Simula o BoardRepository.findAll() retornando dois boards.
//    Usa spy no BoardService para controlar o retorno do findById() e validar que:
//    o service chama findById() para cada board retornado
//    retorna a lista de BoardResponse na mesma ordem
    @Test
    void findAll_shouldCallFindByIdForEachBoardAndReturnList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Board b1 = Board.builder().id(id1).name("B1").build();
        Board b2 = Board.builder().id(id2).name("B2").build();

        when(boardRepository.findAll()).thenReturn(List.of(b1, b2));

        BoardService spyService = spy(new BoardService(boardRepository, columnService, cardService));

        BoardResponse r1 = new BoardResponse(id1, "B1", List.of());
        BoardResponse r2 = new BoardResponse(id2, "B2", List.of());

        doReturn(r1).when(spyService).findById(id1);
        doReturn(r2).when(spyService).findById(id2);

        List<BoardResponse> result = spyService.findAll();

        assertThat(result).containsExactly(r1, r2);
        verify(spyService).findById(id1);
        verify(spyService).findById(id2);
        verify(boardRepository).findAll();
    }

//    Simula um board existente.
//    Chama createColumInBoard(name, boardId) e valida que:
//    foi criada uma entidade Columns com o nome informado
//    a coluna foi associada ao board correto
//    ColumnService.create() foi chamado com essa coluna
//    o retorno final é o BoardResponse do findById(boardId) (board atualizado com a coluna)
    @Test
    void createColumInBoard_shouldCreateColumnAndReturnBoardResponse() {
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder().id(boardId).name("Board").build();

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        BoardService spyService = spy(new BoardService(boardRepository, columnService, cardService));

        BoardResponse expected = new BoardResponse(boardId, "Board", List.of());
        doReturn(expected).when(spyService).findById(boardId);

        BoardResponse resp = spyService.createColumInBoard("Nova Coluna", boardId);

        ArgumentCaptor<Columns> captor = ArgumentCaptor.forClass(Columns.class);
        verify(columnService).create(captor.capture());

        Columns created = captor.getValue();
        assertThat(created.getName()).isEqualTo("Nova Coluna");
        assertThat(created.getBoard().getId()).isEqualTo(boardId);

        assertThat(resp).isEqualTo(expected);
        verify(spyService).findById(boardId);
    }

//    Simula board inexistente e valida que createColumInBoard lança ResourceNotFoundException e não tenta salvar coluna.
    @Test
    void createColumInBoard_shouldThrowWhenBoardNotFound() {
        UUID boardId = UUID.randomUUID();
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createColumInBoard("X", boardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Board não encontrado");
    }
}
