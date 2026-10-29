package com.example.bloodlink.model;

import jakarta.persistence.*;

@Entity
@Table(name = "blood_stock")
public class BloodStock {

    @Id
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private int unitsAvailable;

    public BloodStock() {
    }

    public BloodStock(BloodGroup bloodGroup, int unitsAvailable) {
        this.bloodGroup = bloodGroup;
        this.unitsAvailable = unitsAvailable;
    }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(int unitsAvailable) { this.unitsAvailable = unitsAvailable; }
}
