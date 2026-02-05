package com.fvtf.board_management.service;

import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.ColumnRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColumnServiceTest {

    private final ColumnRepository columnRepository = mock(ColumnRepository.class);
    private final ColumnService columnService = new ColumnService(columnRepository);

//    Cria uma entidade Columns com name e board e valida que o ColumnRepository.save() foi chamado com os mesmos dados (ou seja, que a coluna foi persistida).
    @Test
    void create_shouldSaveColumns() {
        UUID boardId = UUID.randomUUID();
        Board board = Board.builder().id(boardId).name("Board").build();

        Columns columns = Columns.builder()
                .id(UUID.randomUUID())
                .name("Todo")
                .board(board)
                .build();

        when(columnRepository.save(any(Columns.class))).thenAnswer(inv -> inv.getArgument(0));

        columnService.create(columns);

        ArgumentCaptor<Columns> captor = ArgumentCaptor.forClass(Columns.class);
        verify(columnRepository).save(captor.capture());

        Columns saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Todo");
        assertThat(saved.getBoard().getId()).isEqualTo(boardId);
    }

//Simula ColumnRepository.findAllByBoardId(boardId) retornando uma lista de colunas e valida que o service retorna exatamente essa lista.
    @Test
    void findAllByBoardId_shouldReturnColumnsList() {
        UUID boardId = UUID.randomUUID();

        Columns c1 = Columns.builder().id(UUID.randomUUID()).name("A").build();
        Columns c2 = Columns.builder().id(UUID.randomUUID()).name("B").build();

        when(columnRepository.findAllByBoardId(boardId)).thenReturn(List.of(c1, c2));

        List<Columns> result = columnService.findAllByBoardId(boardId);

        assertThat(result).containsExactly(c1, c2);
        verify(columnRepository).findAllByBoardId(boardId);
    }

//Simula ColumnRepository.findById(id) retornando uma coluna e valida que o service retorna a mesma coluna.
    @Test
    void findById_shouldReturnColumnWhenExists() {
        UUID colId = UUID.randomUUID();

        Columns columns = Columns.builder().id(colId).name("Todo").build();
        when(columnRepository.findById(colId)).thenReturn(Optional.of(columns));

        Columns result = columnService.findById(colId);

        assertThat(result.getId()).isEqualTo(colId);
        assertThat(result.getName()).isEqualTo("Todo");
        verify(columnRepository).findById(colId);
    }

//Simula coluna inexistente no repository e valida que o service lança ResourceNotFoundException com mensagem contendo o id.
    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID colId = UUID.randomUUID();

        when(columnRepository.findById(colId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> columnService.findById(colId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Column não encontrado")
                .hasMessageContaining(colId.toString());

        verify(columnRepository).findById(colId);
    }
}
