package com.example.geektrust.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geektrust.constants.AppConstants;
import com.example.geektrust.constants.Category;
import com.example.geektrust.constants.Destination;
import com.example.geektrust.dto.BalanceDto;
import com.example.geektrust.dto.CheckInDto;
import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;
import com.example.geektrust.service.BalanceService;
import com.example.geektrust.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {

	private static final int DELIMITER_BALANCE_3 = 3;

	@Autowired
	PaymentService paymentService;

	private Map<Category, Integer> centralCounters = new EnumMap<>(Category.class);
	private Map<Category, Integer> airportCounters = new EnumMap<>(Category.class);

	/**
	 * Processes the balance information and updates the metro card details.
	 * 
	 * @param metroCardDetails The list of metro card details.
	 * @param data             The balance data.
	 * @return The updated list of metro card details.
	 */
	@Override
	public List<MetroCardDetails> processBalance(List<MetroCardDetails> metroCardDetails, String data) {
		log.info("Processing balance with data: {}", data);
		String[] words = data.split(" ");
		MetroCardDetails cardDetails = new MetroCardDetails();
		if (words.length >= DELIMITER_BALANCE_3) {
			cardDetails.setMetroCardId(words[1]);
			cardDetails.setBalance(new BalanceDto(Long.parseLong(words[2])));
			log.info("Created MetroCardDetails with cardId: {} and amount: {}", words[1], words[2]);
			log.info("Added MetroCardDetails to metroCardDetails list");
			metroCardDetails.add(cardDetails);
		} else {
			log.warn("Invalid data format: {}", data);
		}
		return metroCardDetails;
	}

	/**
	 * Checks the balance, performs the necessary calculations, and updates the
	 * passenger summaries and metro card details.
	 * 
	 * @param metroCardDetails   The list of metro card details.
	 * @param cardDetails        The metro card details.
	 * @param isReturn           Indicates if it is a return journey.
	 * @param currentCheckInData The check-in data.
	 * @param passengerSummaries The list of passenger summaries.
	 * @return The updated list of metro card details.
	 */
	@Override
	public List<MetroCardDetails> checkBalance(List<MetroCardDetails> metroCardDetails, MetroCardDetails cardDetails,
			boolean isReturn, CheckInDto currentCheckInData, List<PassengerSummary> passengerSummaries) {

		long balance = cardDetails.getBalance().getBalance();
		Category category = currentCheckInData.getCategory();
		String metroCardId = cardDetails.getMetroCardId();

		// Calculate the fare based on the category and whether it is a return trip
		long fare = calculateFare(category, isReturn);

		// Update the passenger balance based on the calculated fare
		balance = updatePassengerSummary(passengerSummaries, category, fare, isReturn, balance, currentCheckInData);
		log.info("Updated balance for {} category: {}", category, balance);

		// Update the balance for the metro card with the calculated balance
		updateMetroCardBalance(metroCardDetails, metroCardId, balance);

		// Return the updated list of metro card details
		return metroCardDetails;
	}

	/**
	 * Calculates the fare based on the category and whether it is a return trip.
	 *
	 * @param category The category of the passenger (ADULT, SENIOR_CITIZEN, or
	 *                 KID).
	 * @param isReturn A boolean flag indicating whether it is a return trip.
	 * @return The calculated fare based on the category and return trip status.
	 */
	private long calculateFare(Category category, boolean isReturn) {
		switch (category) {
		case ADULT:
			return isReturn ? AppConstants.R_ADULT : AppConstants.ADULT;
		case SENIOR_CITIZEN:
			return isReturn ? AppConstants.R_SENIOR_CITIZEN : AppConstants.SENIOR_CITIZEN;
		case KID:
			return isReturn ? AppConstants.R_KID : AppConstants.KID;
		default:
			throw new IllegalArgumentException("Unexpected category: " + category);
		}
	}

	/**
	 * Updates the balance for the metro card with the calculated balance.
	 *
	 * @param metroCardDetails The list of metro card details containing the metro
	 *                         card to be updated.
	 * @param metroCardId      The ID of the metro card to be updated.
	 * @param balance          The calculated balance to be updated for the metro
	 *                         card.
	 */
	private void updateMetroCardBalance(List<MetroCardDetails> metroCardDetails, String metroCardId, long balance) {
		for (MetroCardDetails cardDetail : metroCardDetails) {
			if (cardDetail.getMetroCardId().equals(metroCardId)) {
				cardDetail.setBalance(new BalanceDto(balance));
				log.info("Updated balance for metro card ID {}: {}", metroCardId, balance);
			}
		}
	}

	/**
	 * Updates the passenger summaries based on the category, fare, and check-in
	 * data.
	 * 
	 * @param passengerSummaries The list of passenger summaries.
	 * @param category           The category.
	 * @param fare               The fare amount.
	 * @param isReturn           Indicates if it is a return journey.
	 * @param balance            The balance amount.
	 * @param currentCheckInData The check-in data.
	 * @return The updated balance amount.
	 */
	private long updatePassengerSummary(List<PassengerSummary> passengerSummaries, Category category, long fare,
			boolean isReturn, long balance, CheckInDto currentCheckInData) {
		long amountCollected = paymentService.calculateAmountCollected(balance, fare, isReturn);
		log.info("Amount collected: {}", amountCollected);

		long returnCollected = paymentService.calculateReturnCollected(fare, isReturn);
		log.info("Return collected: {}", returnCollected);

		balance = paymentService.updateBalance(balance, amountCollected);
		log.info("Updated balance: {}", balance);

		updatePassengerSummaries(passengerSummaries, category, amountCollected, returnCollected, currentCheckInData);

		return balance;
	}

	private void updatePassengerSummaries(List<PassengerSummary> passengerSummaries, Category category,
			long amountCollected, long returnCollected, CheckInDto currentCheckInData) {
		Destination currentDestination = currentCheckInData.getDestination();
		int counter = 0;

		for (PassengerSummary passengerSummary : passengerSummaries) {
			log.info("Processing PassengerSummary for destination: {}", passengerSummary.getDestination());

			if (currentDestination.equals(passengerSummary.getDestination())) {
				log.info("Destination matches - destination: {}", currentDestination);

				counter = currentDestination.equals(Destination.AIRPORT) ? getAirportCounter(category)
						: getCentralCounter(category);
				log.info("Counter value - category: {}, counter: {}", category, counter);

				updatePassengerSummaryCount(passengerSummary, category, counter);
				updatePassengerSummaryAmountAndReturnAmount(passengerSummary, amountCollected, returnCollected);

			}
		}
	}

	// Method to update passenger summary count
	private void updatePassengerSummaryCount(PassengerSummary passengerSummary, Category category, int counter) {
		passengerSummary.getPassengerCount().put(category, counter);
		log.info("Updated passenger count - category: {}, count: {}", category, counter);
	}

	// Method to update passenger summary amount and return amount
	private void updatePassengerSummaryAmountAndReturnAmount(PassengerSummary passengerSummary, long amountCollected,
			long returnCollected) {
		passengerSummary.setTotalAmountCollected(passengerSummary.getTotalAmountCollected() + amountCollected);
		log.info("Updated total amount collected - current: {}, new: {}",
				passengerSummary.getTotalAmountCollected() - amountCollected,
				passengerSummary.getTotalAmountCollected());

		passengerSummary.setReturnAmountCollected(passengerSummary.getReturnAmountCollected() + returnCollected);
		log.info("Updated return amount collected - current: {}, new: {}",
				passengerSummary.getReturnAmountCollected() - returnCollected,
				passengerSummary.getReturnAmountCollected());
	}

	/**
	 * Gets the central counter value based on the category.
	 * 
	 * @param category The category.
	 * @return The updated central counter value.
	 */
	private int getCentralCounter(Category category) {
		centralCounters.putIfAbsent(category, 0);
		return centralCounters.compute(category, (key, value) -> value + 1);
	}

	/**
	 * Gets the airport counter value based on the category.
	 * 
	 * @param category The category.
	 * @return The updated airport counter value.
	 */
	private int getAirportCounter(Category category) {
		airportCounters.putIfAbsent(category, 0);
		return airportCounters.compute(category, (key, value) -> value + 1);
	}

}