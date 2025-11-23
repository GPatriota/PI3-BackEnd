package com.habitus.apipi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usuarioshabitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idusuario", nullable = false)
    private Long userId;

    @Column(name = "idhabito", nullable = false)
    private Long habitId;

    @Column(name = "idunidademedida", nullable = false)
    private Long measurementUnitId;

    @Column(name = "metadiaria", precision = 14, scale = 4)
    private BigDecimal dailyGoal;

    @Column(name = "frequenciasemanal")
    private Short weeklyFrequency;

    @Column(name = "datainicio")
    private LocalDate startDate;

    @Column(name = "datafim")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idhabito", insertable = false, updatable = false)
    private Habit habit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idunidademedida", insertable = false, updatable = false)
    private MeasurementUnit measurementUnit;

}
