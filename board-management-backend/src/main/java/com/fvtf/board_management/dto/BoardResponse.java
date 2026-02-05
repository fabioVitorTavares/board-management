package com.fvtf.board_management.dto;

import java.util.List;
import java.util.UUID;

public record BoardResponse(
        UUID id,
        String name,
        List<ColumnResponse> columns
) {}
