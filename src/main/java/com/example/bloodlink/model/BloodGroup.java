package com.example.bloodlink.model;

import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BloodGroup {

    A_POS("A+"), A_NEG("A-"),
    B_POS("B+"), B_NEG("B-"),
    AB_POS("AB+"), AB_NEG("AB-"),
    O_POS("O+"), O_NEG("O-");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    // JSON uses the readable form: "A+", "O-" ...
    @JsonValue
    public String getLabel() {
        return label;
    }

    // accepts "A+", "a+", "A_POS". A '+' inside a query string reaches us as a space,
    // so a space is treated as '+'.
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BloodGroup from(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Blood group is required");
        }
        String t = text.toUpperCase().replace(' ', '+').trim();
        for (BloodGroup g : values()) {
            if (g.label.equals(t) || g.name().equals(t)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown blood group: " + text);
    }

    // groups whose blood this group (as a patient) can safely receive
    public Set<BloodGroup> compatibleDonors() {
        return switch (this) {
            case O_NEG -> EnumSet.of(O_NEG);
            case O_POS -> EnumSet.of(O_POS, O_NEG);
            case A_NEG -> EnumSet.of(A_NEG, O_NEG);
            case A_POS -> EnumSet.of(A_POS, A_NEG, O_POS, O_NEG);
            case B_NEG -> EnumSet.of(B_NEG, O_NEG);
            case B_POS -> EnumSet.of(B_POS, B_NEG, O_POS, O_NEG);
            case AB_NEG -> EnumSet.of(AB_NEG, A_NEG, B_NEG, O_NEG);
            case AB_POS -> EnumSet.allOf(BloodGroup.class);
        };
    }

    public boolean canReceiveFrom(BloodGroup donor) {
        return compatibleDonors().contains(donor);
    }
}
