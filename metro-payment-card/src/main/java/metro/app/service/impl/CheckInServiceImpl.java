package metro.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import metro.app.constants.AppConstants;
import metro.app.constants.Category;
import metro.app.constants.Destination;
import metro.app.dto.CheckInDto;
import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;
import metro.app.service.BalanceService;
import metro.app.service.CheckInService;
import metro.app.service.PaymentService;
import metro.app.service.SummaryService;

@Slf4j
@Service
public class CheckInServiceImpl implements CheckInService {

	@Autowired
	PaymentService paymentService;

	@Autowired
	BalanceService balanceService;

	@Autowired
	SummaryService summaryService;

	Map<String, Map<String, String>> prevTravelType = new HashMap<>();

	@Override
	/**
	 * Performs the check-in operation for a MetroCard based on the provided data.
	 * Updates the check-in details for the MetroCard and invokes the necessary
	 * processing.
	 *
	 * @param metroCardDetails   The list of MetroCard details.
	 * @param passengerSummaries The list of passenger summaries.
	 * @param data               The check-in data.
	 * @return The updated list of MetroCard details.
	 */
	public List<MetroCardDetails> checkIn(List<MetroCardDetails> metroCardDetails,
			List<PassengerSummary> passengerSummaries, String data) {

		log.info("Performing check-in with data: {}", data);

		String[] words = data.split(" ");
		if (words.length >= 4) {
			String cardId = words[1];
			String category = words[2];
			String destination = words[3];

			Optional<MetroCardDetails> optionalCardDetails = metroCardDetails.stream()
					.filter(cardDetails -> cardDetails.getMetroCardId().equals(cardId)).findFirst();

			optionalCardDetails.ifPresent(cardDetails -> {
				CheckInDto checkInDto = new CheckInDto(Category.valueOf(category.toUpperCase()),
						Destination.valueOf(destination.toUpperCase()));
				List<CheckInDto> checkInDetails = Optional.ofNullable(cardDetails.getCheckInDetails()).orElseGet(() -> {
					List<CheckInDto> newCheckInDetails = new ArrayList<>();
					cardDetails.setCheckInDetails(newCheckInDetails);
					return newCheckInDetails;
				});

				checkInDetails.add(checkInDto);
				log.info("Added check-in details to MetroCardDetails with cardId: {}, category: {}, destination: {}",
						cardId, category, destination);

				processInput(metroCardDetails, cardDetails, checkInDto, passengerSummaries);
				log.info("Processed input for MetroCardDetails with cardId: {}", cardId);

			});
		} else {
			log.warn("Invalid data format: {}", data);
		}

		return metroCardDetails;
	}

	/**
	 * Processes the input data for a MetroCard and performs the necessary balance
	 * checks based on the check-in details. Updates the previous travel type for
	 * the MetroCard and invokes the balance check service accordingly.
	 *
	 * @param metroCardDetails   The list of MetroCard details.
	 * @param cardDetails        The MetroCard details for which the input is being
	 *                           processed.
	 * @param currentCheckInData The current check-in data.
	 * @param passengerSummaries The list of passenger summaries.
	 */
	public void processInput(List<MetroCardDetails> metroCardDetails, MetroCardDetails cardDetails,
			CheckInDto currentCheckInData, List<PassengerSummary> passengerSummaries) {

		String metroCardId = cardDetails.getMetroCardId();
		String currentDestination = currentCheckInData.getDestination().toString();

		log.info("Processing input for MetroCardDetails with cardId: {}", metroCardId);

		// Check if the card has only one check-in and is not empty
		if (cardDetails.getCheckInDetails().size() == 1 && !cardDetails.getCheckInDetails().isEmpty()) {
			log.info("Performing balance check for one-way trip - cardId: {}", metroCardId);

			// Update the previous travel type and perform balance check for one-way trip
			prevTravelType.put(metroCardId, Map.of(AppConstants.ONE_WAY, currentDestination));
			balanceService.checkBalance(metroCardDetails, cardDetails, false, currentCheckInData, passengerSummaries);
		} else {

			// Retrieve the previous travel type map for the card
			Map<String, String> map = prevTravelType.get(metroCardId);
			String previousDestination = map.getOrDefault(AppConstants.ONE_WAY, "");

			log.info("Previous destination for MetroCardDetails with cardId: {} is: {}", metroCardId,
					previousDestination);

			// Check if the current destination is different from the previous destination
			// and there is no return travel recorded for the card
			if (!previousDestination.equals(currentDestination)
					&& !prevTravelType.get(metroCardId).containsKey(AppConstants.RETURN)) {

				log.info("Performing balance check for return trip - cardId: {}", metroCardId);

				// Update the previous travel type and perform balance check for return trip
				prevTravelType.put(metroCardId, Map.of(AppConstants.RETURN, currentDestination));
				balanceService.checkBalance(metroCardDetails, cardDetails, true, currentCheckInData,
						passengerSummaries);
			} else {

				log.info("Performing balance check for one-way trip - cardId: {}", metroCardId);

				// Update the previous travel type and perform balance check for one-way trip
				prevTravelType.put(metroCardId, Map.of(AppConstants.ONE_WAY, currentDestination));
				balanceService.checkBalance(metroCardDetails, cardDetails, false, currentCheckInData,
						passengerSummaries);
			}
		}
	}
}