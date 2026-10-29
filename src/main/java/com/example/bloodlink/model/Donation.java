package com.example.bloodlink.model;

import java.time.LocalDate;

import jakarta.persistence.*;

// one row = one unit of blood given by one donor
@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "donor_id")
    private Donor donor;

    // null when the blood went into the general stock
    @ManyToOne
    @JoinColumn(name = "request_id")
    private BloodRequest request;

    private LocalDate donatedOn;

    public Donation() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public BloodRequest getRequest() { return request; }
    public void setRequest(BloodRequest request) { this.request = request; }

    public LocalDate getDonatedOn() { return donatedOn; }
    public void setDonatedOn(LocalDate donatedOn) { this.donatedOn = donatedOn; }
}
