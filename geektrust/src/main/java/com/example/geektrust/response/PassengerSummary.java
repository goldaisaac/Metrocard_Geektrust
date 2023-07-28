package com.example.geektrust.response;

import java.util.EnumMap;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;

import lombok.Data;

/**
 * A data class representing the summary of passenger information for a specific destination.
 */
@Data
public class PassengerSummary {

    /**
     * The destination for which the summary is generated.
     */
    Destination destination;

    /**
     * The total amount collected from passengers traveling to the specified destination.
     */
    long totalAmountCollected;

    /**
     * The amount collected from passengers on return journeys to the specified destination.
     */
    long returnAmountCollected;

    /**
     * A map that stores the count of passengers based on their category (e.g., Adult, Child, etc.).
     * The map is represented by an EnumMap, where the keys are Category enums and the values are integers
     * representing the passenger count for each category.
     */
    private EnumMap<Category, Integer> passengerCount;
}
