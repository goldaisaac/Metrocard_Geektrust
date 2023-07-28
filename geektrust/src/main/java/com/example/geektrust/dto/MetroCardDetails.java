package com.example.geektrust.dto;

import java.util.List;

import lombok.Data;

/**
 * A data class representing the details of a MetroCard, including its ID, balance, and check-in history.
 */
@Data
public class MetroCardDetails {

    /**
     * The unique ID of the MetroCard.
     */
    String metroCardId;

    /**
     * The balance information associated with the MetroCard.
     */
    BalanceDto balance;

    /**
     * The list of check-in details for the MetroCard, representing its travel history.
     */
    List<CheckInDto> checkInDetails;
}