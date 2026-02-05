package com.fvtf.board_management.entity;

import com.fvtf.board_management.dto.CardResponse;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "column_id", nullable = false)
    private Columns column;

    public CardResponse getCardResponse() {

        return new CardResponse(
                this.getId(),
                this.getTitle(),
                this.getDescription(),
                this.getColumn().getId());

    }

}