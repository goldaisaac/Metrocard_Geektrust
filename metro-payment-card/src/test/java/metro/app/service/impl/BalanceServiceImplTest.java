package metro.app.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import metro.app.constants.Category;
import metro.app.constants.Destination;
import metro.app.dto.BalanceDto;
import metro.app.dto.CheckInDto;
import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;
import metro.app.service.PaymentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class BalanceServiceImplTest {

	@Mock
	private PaymentService paymentService;

	@InjectMocks
	private BalanceServiceImpl balanceService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProcessBalance_ValidData() {
		List<MetroCardDetails> metroCardDetails = new ArrayList<>();
		String data = "BALANCE 1234567890 100";

		List<MetroCardDetails> result = balanceService.processBalance(metroCardDetails, data);

		// Assert that the metroCardDetails list contains one element
		Assertions.assertEquals(1, result.size());

		// Assert that the card details are correctly set
		MetroCardDetails cardDetails = result.get(0);
		Assertions.assertEquals("1234567890", cardDetails.getMetroCardId());
		Assertions.assertEquals(100L, cardDetails.getBalance().getBalance());
	}

	@Test
	void testProcessBalance_InvalidData() {
		List<MetroCardDetails> metroCardDetails = new ArrayList<>();
		String data = "INVALID_DATA_FORMAT";

		List<MetroCardDetails> result = balanceService.processBalance(metroCardDetails, data);

		// Assert that the metroCardDetails list is still empty
		Assertions.assertEquals(0, result.size());
	}

	@Test
	void testCheckBalance() {
		List<MetroCardDetails> metroCardDetails = new ArrayList<>();
		List<PassengerSummary> passengerSummaries = new ArrayList<>();
		MetroCardDetails cardDetails = new MetroCardDetails();
		CheckInDto checkInDto = new CheckInDto(Category.ADULT, Destination.AIRPORT);

		cardDetails.setMetroCardId("1234567890");
		cardDetails.setBalance(new BalanceDto(100L));

		metroCardDetails.add(cardDetails);

		// Mock the behavior of the paymentService.calculateAmountCollected() method
		when(paymentService.calculateAmountCollected(anyLong(), anyLong(), anyBoolean())).thenReturn(50L);

		// Mock the behavior of the paymentService.calculateReturnCollected() method
		when(paymentService.calculateReturnCollected(anyLong(), anyBoolean())).thenReturn(10L);

		// Call the method to be tested
		List<MetroCardDetails> result = balanceService.checkBalance(metroCardDetails, cardDetails, false, checkInDto,
				passengerSummaries);

		// Verify that the paymentService.calculateAmountCollected() method was called
		// once
		verify(paymentService, times(1)).calculateAmountCollected(anyLong(), anyLong(), anyBoolean());

		// Verify that the paymentService.calculateReturnCollected() method was called
		// once
		verify(paymentService, times(1)).calculateReturnCollected(anyLong(), anyBoolean());

		Assertions.assertEquals(1, result.size());
	}

}
