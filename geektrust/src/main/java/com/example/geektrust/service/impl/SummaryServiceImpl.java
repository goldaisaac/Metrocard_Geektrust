package com.example.geektrust.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.geektrust.constants.Category;
import com.example.geektrust.response.PassengerSummary;
import com.example.geektrust.service.SummaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SummaryServiceImpl implements SummaryService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the summary for each PassengerSummary in the provided list and calls the provided printFunction for
     * each generated summary.
     *
     * @param passengerSummaries The list of PassengerSummary objects to process.
     * @param printSummary      A Consumer function that defines what to do with the generated summary string.
     *                           It accepts a String parameter representing the summary and returns no result.
     */
    @Override
    public void processSummary(List<PassengerSummary> passengerSummaries, Consumer<String> printSummary) {
        // Loop through each PassengerSummary in the list
        passengerSummaries.forEach(passengerSummary -> {
            try {
                // Generate the summary string for the current PassengerSummary
                String summary = generateSummary(passengerSummary);
                // Pass the summary to the provided printFunction for processing
                printSummary.accept(summary);
                log.info("PassengerSummaries: {}", objectMapper.writeValueAsString(passengerSummaries));

            } catch (JsonProcessingException e) {
                log.error("Error converting PassengerSummaries to JSON: {}", e.getMessage());
            }
        });
    }


    /**
     * Generates the summary string for a single PassengerSummary object.
     *
     * @param passengerSummary The PassengerSummary object for which to generate the summary.
     * @return The generated summary string.
     */
    private static String generateSummary(PassengerSummary passengerSummary) {
        StringBuilder summaryBuilder = new StringBuilder();
        // Append total amount summary
        appendTotalAmountSummary(summaryBuilder, passengerSummary);
        // Append passenger type summary
        appendPassengerTypeSummary(summaryBuilder, passengerSummary);
        return summaryBuilder.toString();
    }

    /**
     * Appends the total amount summary to the provided StringBuilder.
     *
     * @param summaryBuilder The StringBuilder to which the summary will be appended.
     * @param passengerSummary The PassengerSummary object from which to extract the data.
     */
    private static void appendTotalAmountSummary(StringBuilder summaryBuilder, PassengerSummary passengerSummary) {
        summaryBuilder.append(String.format("TOTAL_COLLECTION %s %d %d", passengerSummary.getDestination(),
                passengerSummary.getTotalAmountCollected(), passengerSummary.getReturnAmountCollected()))
                .append("\n");
    }

    /**
     * Appends the passenger type summary to the provided StringBuilder.
     *
     * @param summaryBuilder The StringBuilder to which the summary will be appended.
     * @param passengerSummary The PassengerSummary object from which to extract the data.
     */
    private static void appendPassengerTypeSummary(StringBuilder summaryBuilder, PassengerSummary passengerSummary) {
        summaryBuilder.append("PASSENGER_TYPE_SUMMARY\n");
        Map<String, Integer> convertedMap = convertEnumMapToStringMap(passengerSummary.getPassengerCount());
        summaryBuilder.append(generatePassengerTypeSummary(new ArrayList<>(convertedMap.entrySet())));
    }

    /**
     * Generates the passenger type summary string based on the sorted passenger list.
     *
     * @param passengerList The sorted list of passenger type entries (key: passenger type, value: count).
     * @return The passenger type summary as a formatted string.
     */
	private static String generatePassengerTypeSummary(List<Map.Entry<String, Integer>> passengerList) {
		return passengerList.stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining("\n"));
	}

    /**
     * Converts an EnumMap to a Map with keys converted to strings.
     *
     * @param enumMap The EnumMap to be converted.
     * @return The converted Map with string keys and integer values.
     */
    private static Map<String, Integer> convertEnumMapToStringMap(EnumMap<Category, Integer> enumMap) {
        return enumMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
    }
}