package com.fvtf.board_management.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateByNameRequest(
        @NotBlank String name
) {}
