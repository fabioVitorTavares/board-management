package com.fvtf.board_management.dto;

import jakarta.validation.Valid;

import java.util.UUID;

public record MoveCardRequest(@Valid UUID newColumnId) {}
