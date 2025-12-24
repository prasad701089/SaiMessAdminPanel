package com.saiadmin.SaiMessAdminPanel.controller;

import com.saiadmin.SaiMessAdminPanel.entity.DailyRecord;
import com.saiadmin.SaiMessAdminPanel.entity.User;
import com.saiadmin.SaiMessAdminPanel.repository.DailyRecordRepository;
import com.saiadmin.SaiMessAdminPanel.repository.UserRepository;
import com.saiadmin.SaiMessAdminPanel.service.AdminService;
import com.saiadmin.SaiMessAdminPanel.service.MessRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


@Controller
public class LoginController {
    @Autowired
    private MessRecordService messRecordService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DailyRecordRepository dailyRecordRepository;
    @Autowired
    private com.saiadmin.SaiMessAdminPanel.repository.DailySummaryRepository dailySummaryRepository;
    private final AdminService service;

    public LoginController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";   // login.html
    }

    @PostMapping("/login")
    @ResponseBody
    public String loginValidate(@RequestParam String adminId,
                                @RequestParam String password,
                                HttpSession session) {

        if (service.validateLogin(adminId, password)) {
            session.setAttribute("admin", adminId);
            return "success";
        }

        return "fail";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "year", required = false) Integer year,
                            @RequestParam(value = "month", required = false) Integer month,
                            Model model) {
        List<User> userList = userRepository.findAll();
        model.addAttribute("userList", userList);

        // today's summary (always for today)
        LocalDate today = LocalDate.now();
        com.saiadmin.SaiMessAdminPanel.entity.DailySummary summary = dailySummaryRepository.findBySummaryDate(today);

        int totalMorning = 0;
        int totalNight = 0;
        int totalCollection = 0;
        if (summary != null) {
            totalMorning = summary.getTotalMorningTiffins();
            totalNight = summary.getTotalNightTiffins();
            totalCollection = summary.getTotalCollection();
        }

        model.addAttribute("totalMorning", totalMorning);
        model.addAttribute("totalNight", totalNight);
        model.addAttribute("totalCollection", totalCollection);
        model.addAttribute("hasSummary", summary != null);

        // today's records map by user id to prefill UI
        List<com.saiadmin.SaiMessAdminPanel.entity.DailyRecord> todays = dailyRecordRepository.findMonthly(today, today);
        Map<Long, com.saiadmin.SaiMessAdminPanel.entity.DailyRecord> map = new HashMap<>();
        for (com.saiadmin.SaiMessAdminPanel.entity.DailyRecord r : todays) {
            if (r.getUser() != null && r.getUser().getId() != null) {
                map.put(r.getUser().getId(), r);
            }
        }
        model.addAttribute("todayRecordsMap", map);

        // ---------- Monthly history grid for selected month (defaults to current) ----------
        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<com.saiadmin.SaiMessAdminPanel.entity.DailyRecord> monthRecords = dailyRecordRepository.findMonthly(start, end);
        Map<Long, Map<Integer, com.saiadmin.SaiMessAdminPanel.entity.DailyRecord>> monthlyMap = new HashMap<>();
        for (com.saiadmin.SaiMessAdminPanel.entity.DailyRecord r : monthRecords) {
            if (r.getUser() != null && r.getUser().getId() != null && r.getRecordDate() != null) {
                long uid = r.getUser().getId();
                int day = r.getRecordDate().getDayOfMonth();
                monthlyMap.computeIfAbsent(uid, k -> new HashMap<>()).put(day, r);
            }
        }

        // list of days for header
        int daysInMonth = ym.lengthOfMonth();
        List<Integer> monthDays = IntStream.rangeClosed(1, daysInMonth).boxed().toList();

        model.addAttribute("monthDays", monthDays);
        model.addAttribute("monthlyMap", monthlyMap);
        model.addAttribute("monthYearLabel", ym.getMonth().toString() + " " + ym.getYear());
        model.addAttribute("selectedMonthValue", String.format("%04d-%02d", ym.getYear(), ym.getMonthValue()));

        // default selected entry date (for saves) - default to today
        model.addAttribute("selectedEntryDate", today.toString());

        return "dashboard";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/monthly-bill/{userId}")
    public String showMonthlyBill(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year,
            Model model) {

        Map<String, Object> bill =
                messRecordService.getMonthlyBill(userId, month, year);

        model.addAttribute("billDetails", bill.get("details"));
        model.addAttribute("totalAmount", bill.get("total"));

        return "monthly-bill";
    }


}
