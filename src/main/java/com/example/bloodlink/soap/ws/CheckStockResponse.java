package com.example.bloodlink.soap.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"bloodGroup", "unitsAvailable", "level"})
@XmlRootElement(name = "CheckStockResponse")
public class CheckStockResponse {

    private String bloodGroup;
    private int unitsAvailable;
    private String level;

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(int unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
