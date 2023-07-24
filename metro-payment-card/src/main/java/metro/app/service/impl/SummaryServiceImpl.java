package metro.app.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import metro.app.response.PassengerSummary;
import metro.app.service.SummaryService;

import java.util.function.Consumer;
import java.util.stream.Collectors;

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

                // Logging
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
    private String generateSummary(PassengerSummary passengerSummary) {
        StringBuilder summaryBuilder = new StringBuilder();
        // Create the first line of the summary with total amounts collected
        summaryBuilder.append(String.format("TOTAL_COLLECTION %s %d %d", passengerSummary.getDestination(),
                passengerSummary.getTotalAmountCollected(), passengerSummary.getReturnAmountCollected()))
                .append("\nPASSENGER_TYPE_SUMMARY\n");

        // Process passenger count entries using streams
        String passengerTypeSummary = passengerSummary.getPassengerCount().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining("\n"));

        // Append the passenger type summary to the main summary
        summaryBuilder.append(passengerTypeSummary);

        // Return the complete summary as a string
        return summaryBuilder.toString();
    }
}