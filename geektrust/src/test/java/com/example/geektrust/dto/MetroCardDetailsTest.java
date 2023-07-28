package com.example.geektrust.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;

public class MetroCardDetailsTest {

    @Test
    public void testGettersAndSetters() {
        // Create a sample BalanceDto
        BalanceDto balanceDto = new BalanceDto(100L);

        // Create a sample CheckInDto list
        List<CheckInDto> checkInDtoList = new ArrayList<>();
        CheckInDto checkInDto1 = new CheckInDto(Category.ADULT, Destination.AIRPORT);
        CheckInDto checkInDto2 = new CheckInDto(Category.ADULT, Destination.CENTRAL);
        checkInDtoList.add(checkInDto1);
        checkInDtoList.add(checkInDto2);

        // Create a MetroCardDetails object
        MetroCardDetails metroCardDetails = new MetroCardDetails();
        metroCardDetails.setMetroCardId("12345");
        metroCardDetails.setBalance(balanceDto);
        metroCardDetails.setCheckInDetails(checkInDtoList);

        // Test getters
        assertEquals("12345", metroCardDetails.getMetroCardId());
        assertEquals(balanceDto, metroCardDetails.getBalance());
        assertEquals(checkInDtoList, metroCardDetails.getCheckInDetails());

        // Test toString method
        String expectedToString = "MetroCardDetails(metroCardId=12345, balance=BalanceDto(balance=100), checkInDetails=[CheckInDto(category=ADULT, destination=AIRPORT), CheckInDto(category=ADULT, destination=CENTRAL)])";
        assertEquals(expectedToString, metroCardDetails.toString());
    }
}
