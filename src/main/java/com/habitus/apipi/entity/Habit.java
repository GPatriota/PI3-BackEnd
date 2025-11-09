package com.habitus.apipi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "habitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idunidademedida", nullable = false)
    private Long measurementUnitId;

    @Column(name = "nome")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idunidademedida", insertable = false, updatable = false)
    private MeasurementUnit measurementUnit;

}
