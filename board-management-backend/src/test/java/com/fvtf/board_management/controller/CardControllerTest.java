package com.fvtf.board_management.controller;


import com.fvtf.board_management.dto.CardResponse;
import com.fvtf.board_management.service.CardService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CardControllerTest.MockConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardService cardService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(cardService);
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        CardService cardService() {
            return Mockito.mock(CardService.class);
        }
    }

    @Test
    void createCard_shouldReturn201_andCardResponse() throws Exception {
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(cardService.create(eq(columnId), any()))
                .thenReturn(new CardResponse(cardId, "Task 1", "Desc", columnId));

        mockMvc.perform(post("/columns/{id}/cards", columnId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "Task 1", "description": "Desc" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.columnId").value(columnId.toString()));

        verify(cardService).create(eq(columnId), any());
    }

    @Test
    void createCard_shouldReturn400_whenTitleBlank() throws Exception {
        UUID columnId = UUID.randomUUID();

        mockMvc.perform(post("/columns/{id}/cards", columnId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "   ", "description": "x" }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cardService);
    }

    @Test
    void updateCard_shouldReturn200_andCardResponse() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        when(cardService.update(eq(cardId), any()))
                .thenReturn(new CardResponse(cardId, "New", "NewDesc", columnId));

        mockMvc.perform(put("/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "title": "New", "description": "NewDesc" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.description").value("NewDesc"))
                .andExpect(jsonPath("$.columnId").value(columnId.toString()));

        verify(cardService).update(eq(cardId), any());
    }

    @Test
    void moveCard_shouldReturn200_andCardResponse() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID newColumnId = UUID.randomUUID();

        when(cardService.move(eq(cardId), any()))
                .thenReturn(new CardResponse(cardId, "Task", null, newColumnId));

        mockMvc.perform(patch("/cards/{id}/move", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "newColumnId": "%s" }
                                """.formatted(newColumnId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.columnId").value(newColumnId.toString()));

        verify(cardService).move(eq(cardId), any());
    }

    @Test
    void deleteCard_shouldReturn204() throws Exception {
        UUID cardId = UUID.randomUUID();

        mockMvc.perform(delete("/cards/{id}", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).delete(cardId);
    }


}
