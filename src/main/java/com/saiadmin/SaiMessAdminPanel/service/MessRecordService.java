package com.saiadmin.SaiMessAdminPanel.service;

import com.saiadmin.SaiMessAdminPanel.entity.DailyRecord;
import com.saiadmin.SaiMessAdminPanel.entity.DailySummary;
import com.saiadmin.SaiMessAdminPanel.repository.DailyRecordRepository;
import com.saiadmin.SaiMessAdminPanel.repository.DailySummaryRepository;
import com.saiadmin.SaiMessAdminPanel.repository.UserRepository;
import com.saiadmin.SaiMessAdminPanel.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessRecordService {

    private final DailyRecordRepository recordRepo;
    private final DailySummaryRepository summaryRepo;
    private final UserRepository userRepo;

    public void saveDailyRecords(List<DailyRecord> records) {

        // Collect distinct dates processed so we can recompute summaries per date
        Set<LocalDate> datesProcessed = new HashSet<>();

        // Save or update each incoming record (one per user per day)
        for (DailyRecord incoming : records) {

            LocalDate date = incoming.getRecordDate() != null ? incoming.getRecordDate() : LocalDate.now();
            datesProcessed.add(date);

            // fetch managed user
            User user = incoming.getUser();
            if (user == null || user.getId() == null) {
                // skip invalid entries
                continue;
            }
            User managedUser = userRepo.findById(user.getId()).orElse(null);
            if (managedUser == null) {
                continue;
            }

            DailyRecord target;
            Optional<DailyRecord> existingOpt = recordRepo.findByUserAndRecordDate(managedUser, date);
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
            } else {
                target = new DailyRecord();
                target.setUser(managedUser);
                target.setRecordDate(date);
            }

            // copy incoming choices
            target.setMorningStatus(incoming.getMorningStatus());
            target.setMorningRate(incoming.getMorningRate());
            target.setMorningQuantity(incoming.getMorningQuantity());

            target.setNightStatus(incoming.getNightStatus());
            target.setNightRate(incoming.getNightRate());
            target.setNightQuantity(incoming.getNightQuantity());

            // calculate bills
            if ("yes".equalsIgnoreCase(target.getMorningStatus())) {
                int morningBill = target.getMorningRate() * target.getMorningQuantity();
                target.setMorningBill(morningBill);
            } else {
                target.setMorningRate(0);
                target.setMorningQuantity(0);
                target.setMorningBill(0);
            }

            if ("yes".equalsIgnoreCase(target.getNightStatus())) {
                int nightBill = target.getNightRate() * target.getNightQuantity();
                target.setNightBill(nightBill);
            } else {
                target.setNightRate(0);
                target.setNightQuantity(0);
                target.setNightBill(0);
            }

            target.setTotalBill(target.getMorningBill() + target.getNightBill());

            recordRepo.save(target);
        }

        // For each processed date, recompute summary from DB
        for (LocalDate date : datesProcessed) {
            int totalMorningQty = 0;
            int totalNightQty = 0;
            int totalCollection = 0;

            List<DailyRecord> recordsForDate = recordRepo.findMonthly(date, date);
            for (DailyRecord r : recordsForDate) {
                totalMorningQty += r.getMorningQuantity();
                totalNightQty += r.getNightQuantity();
                totalCollection += r.getTotalBill();
            }

            DailySummary summary = summaryRepo.findBySummaryDate(date);
            if (summary == null) {
                summary = new DailySummary();
                summary.setSummaryDate(date);
            }

            summary.setTotalMorningTiffins(totalMorningQty);
            summary.setTotalNightTiffins(totalNightQty);
            summary.setTotalCollection(totalCollection);

            summaryRepo.save(summary);
        }
    }
    public Map<String, Object> getMonthlyBill(Long userId, int month, int year) {

        List<Object[]> data =
                recordRepo.findMonthlyTiffinSummary(userId, month, year);

        int totalAmount = 0;
        List<String> billLines = new ArrayList<>();

        for (Object[] row : data) {
            int price = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();

            billLines.add(count + " × " + price + "=" + count*price);
            totalAmount += count * price;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("details", String.join(", ", billLines));
        result.put("total", totalAmount);

        return result;
    }

}

