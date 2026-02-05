package com.fvtf.board_management.service;

import com.fvtf.board_management.dto.BoardResponse;
import com.fvtf.board_management.entity.Board;
import com.fvtf.board_management.entity.Columns;
import com.fvtf.board_management.exeptions.ResourceNotFoundException;
import com.fvtf.board_management.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColumnService {

    private final ColumnRepository columnRepository;

    public void create(Columns columns) {

        columnRepository.save(columns);

    }

    public List<Columns> findAllByBoardId(UUID id) {

        return columnRepository.findAllByBoardId(id);

    }

    public Columns findById(UUID id) {

        return columnRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Column não encontrado: " + id));

    }

}
