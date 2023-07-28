package com.example.geektrust.service;

import java.util.List;
import java.util.function.Consumer;

import com.example.geektrust.response.PassengerSummary;

/**
 * The SummaryService interface provides a method for processing and generating summaries for PassengerSummary objects.
 */
public interface SummaryService {

    /**
     * Processes the summary for each PassengerSummary in the provided list and calls the provided printFunction for
     * each generated summary.
     *
     * @param passengerSummaries The list of PassengerSummary objects to process.
     * @param printFunction      A Consumer function that defines what to do with the generated summary string.
     *                           It accepts a String parameter representing the summary and returns no result.
     */
    void processSummary(List<PassengerSummary> passengerSummaries, Consumer<String> printFunction);
}
