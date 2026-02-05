package com.fvtf.board_management.controller;

import com.fvtf.board_management.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/columns")
public class ColumnController {

    private final ColumnService columnService;

}
