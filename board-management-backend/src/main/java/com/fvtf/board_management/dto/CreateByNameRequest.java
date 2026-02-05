package com.fvtf.board_management.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateByNameRequest(
        @NotBlank(message = "name is required") String name
) {}
