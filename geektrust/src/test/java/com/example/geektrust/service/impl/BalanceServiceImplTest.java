package com.example.geektrust.service.impl;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.dto.BalanceDto;
import com.example.geektrust.dto.CheckInDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;
import com.example.geektrust.service.PaymentService;

class BalanceServiceImplTest {

	@Mock
	private PaymentService paymentService;

	@InjectMocks
	private BalanceServiceImpl balanceService;
	
    private List<MetroCardDetails> metroCardDetails;
    
    private List<PassengerSummary> passengerSummaries;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		passengerSummaries = new ArrayList<>();
		metroCardDetails = new ArrayList<>();
		ReflectionTestUtils.setField(balanceService, "passengerSummaries", passengerSummaries);
        ReflectionTestUtils.setField(balanceService, "metroCardDetails", metroCardDetails);
	}

	// Test case to check the processBalance method with valid data
	@Test
	void testProcessBalance_ValidData() {
		String data = "BALANCE 1234567890 100";
		List<MetroCardDetails> result = balanceService.processBalance(data);
		
		// Assert that the list contains one element after processing
		Assertions.assertEquals(1, result.size());
		
		// Assert the details of the processed MetroCardDetails object
		MetroCardDetails cardDetails = result.get(0);
		Assertions.assertEquals("1234567890", cardDetails.getMetroCardId());
		Assertions.assertEquals(100L, cardDetails.getBalance().getBalance());
	}

	// Test case to check the processBalance method with invalid data
	@Test
	void testProcessBalance_InvalidData() {
		String data = "INVALID_DATA_FORMAT";
		List<MetroCardDetails> result = balanceService.processBalance(data);
		
		// Assert that the list remains empty after processing invalid data
		Assertions.assertEquals(0, result.size());
	}

	// Test case to check the checkBalance method
	@Test
	void testCheckBalance() {
		List<MetroCardDetails> metroCardDetails = new ArrayList<>();
		MetroCardDetails cardDetails = new MetroCardDetails();
		CheckInDto checkInDto = new CheckInDto(Category.ADULT, Destination.AIRPORT);
		cardDetails.setMetroCardId("1234567890");
		cardDetails.setBalance(new BalanceDto(100L));
		metroCardDetails.add(cardDetails);
		
		// Mock the paymentService methods for calculating amounts
		when(paymentService.calculateAmountCollected(anyLong(), anyLong(), anyBoolean())).thenReturn(50L);
		when(paymentService.calculateReturnCollected(anyLong(), anyBoolean())).thenReturn(10L);
		
		List<MetroCardDetails> result = balanceService.checkBalance(cardDetails, false, checkInDto);
		
		// Verify that the paymentService methods are called once
		verify(paymentService, times(1)).calculateAmountCollected(anyLong(), anyLong(), anyBoolean());
		verify(paymentService, times(1)).calculateReturnCollected(anyLong(), anyBoolean());
		
		// Assert that the list contains one element after processing
		Assertions.assertEquals(0, result.size());
	}
}