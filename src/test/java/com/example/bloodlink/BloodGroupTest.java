package com.example.bloodlink;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.bloodlink.model.BloodGroup;

class BloodGroupTest {

    @Test
    void oNegativeCanDonateToEveryone() {
        for (BloodGroup patient : BloodGroup.values()) {
            assertTrue(patient.canReceiveFrom(BloodGroup.O_NEG), patient + " should accept O-");
        }
    }

    @Test
    void abPositiveCanReceiveFromEveryone() {
        for (BloodGroup donor : BloodGroup.values()) {
            assertTrue(BloodGroup.AB_POS.canReceiveFrom(donor));
        }
    }

    @Test
    void incompatibleGroupsAreRejected() {
        assertFalse(BloodGroup.A_POS.canReceiveFrom(BloodGroup.B_POS));
        assertFalse(BloodGroup.O_NEG.canReceiveFrom(BloodGroup.O_POS));
        assertFalse(BloodGroup.A_NEG.canReceiveFrom(BloodGroup.A_POS));
    }

    @Test
    void parsesDifferentWritings() {
        assertEquals(BloodGroup.AB_NEG, BloodGroup.from("ab-"));
        assertEquals(BloodGroup.O_POS, BloodGroup.from("O_POS"));
        assertEquals(BloodGroup.A_POS, BloodGroup.from("A "));   // '+' arriving as space from a URL
    }

    @Test
    void unknownGroupThrows() {
        assertThrows(IllegalArgumentException.class, () -> BloodGroup.from("C+"));
    }
}
