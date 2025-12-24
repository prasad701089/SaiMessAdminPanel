package com.saiadmin.SaiMessAdminPanel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "daily_record", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "record_date"}))
@Data
public class DailyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "record_date")
    private LocalDate recordDate;

    // ---------- MORNING ----------
    private String morningStatus;      // yes / no
    private int morningRate;           // 40/50/60
    private int morningQuantity;       // 1,2,3...
    private int morningBill;           // rate * qty

    // ---------- NIGHT ----------
    private String nightStatus;        // yes / no
    private int nightRate;             // 40/50/60
    private int nightQuantity;         // 1,2,3...
    private int nightBill;             // rate * qty

    // ---------- TOTAL ----------
    private int totalBill;             // morningBill + nightBill
}
