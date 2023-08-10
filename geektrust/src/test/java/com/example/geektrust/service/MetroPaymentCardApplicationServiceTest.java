package com.example.geektrust.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.dto.BalanceDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;

public class MetroPaymentCardApplicationServiceTest {

    @InjectMocks
    private MetroPaymentCardApplicationService metroPaymentCardApplicationService;

    @Mock
    private CheckInService mockCheckInService;

    @Mock
    private BalanceService mockBalanceService;

    @Mock
    private SummaryService mockSummaryService;
    
    private List<PassengerSummary> passengerSummaries;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        MetroCardDetails metroCardDetails = new MetroCardDetails();
        metroCardDetails.setMetroCardId("12345");
        metroCardDetails.setBalance(new BalanceDto(100L));
        Collections.singletonList(metroCardDetails);
        metroPaymentCardApplicationService = new MetroPaymentCardApplicationService();
        passengerSummaries = new ArrayList<>();
        passengerSummaries = new ArrayList<>();
        for (Destination destination : Destination.values()) {
            PassengerSummary passengerSummary = new PassengerSummary();
            passengerSummary.setDestination(destination);
            passengerSummary.setPassengerCount(new EnumMap<>(Category.class));
            passengerSummaries.add(passengerSummary);
        }
        ReflectionTestUtils.setField(metroPaymentCardApplicationService, "passengerSummaries", passengerSummaries);
    }

    @Test
    public void testRun_InvalidInput_ShouldNotInvokeServices() {
        String input = "invalid_input";
        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        try {
            metroPaymentCardApplicationService.run();
        } catch (Exception e) {
            // Do nothing
        }

        verify(mockCheckInService, never()).checkIn(any());
        verify(mockBalanceService, never()).processBalance(any());
        verify(mockSummaryService, never()).processSummary(any(), any());

        // Reset System.in
        System.setIn(sysInBackup);
    }
    
    @Test
    public void testRun_EmptyInputFile_ShouldNotInvokeServices() {
        String input = ""; // Empty input file
        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        try {
            metroPaymentCardApplicationService.run();
        } catch (Exception e) {
            // Do nothing
        }

        verify(mockCheckInService, never()).checkIn(any());
        verify(mockBalanceService, never()).processBalance(any());
        verify(mockSummaryService, never()).processSummary(any(), any());

        // Reset System.in
        System.setIn(sysInBackup);
    }

    @Test
    public void testRun_UnexpectedCommand() throws Exception {
        String input = "INVALID_COMMAND";
        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        try {
            metroPaymentCardApplicationService.run();
        } catch (Exception e) {
            // Do nothing
        }

        verify(mockCheckInService, never()).checkIn(any());
        verify(mockBalanceService, never()).processBalance(any());
        verify(mockSummaryService, never()).processSummary(any(), any());

        // Reset System.in
        System.setIn(sysInBackup);
    }

    @Test
    public void testRun_FileNotFoundOrEmpty() throws Exception {
        String input = "file_not_found.txt";
        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        try {
            metroPaymentCardApplicationService.run();
        } catch (Exception e) {
            // Do nothing
        }

        verify(mockCheckInService, never()).checkIn(any());
        verify(mockBalanceService, never()).processBalance(any());
        verify(mockSummaryService, never()).processSummary(any(), any());

        // Reset System.in
        System.setIn(sysInBackup);
    }
    
    @Test
    public void testRun_PrintSummaryCommand_CallsSummaryService() throws Exception {

        ReflectionTestUtils.setField(metroPaymentCardApplicationService, "balanceService", mockBalanceService);
        ReflectionTestUtils.setField(metroPaymentCardApplicationService, "summaryService", mockSummaryService);
        ReflectionTestUtils.setField(metroPaymentCardApplicationService, "checkInService", mockCheckInService);
        
        // Arrange
        String fileName = "src/test/resources/input1.txt";
        List<String> inputData = new ArrayList<>();
        inputData.add("PRINT_SUMMARY");

        ReflectionTestUtils.invokeMethod(metroPaymentCardApplicationService, "processInputFile", fileName);
        // Assert
        verify(mockSummaryService).processSummary(eq(passengerSummaries), any());
    }


}

