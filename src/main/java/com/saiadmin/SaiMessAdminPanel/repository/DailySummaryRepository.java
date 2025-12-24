package com.saiadmin.SaiMessAdminPanel.repository;


import com.saiadmin.SaiMessAdminPanel.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
    DailySummary findBySummaryDate(LocalDate date);
}

