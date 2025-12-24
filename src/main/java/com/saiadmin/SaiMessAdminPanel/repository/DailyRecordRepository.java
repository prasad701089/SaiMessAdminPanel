package com.saiadmin.SaiMessAdminPanel.repository;

import com.saiadmin.SaiMessAdminPanel.entity.DailyRecord;
import com.saiadmin.SaiMessAdminPanel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    @Query("SELECT d FROM DailyRecord d WHERE d.recordDate >= :start AND d.recordDate <= :end")
    List<DailyRecord> findMonthly(@Param("start") LocalDate start, @Param("end") LocalDate end);

    Optional<DailyRecord> findByUserAndRecordDate(User user, LocalDate recordDate);

    // Find records for a given user between start and end dates (inclusive)
    List<DailyRecord> findByUserAndRecordDateBetween(User user, LocalDate start, LocalDate end);

    @Query(value = """
            SELECT price, SUM(cnt) AS total_count FROM (
              SELECT morning_rate AS price,
                     CASE WHEN morning_status = 'yes' THEN morning_quantity ELSE 0 END AS cnt,
                     user_id, record_date
              FROM daily_record
              UNION ALL
              SELECT night_rate AS price,
                     CASE WHEN night_status = 'yes' THEN night_quantity ELSE 0 END AS cnt,
                     user_id, record_date
              FROM daily_record
            ) t
            WHERE t.user_id = :userId
              AND MONTH(t.record_date) = :month
              AND YEAR(t.record_date) = :year
            GROUP BY price
            """, nativeQuery = true)
    List<Object[]> findMonthlyTiffinSummary(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

}