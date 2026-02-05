


package com.fvtf.board_management.controller;

import com.fvtf.board_management.dto.BoardResponse;
import com.fvtf.board_management.dto.ColumnResponse;
import com.fvtf.board_management.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(BoardControllerTest.MockConfig.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(boardService);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        BoardService boardService() {
            return Mockito.mock(BoardService.class);
        }
    }

    @Test
    void createBoard_shouldReturn201_andBoardBody() throws Exception {
        UUID id = UUID.randomUUID();

        // O controller retorna Board (entity) no createBoard
        var created = com.fvtf.board_management.entity.Board.builder()
                .id(id)
                .name("Meu Board")
                .build();

        when(boardService.create("Meu Board")).thenReturn(created);

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Meu Board" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Meu Board"));

        verify(boardService).create("Meu Board");
    }

    @Test
    void createBoard_shouldReturn400_whenNameBlank() throws Exception {
        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "" }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }

    @Test
    void createColumnInBoard_shouldReturn201_andBoardResponse() throws Exception {
        UUID boardId = UUID.randomUUID();
        UUID colId = UUID.randomUUID();

        BoardResponse response = new BoardResponse(
                boardId,
                "Board 1",
                List.of(new ColumnResponse(colId, "Todo", List.of(), boardId))
        );

        when(boardService.createColumInBoard("Todo", boardId)).thenReturn(response);

        mockMvc.perform(post("/boards/{id}/columns", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Todo" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(boardId.toString()))
                .andExpect(jsonPath("$.name").value("Board 1"))
                .andExpect(jsonPath("$.columns[0].id").value(colId.toString()))
                .andExpect(jsonPath("$.columns[0].name").value("Todo"))
                .andExpect(jsonPath("$.columns[0].boardId").value(boardId.toString()));

        verify(boardService).createColumInBoard("Todo", boardId);
    }

    @Test
    void createColumnInBoard_shouldReturn400_whenNameBlank() throws Exception {
        UUID boardId = UUID.randomUUID();

        mockMvc.perform(post("/boards/{id}/columns", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "   " }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(boardService);
    }

    @Test
    void findAll_shouldReturn200_andList() throws Exception {
        UUID b1 = UUID.randomUUID();
        UUID b2 = UUID.randomUUID();

        when(boardService.findAll()).thenReturn(List.of(
                new BoardResponse(b1, "B1", List.of()),
                new BoardResponse(b2, "B2", List.of())
        ));

        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(b1.toString()))
                .andExpect(jsonPath("$[0].name").value("B1"))
                .andExpect(jsonPath("$[1].id").value(b2.toString()))
                .andExpect(jsonPath("$[1].name").value("B2"));

        verify(boardService).findAll();
    }

    @Test
    void getBoardById_shouldReturn200_andBoardResponse() throws Exception {
        UUID boardId = UUID.randomUUID();

        when(boardService.findById(boardId))
                .thenReturn(new BoardResponse(boardId, "Board X", List.of()));

        mockMvc.perform(get("/boards/{id}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardId.toString()))
                .andExpect(jsonPath("$.name").value("Board X"));

        verify(boardService).findById(boardId);
    }
}
