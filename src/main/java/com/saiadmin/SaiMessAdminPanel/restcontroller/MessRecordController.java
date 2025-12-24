package com.saiadmin.SaiMessAdminPanel.restcontroller;

import com.saiadmin.SaiMessAdminPanel.entity.DailyRecord;
import com.saiadmin.SaiMessAdminPanel.service.MessRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MessRecordController {

    private final MessRecordService recordService;

    @PostMapping("/save")
    public String saveDailyRecords(@RequestBody List<DailyRecord> records) {

        for (DailyRecord rec : records) {

            // Auto set today's date if missing
            if (rec.getRecordDate() == null) {
                rec.setRecordDate(LocalDate.now());
            }

            // ---------- MORNING ----------
            if (!"yes".equalsIgnoreCase(rec.getMorningStatus())) {
                rec.setMorningStatus("no");
                rec.setMorningRate(0);
                rec.setMorningQuantity(0);
                rec.setMorningBill(0);
            } else {
                int bill = rec.getMorningRate() * rec.getMorningQuantity();
                rec.setMorningBill(bill);
            }

            // ---------- NIGHT ----------
            if (!"yes".equalsIgnoreCase(rec.getNightStatus())) {
                rec.setNightStatus("no");
                rec.setNightRate(0);
                rec.setNightQuantity(0);
                rec.setNightBill(0);
            } else {
                int bill = rec.getNightRate() * rec.getNightQuantity();
                rec.setNightBill(bill);
            }

            // ---------- TOTAL ----------
            rec.setTotalBill(rec.getMorningBill() + rec.getNightBill());
        }

        recordService.saveDailyRecords(records);
        return "Daily records saved successfully.";
    }


}
