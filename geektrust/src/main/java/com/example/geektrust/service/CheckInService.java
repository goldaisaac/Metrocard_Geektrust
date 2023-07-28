package com.example.geektrust.service;

import java.util.List;

import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;

/**
 * The CheckInService interface provides methods to perform check-in operations for MetroCardDetails and update passenger summaries.
 */
public interface CheckInService {

    /**
     * Performs the check-in operation for a list of MetroCardDetails based on the provided data.
     * This method updates the check-in details for the MetroCards and invokes the necessary processing
     * to update passenger summaries and perform balance checks based on the check-in data.
     *
     * @param metroCardDetails   The list of MetroCard details to perform check-in for.
     * @param passengerSummaries The list of passenger summaries to be updated based on the check-in data.
     * @param data               The check-in data containing instructions for the check-in operation.
     * @return The updated list of MetroCard details after performing the check-in operations.
     */
    List<MetroCardDetails> checkIn(List<MetroCardDetails> metroCardDetails, List<PassengerSummary> passengerSummaries, String data);

}
