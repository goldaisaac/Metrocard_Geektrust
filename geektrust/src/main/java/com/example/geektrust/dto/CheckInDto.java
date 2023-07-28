package com.example.geektrust.dto;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing the details of a check-in operation for a MetroCard.
 * This class holds the category and destination of the check-in.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDto {

    /**
     * The category of the check-in, which indicates whether it is an adult or a child passenger.
     */
    private Category category;

    /**
     * The destination of the check-in, which represents the station or location where the passenger checks in.
     */
    private Destination destination;

}
