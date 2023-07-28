package com.example.geektrust.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EnumMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;

public class PassengerSummaryTest {

    private PassengerSummary passengerSummary;

    @BeforeEach
    public void setUp() {
        passengerSummary = new PassengerSummary();
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        passengerSummary.setDestination(Destination.CENTRAL);
        assertEquals(Destination.CENTRAL, passengerSummary.getDestination());

        passengerSummary.setTotalAmountCollected(100L);
        assertEquals(100L, passengerSummary.getTotalAmountCollected());

        passengerSummary.setReturnAmountCollected(50L);
        assertEquals(50L, passengerSummary.getReturnAmountCollected());

        // Test passengerCount map
        passengerSummary.setPassengerCount(new EnumMap<>(Category.class));
        EnumMap<Category, Integer> passengerCount = passengerSummary.getPassengerCount();

        // Add some passenger count data
        passengerCount.put(Category.ADULT, 5);
        passengerCount.put(Category.KID, 3);

        // Verify the passenger count data
        assertEquals(5, passengerCount.get(Category.ADULT));
        assertEquals(3, passengerCount.get(Category.KID));
        assertNull(passengerCount.get(Category.SENIOR_CITIZEN)); // Ensure that an undefined key returns null

        // Test modifying passenger count directly from the PassengerSummary object
        passengerSummary.getPassengerCount().put(Category.ADULT, 10);
        assertEquals(10, passengerCount.get(Category.ADULT));
    }

    @Test
    public void testToString() {
        // Create a PassengerSummary object
        passengerSummary.setDestination(Destination.AIRPORT);
        passengerSummary.setTotalAmountCollected(200L);
        passengerSummary.setReturnAmountCollected(100L);
        passengerSummary.setPassengerCount(new EnumMap<>(Category.class));
        passengerSummary.getPassengerCount().put(Category.ADULT, 8);
        passengerSummary.getPassengerCount().put(Category.KID, 4);

        // Test toString method
        String expectedToString = "PassengerSummary(destination=AIRPORT, totalAmountCollected=200, returnAmountCollected=100, passengerCount={KID=4, ADULT=8})";
        assertEquals(expectedToString, passengerSummary.toString());
    }
}

