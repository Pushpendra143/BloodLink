package com.example.bloodlink.soap.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"donorId", "donorName", "eligible", "message", "nextEligibleDate"})
@XmlRootElement(name = "CheckEligibilityResponse")
public class CheckEligibilityResponse {

    private long donorId;
    private String donorName;
    private boolean eligible;
    private String message;
    private String nextEligibleDate;

    public long getDonorId() { return donorId; }
    public void setDonorId(long donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNextEligibleDate() { return nextEligibleDate; }
    public void setNextEligibleDate(String nextEligibleDate) { this.nextEligibleDate = nextEligibleDate; }
}
