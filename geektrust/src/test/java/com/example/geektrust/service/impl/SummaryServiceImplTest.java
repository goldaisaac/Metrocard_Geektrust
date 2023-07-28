package com.example.geektrust.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.response.PassengerSummary;

public class SummaryServiceImplTest {

    private SummaryServiceImpl summaryService;
    private List<PassengerSummary> passengerSummaries;
    private Consumer<String> mockPrintFunction;

    @BeforeEach
    public void setUp() {
        summaryService = new SummaryServiceImpl();
        passengerSummaries = new ArrayList<>();
        mockPrintFunction = mock(Consumer.class);
    }

    @Test
    public void testProcessSummary_ShouldGenerateAndPrintSummaryForEachPassengerSummary() {
        // Create sample passenger summaries
        PassengerSummary passengerSummary1 = createPassengerSummary("AIRPORT", 100, 20, createPassengerCountMap(50, 30, 20));
        PassengerSummary passengerSummary2 = createPassengerSummary("CENTRAL", 200, 40, createPassengerCountMap(100, 80, 20));
        passengerSummaries.add(passengerSummary1);
        passengerSummaries.add(passengerSummary2);

        // Call the processSummary method with the mock printFunction
        summaryService.processSummary(passengerSummaries, mockPrintFunction);

        // Verify that the printFunction was called twice with the expected summaries
        verify(mockPrintFunction, times(2)).accept(anyString());

        // Verify the contents of the printed summaries
        verifySummaryContents("TOTAL_COLLECTION AIRPORT 100 20\nPASSENGER_TYPE_SUMMARY\nADULT 50\nKID 30\nSENIOR_CITIZEN 20",
                0);
        verifySummaryContents("TOTAL_COLLECTION CENTRAL 200 40\nPASSENGER_TYPE_SUMMARY\nADULT 100\nKID 80\nSENIOR_CITIZEN 20",
                1);
    }

    // Helper method to verify the contents of the printed summary
    private void verifySummaryContents(String expectedSummary, int index) {
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPrintFunction, times(2)).accept(summaryCaptor.capture());
        List<String> printedSummaries = summaryCaptor.getAllValues();
        assertEquals(expectedSummary, printedSummaries.get(index));
    }

    private PassengerSummary createPassengerSummary(String destination, long totalAmountCollected,
            long returnAmountCollected, Map<Category, Integer> passengerCount) {
        PassengerSummary summary = new PassengerSummary();
        summary.setDestination(Destination.valueOf(destination));
        summary.setTotalAmountCollected(totalAmountCollected);
        summary.setReturnAmountCollected(returnAmountCollected);
        summary.setPassengerCount(new EnumMap<>(passengerCount));
        return summary;
    }

    private Map<Category, Integer> createPassengerCountMap(int adultCount, int childCount, int seniorCount) {
        Map<Category, Integer> passengerCount = new EnumMap<>(Category.class);
        passengerCount.put(Category.ADULT, adultCount);
        passengerCount.put(Category.KID, childCount);
        passengerCount.put(Category.SENIOR_CITIZEN, seniorCount);
        return passengerCount;
    }
}
