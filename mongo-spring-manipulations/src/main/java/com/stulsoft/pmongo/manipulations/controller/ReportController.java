/*
 * Copyright (c) StulSoft, 2022
 */

package com.stulsoft.pmongo.manipulations.controller;

import com.stulsoft.pmongo.manipulations.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public String getAll() {
        return reportService.showAllReports();
    }

    @GetMapping("/allLast")
    public String getAllLast() {
        return reportService.showAllLastReport();
    }

    @PostMapping("/recreateReports")
    public void recreateReports() {
        reportService.recreateReports();
    }

    @PutMapping("/updateReport")
    public String updateReport() {
        reportService.updateReport();
        return "done";
    }
}
