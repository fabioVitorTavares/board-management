package com.fvtf.board_management.dto;

import java.util.List;
import java.util.UUID;

public record ColumnResponse(
        UUID id,
        String name,
        List<CardResponse> cards,
        UUID boardId
) {}
