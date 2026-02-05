package com.fvtf.board_management.entity;

import com.fvtf.board_management.dto.ColumnResponse;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "columns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Columns {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @jakarta.persistence.Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    public ColumnResponse getColumnResponse() {

        return new ColumnResponse(
                this.getId(),
                this.getName(),
                List.of(),
                this.getBoard().getId()
        );

    }

}