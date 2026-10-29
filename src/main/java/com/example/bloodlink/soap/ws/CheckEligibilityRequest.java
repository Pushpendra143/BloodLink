package com.example.bloodlink.soap.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"donorId"})
@XmlRootElement(name = "CheckEligibilityRequest")
public class CheckEligibilityRequest {

    private long donorId;

    public long getDonorId() { return donorId; }
    public void setDonorId(long donorId) { this.donorId = donorId; }
}
