package com.example.geektrust.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;

public class CheckInDtoTest {

    @Test
    public void testGettersAndSetters() {
        // Create a CheckInDto object
        CheckInDto checkInDto = new CheckInDto(Category.ADULT, Destination.AIRPORT);

        // Test getters
        assertEquals(Category.ADULT, checkInDto.getCategory());
        assertEquals(Destination.AIRPORT, checkInDto.getDestination());

        // Test setters
        checkInDto.setCategory(Category.KID);
        checkInDto.setDestination(Destination.CENTRAL);
        assertEquals(Category.KID, checkInDto.getCategory());
        assertEquals(Destination.CENTRAL, checkInDto.getDestination());
    }

    @Test
    public void testToString() {
        // Create a CheckInDto object
        CheckInDto checkInDto = new CheckInDto(Category.ADULT, Destination.AIRPORT);

        // Test toString method
        String expectedToString = "CheckInDto(category=ADULT, destination=AIRPORT)";
        assertEquals(expectedToString, checkInDto.toString());
    }
}
