package metro.app.service;

import java.util.List;

import metro.app.dto.CheckInDto;
import metro.app.dto.MetroCardDetails;
import metro.app.response.PassengerSummary;

public interface BalanceService {

	List<MetroCardDetails> processBalance(List<MetroCardDetails> metroCardDetails, String data);

	List<MetroCardDetails> checkBalance(List<MetroCardDetails> metroCardDetails, MetroCardDetails cardDetails,
			boolean isReturn, CheckInDto currentCheckInData, List<PassengerSummary> passengerSummaries);

}
