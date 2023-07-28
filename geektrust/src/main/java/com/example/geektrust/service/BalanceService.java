package com.example.geektrust.service;

import java.util.List;

import com.example.geektrust.dto.CheckInDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;

/**
 * The BalanceService interface provides methods to process balance-related operations
 * for MetroCardDetails and perform balance checks during check-ins.
 */
public interface BalanceService {

    /**
     * Processes balance-related operations for the provided MetroCardDetails based on the given data.
     * This method is responsible for updating the balance of the MetroCards based on various transactions,
     * such as recharge and journey fare deductions.
     *
     * @param metroCardDetails The list of MetroCard details to process.
     * @param data             The balance-related data that contains the instructions for updating the balances.
     * @return The updated list of MetroCard details after processing the balance operations.
     */
    List<MetroCardDetails> processBalance(List<MetroCardDetails> metroCardDetails, String data);

    /**
     * Checks the balance for a specific MetroCard during a check-in operation and performs necessary actions
     * based on the current check-in data, such as fare deductions and generating passenger summaries.
     * This method is invoked by the CheckInService to verify the balance and process check-ins.
     *
     * @param metroCardDetails   The list of MetroCard details to process.
     * @param cardDetails        The MetroCard details for which the balance check is performed.
     * @param isReturn           A boolean indicating if it is a return journey check-in.
     * @param currentCheckInData The current check-in data containing the category and destination information.
     * @param passengerSummaries The list of passenger summaries to be updated based on the check-in data.
     * @return The updated list of MetroCard details after processing the balance check.
     */
    List<MetroCardDetails> checkBalance(List<MetroCardDetails> metroCardDetails, MetroCardDetails cardDetails,
                                        boolean isReturn, CheckInDto currentCheckInData, List<PassengerSummary> passengerSummaries);

}
