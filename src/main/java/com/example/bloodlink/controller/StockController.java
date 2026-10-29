package com.example.bloodlink.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.BloodStock;
import com.example.bloodlink.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<BloodStock> all() {
        return stockService.getAll();
    }

    // e.g. /api/stock/O+  or  /api/stock/O_POS
    @GetMapping("/{group}")
    public BloodStock one(@PathVariable BloodGroup group) {
        return stockService.get(group);
    }

    // blood bank staff adding units that came from a camp
    // /api/stock/O_POS/add?units=5
    @PostMapping("/{group}/add")
    public BloodStock add(@PathVariable BloodGroup group, @RequestParam int units) {
        return stockService.add(group, units);
    }
}
