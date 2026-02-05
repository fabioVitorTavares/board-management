package com.fvtf.board_management.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrUpdateTitleDescriptionRequest(
        @NotBlank String title,
        String description
) {}
