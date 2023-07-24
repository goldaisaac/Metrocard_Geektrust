package metro.app.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import metro.app.constants.AppConstants;
import metro.app.constants.Category;
import metro.app.constants.Destination;
import metro.app.dto.BalanceDto;
import metro.app.dto.CheckInDto;
import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;
import metro.app.service.BalanceService;
import metro.app.service.PaymentService;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {

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
		if (words.length >= 3) {
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
		long fare;

		switch (category) {
		case ADULT -> {
			fare = isReturn ? AppConstants.R_ADULT : AppConstants.ADULT;
			balance = updatePassengerSummary(passengerSummaries, category, fare, isReturn, balance, currentCheckInData);
			log.info("Updated balance for ADULT category: {}", balance);
		}
		case SENIOR_CITIZEN -> {
			fare = isReturn ? AppConstants.R_SENIOR_CITIZEN : AppConstants.SENIOR_CITIZEN;
			balance = updatePassengerSummary(passengerSummaries, category, fare, isReturn, balance, currentCheckInData);
			log.info("Updated balance for SENIOR_CITIZEN category: {}", balance);
		}
		case KID -> {
			fare = isReturn ? AppConstants.R_KID : AppConstants.KID;
			balance = updatePassengerSummary(passengerSummaries, category, fare, isReturn, balance, currentCheckInData);
			log.info("Updated balance for KID category: {}", balance);
		}
		default -> throw new IllegalArgumentException("Unexpected category: " + category);
		}

		for (MetroCardDetails cardDetail : metroCardDetails) {
			if (cardDetail.getMetroCardId().equals(metroCardId)) {
				cardDetail.setBalance(new BalanceDto(balance));
				log.info("Updated balance for metro card ID {}: {}", metroCardId, balance);
			}
		}
		return metroCardDetails;
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

		for (PassengerSummary passengerSummary : passengerSummaries) {
			log.info("Processing PassengerSummary for destination: {}", passengerSummary.getDestination());

			int counter = 0;
			if (currentCheckInData.getDestination().equals(passengerSummary.getDestination())) {
				log.info("Destination matches - destination: {}", currentCheckInData.getDestination());

				counter = currentCheckInData.getDestination().equals(Destination.AIRPORT) ? getAirportCounter(category)
						: getCentralCounter(category);
				log.info("Counter value - category: {}, counter: {}", category, counter);

				passengerSummary.getPassengerCount().put(category, counter);
				log.info("Updated passenger count - category: {}, count: {}", category, counter);

				passengerSummary.setTotalAmountCollected(passengerSummary.getTotalAmountCollected() + amountCollected);
				log.info("Updated total amount collected - current: {}, new: {}",
						passengerSummary.getTotalAmountCollected() - amountCollected,
						passengerSummary.getTotalAmountCollected());

				passengerSummary
						.setReturnAmountCollected(passengerSummary.getReturnAmountCollected() + returnCollected);
				log.info("Updated return amount collected - current: {}, new: {}",
						passengerSummary.getReturnAmountCollected() - returnCollected,
						passengerSummary.getReturnAmountCollected());
			}
		}

		return balance;
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