package com.example.geektrust.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceDtoTest {

    @Test
    public void testGettersAndSetters() {
        // Create a BalanceDto object
        BalanceDto balanceDto = new BalanceDto(100L);

        // Test getter
        assertEquals(100L, balanceDto.getBalance());

        // Test setter
        balanceDto.setBalance(200L);
        assertEquals(200L, balanceDto.getBalance());
    }

    @Test
    public void testToString() {
        // Create a BalanceDto object
        BalanceDto balanceDto = new BalanceDto(150L);

        // Test toString method
        String expectedToString = "BalanceDto(balance=150)";
        assertEquals(expectedToString, balanceDto.toString());
    }
}
