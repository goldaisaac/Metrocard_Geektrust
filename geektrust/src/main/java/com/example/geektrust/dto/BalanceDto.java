package com.example.geektrust.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO class representing the balance of a MetroCard.
 * This class is used to hold the balance value and provides getter and setter methods for accessing it.
 */
@Data
@AllArgsConstructor
public class BalanceDto {

    /**
     * The balance amount of the MetroCard.
     */
    private long balance;

}
