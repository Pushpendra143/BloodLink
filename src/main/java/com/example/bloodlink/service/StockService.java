package com.example.bloodlink.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloodlink.exception.BusinessRuleException;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.BloodStock;
import com.example.bloodlink.repository.BloodStockRepository;

@Service
public class StockService {

    private static final int LOW_LEVEL = 5;

    private final BloodStockRepository stockRepository;

    public StockService(BloodStockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // a group that was never touched simply has 0 units
    public BloodStock get(BloodGroup group) {
        return stockRepository.findById(group).orElse(new BloodStock(group, 0));
    }

    public List<BloodStock> getAll() {
        return Arrays.stream(BloodGroup.values()).map(this::get).toList();
    }

    @Transactional
    public BloodStock add(BloodGroup group, int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Units must be greater than zero");
        }
        BloodStock stock = get(group);
        stock.setUnitsAvailable(stock.getUnitsAvailable() + units);
        return stockRepository.save(stock);
    }

    @Transactional
    public BloodStock take(BloodGroup group, int units) {
        BloodStock stock = get(group);
        if (stock.getUnitsAvailable() < units) {
            throw new BusinessRuleException("Only " + stock.getUnitsAvailable()
                    + " unit(s) of " + group.getLabel() + " in stock, cannot take " + units);
        }
        stock.setUnitsAvailable(stock.getUnitsAvailable() - units);
        return stockRepository.save(stock);
    }

    public String levelFor(int units) {
        if (units == 0) {
            return "OUT_OF_STOCK";
        }
        return units < LOW_LEVEL ? "LOW" : "OK";
    }
}
