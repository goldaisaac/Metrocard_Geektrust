package com.example.geektrust.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.constants.InputCommands;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetroPaymentCardApplicationService implements CommandLineRunner {

    @Autowired
    CheckInService checkInService;

    @Autowired
    BalanceService balanceService;

    @Autowired
    SummaryService summaryService;

    // Lists to hold MetroCard details and passenger summaries
    private final List<MetroCardDetails> metroCardDetails;
    private final List<PassengerSummary> passengerSummaries;

    // Constructor to inject the lists of MetroCardDetails and PassengerSummary
    @Autowired
    public MetroPaymentCardApplicationService(List<MetroCardDetails> metroCardDetails, List<PassengerSummary> passengerSummaries) {
        this.metroCardDetails = metroCardDetails;
        this.passengerSummaries = passengerSummaries;
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("MetroPaymentCardApplicationService - START");

        // Initialize PassengerSummary objects for each destination
        for (Destination destination : Destination.values()) {
            PassengerSummary passengerSummary = new PassengerSummary();
            passengerSummary.setDestination(destination);
            EnumMap<Category, Integer> countMap = new EnumMap<>(Category.class);
            passengerSummary.setPassengerCount(countMap);
            passengerSummaries.add(passengerSummary);
        }

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Enter file name");
            String fileName = scanner.next();
            log.info("Processing File : {}", fileName);

            try {
                List<String> items = Files.readAllLines(Paths.get(fileName));
                // Process each line of input data
                items.stream()
                        .map(line -> line.split(" "))
                        .filter(words -> words.length > 0)
                        .forEach(words -> {
                            String firstWord = words[0];
                            InputCommands command = InputCommands.valueOf(firstWord.toUpperCase());
                            switch (command) {
                                case BALANCE:
                                    // Invoke the BalanceService to process the balance command
                                    balanceService.processBalance(metroCardDetails, String.join(" ", words));
                                    break;
                                case CHECK_IN:
                                    // Invoke the CheckInService to process the check-in command
                                    checkInService.checkIn(metroCardDetails, passengerSummaries, String.join(" ", words));
                                    break;
                                case PRINT_SUMMARY:
                                    // Invoke the SummaryService to process the print summary command
                                    summaryService.processSummary(passengerSummaries, summary -> System.out.println(summary));
                                    break;
                                default:
                                    // Throw an exception for unexpected command types
                                    throw new IllegalArgumentException("Unexpected value: " + command);
                            }
                        });
            } catch (IOException e) {
                System.out.println("File not found / empty");
            }
        }

        log.info("MetroPaymentCardApplicationService - END");
    }
}
