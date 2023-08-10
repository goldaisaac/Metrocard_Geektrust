package com.example.geektrust.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.geektrust.constants.AppConstants;
import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.dto.BalanceDto;
import com.example.geektrust.dto.CheckInDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;
import com.example.geektrust.service.PaymentService;

public class BalanceServiceImpl1Test {

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Mock
    private PaymentService mockPaymentService;

    private List<MetroCardDetails> metroCardDetails;
    
    private List<PassengerSummary> passengerSummaries;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        metroCardDetails = new ArrayList<>();
        passengerSummaries = new ArrayList<>();
        ReflectionTestUtils.setField(balanceService, "passengerSummaries", passengerSummaries);
        ReflectionTestUtils.setField(balanceService, "metroCardDetails", metroCardDetails);
    }

    @Test
    public void testProcessBalance_withValidData() {
        String data = "BALANCE cardId 100";
        metroCardDetails = balanceService.processBalance(data);
        
        assertFalse(metroCardDetails.isEmpty());
        assertEquals(1, metroCardDetails.size());
        MetroCardDetails cardDetails = metroCardDetails.get(0);
        assertEquals("cardId", cardDetails.getMetroCardId());
        assertEquals(100, cardDetails.getBalance().getBalance());
    }

    @Test
    public void testProcessBalance_withInvalidData() {
        String data = "INVALID_FORMAT";
        metroCardDetails = balanceService.processBalance(data);
        
        assertTrue(metroCardDetails.isEmpty());
    }

    @Test
    public void testCheckBalance() throws Exception {
        String metroCardId = "cardId";
        Category category = Category.ADULT;
        boolean isReturn = false;
        long balance = 100L;
        CheckInDto currentCheckInData = new CheckInDto(category, Destination.AIRPORT);

        MetroCardDetails cardDetails = new MetroCardDetails();
        cardDetails.setMetroCardId(metroCardId);
        cardDetails.setBalance(new BalanceDto(balance));
        metroCardDetails.add(cardDetails);

     // Initialize passengerSummaries
        PassengerSummary airportPassengerSummary = new PassengerSummary();
        airportPassengerSummary.setDestination(Destination.AIRPORT);
        airportPassengerSummary.setPassengerCount(new EnumMap<>(Category.class));
        passengerSummaries.add(airportPassengerSummary);
        
        PassengerSummary passengerSummary = new PassengerSummary();
        passengerSummary.setDestination(Destination.AIRPORT);
        metroCardDetails.add(cardDetails);

        long amountCollected = 5L;
        long returnCollected = 2L;
        long updatedBalance = 93L;

        Method calculateFareMethod = BalanceServiceImpl.class.getDeclaredMethod("calculateFare", Category.class, boolean.class);
        calculateFareMethod.setAccessible(true);
        long calculatedFare = (long) calculateFareMethod.invoke(balanceService, category, isReturn);

        Method calculateAmountCollectedMethod = PaymentService.class.getDeclaredMethod("calculateAmountCollected", long.class, long.class, boolean.class);
        calculateAmountCollectedMethod.setAccessible(true);
        when(mockPaymentService.calculateAmountCollected(eq(balance), eq(calculatedFare), eq(isReturn))).thenReturn(amountCollected);
        long calculatedAmountCollected = (long) calculateAmountCollectedMethod.invoke(mockPaymentService, balance, calculatedFare, isReturn);

        Method calculateReturnCollectedMethod = PaymentService.class.getDeclaredMethod("calculateReturnCollected", long.class, boolean.class);
        calculateReturnCollectedMethod.setAccessible(true);
        when(mockPaymentService.calculateReturnCollected(eq(calculatedFare), eq(isReturn))).thenReturn(returnCollected);
        calculateReturnCollectedMethod.invoke(mockPaymentService, calculatedFare, isReturn);

        Method updateBalanceMethod = PaymentService.class.getDeclaredMethod("updateBalance", long.class, long.class);
        updateBalanceMethod.setAccessible(true);
        when(mockPaymentService.updateBalance(eq(balance), eq(calculatedAmountCollected))).thenReturn(updatedBalance);
        updateBalanceMethod.invoke(mockPaymentService, balance, calculatedAmountCollected);

        metroCardDetails = balanceService.checkBalance(cardDetails, isReturn, currentCheckInData);

        assertFalse(metroCardDetails.isEmpty());
        assertEquals(updatedBalance, metroCardDetails.get(0).getBalance().getBalance());
        assertEquals(1, passengerSummaries.size());
        PassengerSummary updatedSummary = passengerSummaries.get(0);
        assertEquals(Destination.AIRPORT, updatedSummary.getDestination());
        assertEquals(1, updatedSummary.getPassengerCount().get(category));
        assertEquals(amountCollected, updatedSummary.getTotalAmountCollected());
        assertEquals(returnCollected, updatedSummary.getReturnAmountCollected());
    }


    @Test
    public void testCalculateFare() throws Exception {
        Method calculateFareMethod = BalanceServiceImpl.class.getDeclaredMethod("calculateFare", Category.class, boolean.class);
        calculateFareMethod.setAccessible(true);

        // Test for ADULT category and no return trip
        long adultFare = (long) calculateFareMethod.invoke(balanceService, Category.ADULT, false);
        assertEquals(AppConstants.ADULT, adultFare);

        // Test for SENIOR_CITIZEN category and return trip
        long seniorCitizenReturnFare = (long) calculateFareMethod.invoke(balanceService, Category.SENIOR_CITIZEN, true);
        assertEquals(AppConstants.R_SENIOR_CITIZEN, seniorCitizenReturnFare);

        // Test for KID category and no return trip
        long kidFare = (long) calculateFareMethod.invoke(balanceService, Category.KID, false);
        assertEquals(AppConstants.KID, kidFare);
    }

    @Test
    public void testUpdateMetroCardBalance() throws Exception {
        List<MetroCardDetails> metroCardDetails = new ArrayList<>();
        String metroCardId = "12345";
        long balance = 500;

        MetroCardDetails cardDetails = new MetroCardDetails();
        cardDetails.setMetroCardId(metroCardId);
        cardDetails.setBalance(new BalanceDto(balance));

        metroCardDetails.add(cardDetails);

        Method updateMetroCardBalanceMethod = BalanceServiceImpl.class.getDeclaredMethod("updateMetroCardBalance", List.class, String.class, long.class);
        updateMetroCardBalanceMethod.setAccessible(true);

        long newBalance = 300;
        updateMetroCardBalanceMethod.invoke(balanceService, metroCardDetails, metroCardId, newBalance);

        assertEquals(newBalance, metroCardDetails.get(0).getBalance().getBalance());
    }
    
    @Test
    public void testUpdatePassengerSummaries() throws Exception {
        PassengerSummary airportPassengerSummary = new PassengerSummary();
        airportPassengerSummary.setDestination(Destination.AIRPORT);
        airportPassengerSummary.setPassengerCount(new EnumMap<>(Category.class));
        passengerSummaries.add(airportPassengerSummary);

        ReflectionTestUtils.setField(balanceService, "passengerSummaries", passengerSummaries);
        ReflectionTestUtils.setField(balanceService, "metroCardDetails", metroCardDetails);

        PassengerSummary centralPassengerSummary = new PassengerSummary();
        centralPassengerSummary.setDestination(Destination.CENTRAL);
        centralPassengerSummary.setPassengerCount(new EnumMap<>(Category.class));
        passengerSummaries.add(centralPassengerSummary);

        CheckInDto checkInData1 = new CheckInDto(Category.ADULT, Destination.AIRPORT);
        CheckInDto checkInData2 = new CheckInDto(Category.KID, Destination.AIRPORT);
        CheckInDto checkInData3 = new CheckInDto(Category.ADULT, Destination.CENTRAL);

        int initialPassengerCount = 0;

        // Update passenger summaries for an adult category, airport destination, and non-return trip
        ReflectionTestUtils.invokeMethod(balanceService, "updatePassengerSummaries", Category.ADULT, 100l, 0l, checkInData1);

        assertEquals(1, passengerSummaries.get(0).getPassengerCount().get(Category.ADULT).intValue());
        assertEquals(100, passengerSummaries.get(0).getTotalAmountCollected());
        assertEquals(initialPassengerCount, passengerSummaries.get(0).getReturnAmountCollected());

        // Update passenger summaries for a kid category, airport destination, and return trip
        ReflectionTestUtils.invokeMethod(balanceService, "updatePassengerSummaries", Category.KID, 50l, 30l, checkInData2);

        assertEquals(1, passengerSummaries.get(0).getPassengerCount().get(Category.KID).intValue());
        assertEquals(150, passengerSummaries.get(0).getTotalAmountCollected());
        assertEquals(30, passengerSummaries.get(0).getReturnAmountCollected());

        // Update passenger summaries for an adult category, central destination, and non-return trip
        ReflectionTestUtils.invokeMethod(balanceService, "updatePassengerSummaries", Category.ADULT, 120l, 0l, checkInData3);

        assertEquals(1, passengerSummaries.get(1).getPassengerCount().get(Category.ADULT).intValue());
        assertEquals(120, passengerSummaries.get(1).getTotalAmountCollected());
        assertEquals(initialPassengerCount, passengerSummaries.get(1).getReturnAmountCollected());
    }



}
