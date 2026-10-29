package com.example.bloodlink.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.bloodlink.model.Hospital;
import com.example.bloodlink.service.HospitalService;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hospital register(@Valid @RequestBody Hospital hospital) {
        return hospitalService.register(hospital);
    }

    @GetMapping
    public List<Hospital> list() {
        return hospitalService.getAll();
    }

    @GetMapping("/{id}")
    public Hospital get(@PathVariable Long id) {
        return hospitalService.getById(id);
    }
}
