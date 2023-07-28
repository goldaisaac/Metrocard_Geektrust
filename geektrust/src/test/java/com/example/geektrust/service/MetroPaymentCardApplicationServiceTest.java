package com.example.geektrust.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private List<MetroCardDetails> mockMetroCardDetails;
    
    private List<PassengerSummary> mockPassengerSummaries;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        MetroCardDetails metroCardDetails = new MetroCardDetails();
        metroCardDetails.setMetroCardId("12345");
        metroCardDetails.setBalance(new BalanceDto(100L));
        mockMetroCardDetails = Collections.singletonList(metroCardDetails);
        mockPassengerSummaries = new ArrayList<>();
        metroPaymentCardApplicationService = new MetroPaymentCardApplicationService(mockMetroCardDetails, mockPassengerSummaries);
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

        verify(mockCheckInService, never()).checkIn(any(), any(), any());
        verify(mockBalanceService, never()).processBalance(any(), any());
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

        verify(mockCheckInService, never()).checkIn(any(), any(), any());
        verify(mockBalanceService, never()).processBalance(any(), any());
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

        verify(mockCheckInService, never()).checkIn(any(), any(), any());
        verify(mockBalanceService, never()).processBalance(any(), any());
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

        verify(mockCheckInService, never()).checkIn(any(), any(), any());
        verify(mockBalanceService, never()).processBalance(any(), any());
        verify(mockSummaryService, never()).processSummary(any(), any());

        // Reset System.in
        System.setIn(sysInBackup);
    }


}

