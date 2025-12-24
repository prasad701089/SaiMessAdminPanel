package com.saiadmin.SaiMessAdminPanel.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate summaryDate;

    private int totalMorningTiffins;   // total morning quantity count
    private int totalNightTiffins;     // total night quantity count

    private int totalCollection;       // full day collection (morning + night)
}
