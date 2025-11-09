package com.habitus.apipi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "registros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idusuariohabito", nullable = false)
    private Long userHabitId;

    @Column(name = "valor", precision = 14, scale = 4)
    private BigDecimal value;

    @Column(name = "data")
    private OffsetDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuariohabito", insertable = false, updatable = false)
    private UserHabit userHabit;

}
