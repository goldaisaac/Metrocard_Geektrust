package com.example.geektrust.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.dto.BalanceDto;
import com.example.geektrust.dto.CheckInDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;
import com.example.geektrust.service.BalanceService;

public class CheckInServiceImplTest {

    private CheckInServiceImpl checkInService;
    private List<MetroCardDetails> metroCardDetails;
    private List<PassengerSummary> passengerSummaries;
    private BalanceService mockBalanceService;

    @BeforeEach
    public void setUp() {
        checkInService = new CheckInServiceImpl();
        metroCardDetails = new ArrayList<>();
        passengerSummaries = new ArrayList<>();
        mockBalanceService = mock(BalanceService.class);
        checkInService.balanceService = mockBalanceService; // Inject the mockBalanceService into the CheckInServiceImpl
        checkInService.prevTravelType = new HashMap<>();
    }

    @Test
    public void testCheckIn_ShouldAddCheckInDetailsAndProcessInputForOneWayTrip() {
        // Create sample MetroCard details
        MetroCardDetails cardDetails = createMetroCardDetails("12345", 100L);
        metroCardDetails.add(cardDetails);

        // Call the checkIn method with valid check-in data for a one-way trip
        String checkInData = "CHECK_IN 12345 ADULT AIRPORT";
        checkInService.checkIn(metroCardDetails, passengerSummaries, checkInData);

        // Verify that check-in details were added to the MetroCard and balanceService was called with the correct arguments
        assertEquals(1, cardDetails.getCheckInDetails().size());
        CheckInDto checkInDto = cardDetails.getCheckInDetails().get(0);
        assertEquals(Category.ADULT, checkInDto.getCategory());
        assertEquals(Destination.AIRPORT, checkInDto.getDestination());

        verify(mockBalanceService, times(1)).checkBalance(any(), any(), anyBoolean(), any(), any());
    }

    @Test
    public void testCheckIn_ShouldAddCheckInDetailsAndProcessInputForReturnTrip() {
        // Create sample MetroCard details
        MetroCardDetails cardDetails = createMetroCardDetails("12345", 200L);
        metroCardDetails.add(cardDetails);

        // Call the checkIn method with valid check-in data for a return trip
        String checkInData1 = "CHECK_IN 12345 ADULT AIRPORT";
        String checkInData2 = "CHECK_IN 12345 ADULT CENTRAL";
        checkInService.checkIn(metroCardDetails, passengerSummaries, checkInData1);
        checkInService.checkIn(metroCardDetails, passengerSummaries, checkInData2);

        // Verify that check-in details were added to the MetroCard and balanceService was called twice with the correct arguments
        assertEquals(2, cardDetails.getCheckInDetails().size());
        CheckInDto checkInDto1 = cardDetails.getCheckInDetails().get(0);
        CheckInDto checkInDto2 = cardDetails.getCheckInDetails().get(1);
        assertEquals(Category.ADULT, checkInDto1.getCategory());
        assertEquals(Destination.AIRPORT, checkInDto1.getDestination());
        assertEquals(Category.ADULT, checkInDto2.getCategory());
        assertEquals(Destination.CENTRAL, checkInDto2.getDestination());

        verify(mockBalanceService, times(2)).checkBalance(any(), any(), anyBoolean(), any(), any());
    }

    @Test
    public void testCheckIn_ShouldNotAddCheckInDetailsForInvalidCardId() {
        // Create sample MetroCard details
        MetroCardDetails cardDetails = createMetroCardDetails("12345", 200L);
        metroCardDetails.add(cardDetails);
        cardDetails.setCheckInDetails(new ArrayList<CheckInDto>());
        // Call the checkIn method with invalid check-in data
        String checkInData = "CHECK_IN 98765 ADULT AIRPORT";
        checkInService.checkIn(metroCardDetails, passengerSummaries, checkInData);

        // Verify that no check-in details were added to the MetroCard and balanceService was not called
        assertEquals(0, cardDetails.getCheckInDetails().size());
        verify(mockBalanceService, never()).checkBalance(any(), any(), anyBoolean(), any(), any());
    }

    private MetroCardDetails createMetroCardDetails(String metroCardId, long balance) {
        MetroCardDetails cardDetails = new MetroCardDetails();
        cardDetails.setMetroCardId(metroCardId);
        cardDetails.setBalance(new BalanceDto(balance));
        return cardDetails;
    }
}
